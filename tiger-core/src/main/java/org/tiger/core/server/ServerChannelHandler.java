package org.tiger.core.server;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tiger.api.connection.Connection;
import org.tiger.api.connection.ConnectionManager;
import org.tiger.api.protocol.Packet;
import org.tiger.netty.connection.NettyConnection;

/**
 * {@link ServerChannelHandler}
 *
 * @author SongQingWei
 * @since 1.0.0
 * 2020-01-14 17:41 周二
 */
@ChannelHandler.Sharable
public class ServerChannelHandler extends ChannelInboundHandlerAdapter {

    private static final Logger LOGGER = LoggerFactory.getLogger(ServerChannelHandler.class);

    /**
     * 是否启用加密
     */
    private final boolean security;

    /**
     * 连接管理器
     */
    private final ConnectionManager connectionManager;

    public ServerChannelHandler(boolean security, ConnectionManager connectionManager) {
        this.security = security;
        this.connectionManager = connectionManager;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        LOGGER.info("client connected, channel={}", ctx.channel());
        Connection connection = new NettyConnection();
        connection.init(ctx.channel(), security);
        connectionManager.add(connection);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        Connection connection = connectionManager.removeAndClose(ctx.channel());
        LOGGER.info("client disconnected, connection={}", connection);
        // TODO 借助EventBus做通知
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        Packet packet = (Packet) msg;
        Connection connection = connectionManager.get(ctx.channel());
        LOGGER.debug("channel read, connection={}, packet={}", connection, packet);
        connection.updateLastReadTime();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        Connection connection = connectionManager.get(ctx.channel());
        LOGGER.error("client caught exception, connection={}", connection, cause);
        ctx.close();
    }
}
