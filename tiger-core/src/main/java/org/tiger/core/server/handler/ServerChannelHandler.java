package org.tiger.core.server.handler;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.extern.slf4j.Slf4j;
import org.tiger.api.connection.Connection;
import org.tiger.api.connection.ConnectionManager;
import org.tiger.core.connection.NettyConnection;
import org.tiger.tools.protocol.Packet;

/**
 * {@link ServerChannelHandler}
 *
 * @author SongQingWei
 * @since 1.0.0
 * 2020-01-08 15:17 周三
 */
@Slf4j
@ChannelHandler.Sharable
public class ServerChannelHandler extends ChannelInboundHandlerAdapter {

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
        log.info("client connected, channel={}", ctx.channel());
        Connection connection = new NettyConnection();
        connection.init(ctx.channel(), security);
        connectionManager.add(connection);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        Connection connection = connectionManager.removeAndClose(ctx.channel());
        log.info("client disconnected, connection={}", connection);
        // TODO 借助EventBus做通知
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        Packet packet = (Packet) msg;
        Connection connection = connectionManager.get(ctx.channel());
        log.debug("channel read, connection={}, packet={}", connection, packet);
        connection.updateLastReadTime();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        Connection connection = connectionManager.get(ctx.channel());
        log.error("client caught exception, connection={}", connection, cause);
        ctx.close();
    }
}
