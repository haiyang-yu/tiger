package org.tiger.core.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.*;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.epoll.EpollServerSocketChannel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.util.concurrent.DefaultThreadFactory;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.tiger.api.listener.Listener;
import org.tiger.api.service.BaseService;
import org.tiger.core.codec.PacketDecoder;
import org.tiger.core.codec.PacketEncoder;
import org.tiger.tools.common.ThreadName;
import org.tiger.tools.exception.ServiceException;
import org.tiger.tools.utils.OsUtil;

import java.net.InetSocketAddress;
import java.nio.channels.spi.SelectorProvider;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicReference;

/**
 * {@link AbstractNettyTcpService}
 *
 * @author SongQingWei
 * @since 1.0.0
 * 2020-01-08 13:58 周三
 */
@Slf4j
public abstract class AbstractNettyTcpService extends BaseService {

    private final AtomicReference<State> serverState = new AtomicReference<>(State.CREATED);

    protected final String host;
    protected final int port;
    protected EventLoopGroup bossGroup;
    protected EventLoopGroup workerGroup;

    public AbstractNettyTcpService(int port) {
        this(null, port);
    }

    public AbstractNettyTcpService(String host, int port) {
        this.host = host;
        this.port = port;
    }

    @Override
    public void init() {
        if (!serverState.compareAndSet(State.CREATED, State.INITIALIZED)) {
            throw new ServiceException("Server already init");
        }
    }

    @Override
    public boolean isRunning() {
        return serverState.get() == State.STARTED;
    }

    @Override
    protected void doStart(Listener listener) {
        if (!serverState.compareAndSet(State.INITIALIZED, State.STARTING)) {
            throw new ServiceException("Server already started or have not init");
        }
        if (useNettyEpoll()) {
            createEpollServer(listener);
        } else {
            createNioServer(listener);
        }
    }

    @Override
    protected void doStop(Listener listener) {
        if (!serverState.compareAndSet(State.STARTED, State.SHUTDOWN)) {
            if (listener != null) {
                listener.onFailure(new ServiceException("Server was already shutdown."));
            }
            return;
        }
        log.info("try shutdown {} ...", this.getClass().getSimpleName());
        if (bossGroup != null) {
            bossGroup.shutdownGracefully().syncUninterruptibly();
        }
        if (workerGroup != null) {
            workerGroup.shutdownGracefully().syncUninterruptibly();
        }
        log.info("{} shutdown success.", this.getClass().getSimpleName());
        if (listener != null) {
            listener.onSuccess(port);
        }
    }

    private boolean useNettyEpoll() {
        if (OsUtil.isUnix()) {
            try {
                Class.forName("io.netty.channel.epoll.Native");
                return true;
            } catch (ClassNotFoundException e) {
                log.warn("can not load netty epoll, switch nio model.");
            }
        }
        return false;
    }

    private void createEpollServer(Listener listener) {
        EventLoopGroup bossGroup = getBossGroup();
        EventLoopGroup workerGroup = getWorkerGroup();
        if (bossGroup == null) {
            EpollEventLoopGroup epollEventLoopGroup = new EpollEventLoopGroup(getBossThreadNumber(), getBossThreadFactory());
            epollEventLoopGroup.setIoRatio(100);
            bossGroup = epollEventLoopGroup;
        }
        if (workerGroup == null) {
            EpollEventLoopGroup epollEventLoopGroup = new EpollEventLoopGroup(getWorkThreadNumber(), getWorkThreadFactory());
            epollEventLoopGroup.setIoRatio(getIoRate());
            workerGroup = epollEventLoopGroup;
        }
        createServer(listener, bossGroup, workerGroup, EpollServerSocketChannel::new);
    }

    private void createNioServer(Listener listener) {
        EventLoopGroup bossGroup = getBossGroup();
        EventLoopGroup workerGroup = getWorkerGroup();
        if (bossGroup == null) {
            NioEventLoopGroup nioEventLoopGroup = new NioEventLoopGroup(getBossThreadNumber(), getBossThreadFactory(), getSelectorProvider());
            nioEventLoopGroup.setIoRatio(100);
            bossGroup = nioEventLoopGroup;
        }
        if (workerGroup == null) {
            NioEventLoopGroup nioEventLoopGroup = new NioEventLoopGroup(getWorkThreadNumber(), getWorkThreadFactory(), getSelectorProvider());
            nioEventLoopGroup.setIoRatio(getIoRate());
            workerGroup = nioEventLoopGroup;
        }
        createServer(listener, bossGroup, workerGroup, getChannelFactory());
    }

    private void createServer(Listener listener, EventLoopGroup boss, EventLoopGroup work, ChannelFactory<? extends ServerChannel> factory) {
        this.bossGroup = boss;
        this.workerGroup = work;
        try {
            ServerBootstrap bootstrap = new ServerBootstrap();
            bootstrap.group(bossGroup, workerGroup);
            bootstrap.channelFactory(factory);
            bootstrap.childHandler(new ChannelInitializer<Channel>() {
                @Override
                protected void initChannel(Channel channel) {
                    initPipeline(channel.pipeline());
                }
            });
            initOptions(bootstrap);
            InetSocketAddress address = StringUtils.isNotBlank(host) ? new InetSocketAddress(host, port) : new InetSocketAddress(port);
            bootstrap.bind(address).addListener(future -> {
                if (future.isSuccess()) {
                    serverState.set(State.STARTED);
                    log.info("{} start success on:{}", this.getClass().getSimpleName(), port);
                    if (listener != null) {
                        listener.onSuccess(port);
                    }
                } else {
                    log.error("{} start failure on:{}", this.getClass().getSimpleName(), port, future.cause());
                    if (listener != null) {
                        listener.onFailure(future.cause());
                    }
                }
            });
        } catch (Exception e) {
            log.error("Server start occur exception", e);
            if (listener != null) {
                listener.onFailure(e);
            }
            throw new ServiceException("Server start occur exception, port=" + port, e);
        }
    }

    public EventLoopGroup getBossGroup() {
        return bossGroup;
    }

    public EventLoopGroup getWorkerGroup() {
        return workerGroup;
    }

    protected int getBossThreadNumber() {
        return 1;
    }

    protected int getWorkThreadNumber() {
        return 0;
    }

    protected String getBossThreadName() {
        return ThreadName.T_BOSS;
    }

    protected String getWorkThreadName() {
        return ThreadName.T_WORKER;
    }

    protected ThreadFactory getBossThreadFactory() {
        return new DefaultThreadFactory(getBossThreadName());
    }

    protected ThreadFactory getWorkThreadFactory() {
        return new DefaultThreadFactory(getWorkThreadName());
    }

    protected int getIoRate() {
        return 70;
    }

    protected SelectorProvider getSelectorProvider() {
        return SelectorProvider.provider();
    }

    protected ChannelFactory<? extends ServerChannel> getChannelFactory() {
        return NioServerSocketChannel::new;
    }

    protected void initPipeline(ChannelPipeline pipeline) {
        pipeline.addLast("decoder", getDecoder());
        pipeline.addLast("encoder", getEncoder());
        pipeline.addLast("handler", getChannelHandler());
    }

    protected void initOptions(ServerBootstrap bootstrap) {
        bootstrap.option(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT);
        bootstrap.childOption(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT);
    }

    protected ChannelHandler getDecoder() {
        return new PacketDecoder();
    }

    protected ChannelHandler getEncoder() {
        return PacketEncoder.INSTANCE;
    }

    /**
     * 消息处理器
     * @return ChannelHandler
     */
    public abstract ChannelHandler getChannelHandler();

    private enum State {
        // 创建
        CREATED,
        // 初始化
        INITIALIZED,
        // 启动中
        STARTING,
        // 启动完成
        STARTED,
        // 停止
        SHUTDOWN
    }
}
