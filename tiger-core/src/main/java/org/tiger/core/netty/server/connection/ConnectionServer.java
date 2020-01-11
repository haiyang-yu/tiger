package org.tiger.core.netty.server.connection;

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
import org.tiger.core.netty.AbstractNettyTcpService;
import org.tiger.core.netty.server.handler.ServerChannelHandler;
import org.tiger.netty.server.connection.ServerConnectionManager;
import org.tiger.tools.common.ThreadName;
import org.tiger.tools.config.DefaultConfig;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;

import static org.tiger.tools.config.DefaultConfig.Tiger.Net.CONNECT_SERVER_BIND_IP;
import static org.tiger.tools.config.DefaultConfig.Tiger.Net.CONNECT_SERVER_PORT;
import static org.tiger.tools.config.DefaultConfig.Tiger.Net.TrafficShaping.ConnectServer.*;
import static org.tiger.tools.config.DefaultConfig.Tiger.Net.WriteBufferWaterMark.CONNECT_SERVER_HIGH;
import static org.tiger.tools.config.DefaultConfig.Tiger.Net.WriteBufferWaterMark.CONNECT_SERVER_LOW;

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

    public ConnectionServer() {
        super(CONNECT_SERVER_BIND_IP, CONNECT_SERVER_PORT);
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
        bootstrap.childOption(ChannelOption.SO_SNDBUF, DefaultConfig.Tiger.Net.SndBuf.CONNECT_SERVER);
        bootstrap.childOption(ChannelOption.SO_RCVBUF, DefaultConfig.Tiger.Net.RcvBuf.CONNECT_SERVER);
        bootstrap.childOption(ChannelOption.WRITE_BUFFER_WATER_MARK, new WriteBufferWaterMark(
                CONNECT_SERVER_LOW, CONNECT_SERVER_HIGH)
        );
    }

    @Override
    public ChannelHandler getChannelHandler() {
        return channelHandler;
    }
}
