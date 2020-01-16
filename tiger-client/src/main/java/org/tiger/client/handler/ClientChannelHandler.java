package org.tiger.client.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tiger.api.cache.CacheManager;
import org.tiger.api.connection.Connection;
import org.tiger.api.connection.ConnectionManager;
import org.tiger.api.protocol.Command;
import org.tiger.api.protocol.Packet;
import org.tiger.client.config.ClientCache;
import org.tiger.client.config.ClientConfig;
import org.tiger.common.constants.CacheKey;
import org.tiger.common.security.cipher.AesCipher;
import org.tiger.netty.connection.NettyConnection;
import org.tiger.spi.factory.cache.CacheManagerFactory;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * {@link ClientChannelHandler}
 *
 * @author SongQingWei
 * @since 1.0.0
 * 2020-01-16 10:41 周四
 */
public class ClientChannelHandler extends ChannelInboundHandlerAdapter {

    private static final Logger LOGGER = LoggerFactory.getLogger(ClientChannelHandler.class);

    private final CacheManager cacheManager = CacheManagerFactory.create();
    private final ClientConfig clientConfig;
    private final ConnectionManager connectionManager;

    private final Connection connection = new NettyConnection();

    public ClientChannelHandler(ClientConfig clientConfig, ConnectionManager connectionManager) {
        this.clientConfig = clientConfig;
        this.connectionManager = connectionManager;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        LOGGER.info("client connect channel={}", ctx.channel());
        if (Objects.isNull(clientConfig)) {
            throw new NullPointerException("client config is null, channel=" + ctx.channel());
        }
        connection.init(ctx.channel(), true);
        connectionManager.add(connection);
        connection();
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        Connection connection = connectionManager.removeAndClose(ctx.channel());
        LOGGER.info("client disconnect connection={}", connection);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        Connection connection = connectionManager.get(ctx.channel());
        connection.updateLastReadTime();
        Packet packet = (Packet) msg;
        Command command = Command.getCommand(packet.cmd);
        switch (command) {
            case HANDSHAKE:
                connection.getSessionContext().cipher = new AesCipher(clientConfig.getKey(), clientConfig.getIv());
                LOGGER.info("handshake success, clientConfig={}", clientConfig);
                cacheConnection(connection, "", 100L);
                break;
            case HEARTBEAT:
                LOGGER.info("receive heartbeat pong...");
                break;
            case FAST_CONNECT:
                break;
            default:
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        Connection connection = connectionManager.get(ctx.channel());
        LOGGER.error("client disconnect, caught an exception, connection={}", connection, cause);
    }

    private void connection() {
        String key = CacheKey.getDeviceIdKey(clientConfig.getDeviceId());
        ClientCache cache = cacheManager.get(key, ClientCache.class);
        if (Objects.isNull(cache) || StringUtils.isBlank(cache.getSessionId())
                || cache.getExpireTime() < System.currentTimeMillis()) {
            // TODO 握手操作
            return;
        }
        // TODO 快速重连操作
        LOGGER.debug("send fast connect message={}", "");
    }

    private void cacheConnection(Connection connection, String sessionId, long expireTime) {
        ClientCache cache = new ClientCache();
        cache.setSessionId(sessionId);
        cache.setExpireTime(expireTime);
        cache.setCipher(connection.getSessionContext().cipher.toString());
        String key = CacheKey.getDeviceIdKey(clientConfig.getDeviceId());
        cacheManager.set(key, cache, 5, TimeUnit.MINUTES);
    }

    public Connection getConnection() {
        return connection;
    }
}