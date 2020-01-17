package org.tiger.core.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.WriteBufferWaterMark;
import io.netty.handler.traffic.GlobalChannelTrafficShapingHandler;
import io.netty.util.concurrent.DefaultThreadFactory;
import org.tiger.api.connection.ConnectionManager;
import org.tiger.api.listener.Listener;
import org.tiger.common.config.TigerConfig;
import org.tiger.common.constants.ThreadName;
import org.tiger.core.TigerServer;
import org.tiger.core.manager.ServerConnectionManager;
import org.tiger.netty.server.BaseNettyTcpServer;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;

import static org.tiger.common.config.TigerConfig.Tiger.Net.CONNECT_SERVER_BIND_IP;
import static org.tiger.common.config.TigerConfig.Tiger.Net.CONNECT_SERVER_PORT;
import static org.tiger.common.config.TigerConfig.Tiger.Net.TrafficShaping.ConnectServer.*;
import static org.tiger.common.config.TigerConfig.Tiger.Net.WriteBufferWaterMark.CONNECT_SERVER_HIGH;
import static org.tiger.common.config.TigerConfig.Tiger.Net.WriteBufferWaterMark.CONNECT_SERVER_LOW;

/**
 * {@link ConnectionServer}
 *
 * @author SongQingWei
 * @since 1.0.0
 * 2020-01-14 17:39 周二
 */
public class ConnectionServer extends BaseNettyTcpServer {

    private TigerServer tigerServer;
    private ServerChannelHandler channelHandler;
    private GlobalChannelTrafficShapingHandler trafficShapingHandler;
    private ScheduledExecutorService trafficShapingExecutor;
    private ConnectionManager connectionManager;

    public ConnectionServer(TigerServer tigerServer) {
        super(CONNECT_SERVER_BIND_IP, CONNECT_SERVER_PORT);
        this.tigerServer = tigerServer;
        this.connectionManager = new ServerConnectionManager(true);
        this.channelHandler = new ServerChannelHandler(true, connectionManager);
    }

    @Override
    public void init() {
        super.init();
        connectionManager.init();
        if (ENABLED) {
            trafficShapingExecutor = new ScheduledThreadPoolExecutor(1, new DefaultThreadFactory(ThreadName.T_TRAFFIC_SHAPING, true));
            trafficShapingHandler = new GlobalChannelTrafficShapingHandler(
                    trafficShapingExecutor,
                    WRITE_GLOBAL_LIMIT,
                    READ_GLOBAL_LIMIT,
                    WRITE_CHANNEL_LIMIT,
                    READ_CHANNEL_LIMIT,
                    CHECK_INTERVAL);
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
        bootstrap.childOption(ChannelOption.SO_SNDBUF, TigerConfig.Tiger.Net.SndBuf.CONNECT_SERVER);
        bootstrap.childOption(ChannelOption.SO_RCVBUF, TigerConfig.Tiger.Net.RcvBuf.CONNECT_SERVER);
        bootstrap.childOption(ChannelOption.WRITE_BUFFER_WATER_MARK, new WriteBufferWaterMark(
                CONNECT_SERVER_LOW, CONNECT_SERVER_HIGH)
        );
    }

    @Override
    public ChannelHandler getChannelHandler() {
        return channelHandler;
    }

    public ConnectionManager getConnectionManager() {
        return connectionManager;
    }
}
