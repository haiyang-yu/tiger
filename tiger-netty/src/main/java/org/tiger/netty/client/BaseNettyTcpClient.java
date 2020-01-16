package org.tiger.netty.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.*;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.epoll.EpollSocketChannel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.concurrent.DefaultThreadFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tiger.api.listener.Listener;
import org.tiger.api.service.BaseService;
import org.tiger.api.service.Client;
import org.tiger.common.constants.ThreadName;
import org.tiger.netty.codec.PacketDecoder;
import org.tiger.netty.codec.PacketEncoder;
import org.tiger.netty.utils.EpollUtil;

import java.net.InetSocketAddress;
import java.nio.channels.spi.SelectorProvider;
import java.util.Objects;
import java.util.concurrent.ThreadFactory;

/**
 * {@link BaseNettyTcpClient}
 *
 * @author SongQingWei
 * @since 1.0.0
 * 2020-01-16 09:56 周四
 */
public abstract class BaseNettyTcpClient extends BaseService implements Client {

    private static final Logger LOGGER = LoggerFactory.getLogger(BaseNettyTcpClient.class);

    private EventLoopGroup workerGroup;
    private Bootstrap bootstrap;

    @Override
    protected void doStart(Listener listener) throws Exception {
        if (EpollUtil.useNettyEpoll()) {
            createEpollClient(listener);
        } else {
            createNioClient(listener);
        }
    }

    @Override
    protected void doStop(Listener listener) throws Exception {
        if (Objects.nonNull(workerGroup)) {
            workerGroup.shutdownGracefully().syncUninterruptibly();
        }
        LOGGER.info("netty client [{}] stopped.", this.getClass().getSimpleName());
        super.doStop(listener);
    }

    public ChannelFuture connect(String host, int port) {
        return bootstrap.connect(new InetSocketAddress(host, port));
    }

    public ChannelFuture connect(String host, int port, Listener listener) {
        return bootstrap.connect(new InetSocketAddress(host, port)).addListener(future -> {
            if (future.isSuccess()) {
                if (Objects.nonNull(listener)) {
                    listener.onSuccess(port);
                }
                LOGGER.info("start netty client success, host={}, port={}", host, port);
            } else {
                if (Objects.nonNull(listener)) {
                    listener.onFailure(future.cause());
                    LOGGER.error("start netty client failure, host={}, port={}", host, port, future.cause());
                }
            }
        });
    }

    private void createEpollClient(Listener listener) {
        EventLoopGroup workerGroup = getWorkerGroup();
        if (Objects.isNull(workerGroup)) {
            EpollEventLoopGroup loopGroup = new EpollEventLoopGroup(getWorkThreadNumber(), getWorkThreadFactory());
            loopGroup.setIoRatio(getIoRate());
            workerGroup = loopGroup;
        }
        createClient(listener, workerGroup, EpollSocketChannel::new);
    }

    private void createNioClient(Listener listener) {
        EventLoopGroup workerGroup = getWorkerGroup();
        if (Objects.isNull(workerGroup)) {
            NioEventLoopGroup loopGroup = new NioEventLoopGroup(getWorkThreadNumber(), getWorkThreadFactory(), getSelectorProvider());
            loopGroup.setIoRatio(getIoRate());
            workerGroup = loopGroup;
        }
        createClient(listener, workerGroup, getChannelFactory());
    }

    private void createClient(Listener listener, EventLoopGroup workerGroup, ChannelFactory<? extends Channel> channelFactory) {
        this.workerGroup = workerGroup;
        this.bootstrap = new Bootstrap();
        bootstrap.group(workerGroup)
                // 允许重复使用本地地址和端口
                .option(ChannelOption.SO_REUSEADDR, true)
                .option(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT)
                .channelFactory(channelFactory)
                .handler(new ChannelInitializer<Channel>() {
                    @Override
                    protected void initChannel(Channel channel) throws Exception {
                        initPipeline(channel.pipeline());
                    }
                });
        initOptions(bootstrap);
        listener.onSuccess();
    }

    public EventLoopGroup getWorkerGroup() {
        return workerGroup;
    }

    protected int getWorkThreadNumber() {
        return 0;
    }

    protected ThreadFactory getWorkThreadFactory() {
        return new DefaultThreadFactory(getWorkThreadName());
    }

    protected String getWorkThreadName() {
        return ThreadName.T_CLIENT;
    }

    protected int getIoRate() {
        return 50;
    }

    protected SelectorProvider getSelectorProvider() {
        return SelectorProvider.provider();
    }

    protected ChannelFactory<? extends Channel> getChannelFactory() {
        return NioSocketChannel::new;
    }

    protected void initPipeline(ChannelPipeline pipeline) {
        pipeline.addLast("decoder", getDecoder());
        pipeline.addLast("encoder", getEncoder());
        pipeline.addLast("handler", getChannelHandler());
    }

    protected void initOptions(Bootstrap bootstrap) {
        bootstrap.option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 5000);
        bootstrap.option(ChannelOption.TCP_NODELAY, true);
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
}
