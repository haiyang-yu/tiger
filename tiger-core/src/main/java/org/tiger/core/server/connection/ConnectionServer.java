package org.tiger.core.server.connection;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.WriteBufferWaterMark;
import io.netty.handler.traffic.GlobalChannelTrafficShapingHandler;
import io.netty.util.concurrent.DefaultThreadFactory;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.tiger.api.connection.ConnectionManager;
import org.tiger.api.listener.Listener;
import org.tiger.core.connection.ServerConnectionManager;
import org.tiger.core.server.AbstractNettyTcpService;
import org.tiger.core.server.handler.ServerChannelHandler;
import org.tiger.tools.common.Constant;
import org.tiger.tools.common.ThreadName;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;

/**
 * {@link ConnectionServer}
 *
 * @author SongQingWei
 * @since 1.0.0
 * 2020-01-08 15:16 周三
 */
@Slf4j
public class ConnectionServer extends AbstractNettyTcpService {

    private ServerChannelHandler channelHandler;
    private GlobalChannelTrafficShapingHandler trafficShapingHandler;
    private ScheduledExecutorService trafficShapingExecutor;
    @Getter
    private ConnectionManager connectionManager;

    public ConnectionServer(String host, int port) {
        super(host, port);
        this.connectionManager = new ServerConnectionManager(true);
        this.channelHandler = new ServerChannelHandler(true, connectionManager);
    }

    @Override
    public void init() {
        super.init();
        connectionManager.init();
        if (Constant.TRAFFIC_SHAPING_ENABLED) {
            trafficShapingExecutor = new ScheduledThreadPoolExecutor(1, new DefaultThreadFactory(ThreadName.T_TRAFFIC_SHAPING, true));
            trafficShapingHandler = new GlobalChannelTrafficShapingHandler(
                    trafficShapingExecutor,
                    0,
                    1024 * 100,
                    1024 * 3,
                    1024 * 3,
                    100);
        }
    }

    @Override
    public void stop(Listener listener) {
        super.stop(listener);
        if (trafficShapingHandler != null) {
            trafficShapingHandler.release();
            trafficShapingExecutor.shutdown();
        }
        connectionManager.destroy();
    }

    @Override
    protected String getBossThreadName() {
        return ThreadName.T_CONN_BOSS;
    }

    @Override
    protected String getWorkThreadName() {
        return ThreadName.T_CONN_WORKER;
    }

    @Override
    protected void initPipeline(ChannelPipeline pipeline) {
        super.initPipeline(pipeline);
        if (trafficShapingHandler != null) {
            pipeline.addFirst(trafficShapingHandler);
        }
    }

    @Override
    protected void initOptions(ServerBootstrap bootstrap) {
        super.initOptions(bootstrap);
        bootstrap.option(ChannelOption.SO_BACKLOG, 1024);
        bootstrap.childOption(ChannelOption.SO_SNDBUF, 1024 * 32);
        bootstrap.childOption(ChannelOption.SO_RCVBUF, 1024 * 32);
        bootstrap.childOption(ChannelOption.WRITE_BUFFER_WATER_MARK, new WriteBufferWaterMark(1024 * 32, 1024 * 64));
    }

    @Override
    public ChannelHandler getChannelHandler() {
        return channelHandler;
    }
}
