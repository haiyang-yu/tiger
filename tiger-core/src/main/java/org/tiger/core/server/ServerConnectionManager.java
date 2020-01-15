package org.tiger.core.server;

import io.netty.channel.Channel;
import io.netty.channel.ChannelId;
import io.netty.util.HashedWheelTimer;
import io.netty.util.Timeout;
import io.netty.util.TimerTask;
import io.netty.util.concurrent.DefaultThreadFactory;
import org.tiger.api.connection.Connection;
import org.tiger.api.connection.ConnectionManager;
import org.tiger.common.constants.ThreadName;
import org.tiger.common.log.TigerLog;
import org.tiger.netty.connection.NettyConnection;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;

import static org.tiger.common.config.TigerConfig.Tiger.Core.MAX_HEARTBEAT;
import static org.tiger.common.config.TigerConfig.Tiger.Core.MAX_HEARTBEAT_TIMEOUT_TIMES;

/**
 * {@link ServerConnectionManager}
 *
 * @author SongQingWei
 * @since 1.0.0
 * 2020-01-14 17:27 周二
 */
public class ServerConnectionManager implements ConnectionManager {

    private final ConcurrentMap<ChannelId, ConnectionHolder> connections = new ConcurrentHashMap<>();
    private final ConnectionHolder default_holder = new SimpleConnectionHolder(null);
    private final boolean enabledHeartbeat;
    private final ConnectionHolderFactory factory;
    private HashedWheelTimer timer;

    public ServerConnectionManager(boolean enabledHeartbeat) {
        this.enabledHeartbeat = enabledHeartbeat;
        this.factory = enabledHeartbeat ? HeartbeatConnectionHolder::new : SimpleConnectionHolder::new;
    }

    @Override
    public void init() {
        if (enabledHeartbeat) {
            long tickDuration = TimeUnit.SECONDS.toMillis(1);
            int ticksPerWheel = (int) (MAX_HEARTBEAT / tickDuration);
            this.timer = new HashedWheelTimer(
                    new DefaultThreadFactory(ThreadName.T_CONN_TIMER, true),
                    tickDuration,
                    TimeUnit.MILLISECONDS,
                    ticksPerWheel
            );
        }
    }

    @Override
    public void destroy() {
        if (timer != null) {
            timer.stop();
        }
        connections.values().forEach(ConnectionHolder::close);
        connections.clear();
    }

    @Override
    public void add(Connection connection) {
        connections.putIfAbsent(connection.getChannel().id(), factory.create(connection));
    }

    @Override
    public Connection get(Channel channel) {
        return connections.getOrDefault(channel.id(), default_holder).get();
    }

    @Override
    public Connection removeAndClose(Channel channel) {
        ConnectionHolder holder = connections.remove(channel.id());
        if (holder != null) {
            Connection connection = holder.get();
            holder.close();
            return connection;
        }
        Connection connection = new NettyConnection();
        connection.init(channel, false);
        connection.close();
        return connection;
    }

    @Override
    public int getConnectionNumber() {
        return connections.size();
    }

    private interface ConnectionHolder {

        /**
         * 从连接持有器中获取连接信息
         * @return 连接信息
         */
        Connection get();

        /**
         * 关闭当前持有器所持有的连接
         */
        void close();
    }

    private static class SimpleConnectionHolder implements ConnectionHolder {

        private final Connection connection;

        public SimpleConnectionHolder(Connection connection) {
            this.connection = connection;
        }

        @Override
        public Connection get() {
            return connection;
        }

        @Override
        public void close() {
            if (connection != null) {
                connection.close();
            }
        }
    }

    private class HeartbeatConnectionHolder implements ConnectionHolder, TimerTask {

        private byte timeoutTimes = 0;
        private final Connection connection;

        public HeartbeatConnectionHolder(Connection connection) {
            this.connection = connection;
            startTimeout();
        }

        @Override
        public void run(Timeout timeout) {
            Connection connection = this.connection;
            if (connection == null || !connection.isConnected()) {
                TigerLog.CONNECT.info("connection disconnected, heartbeat timeout times={}, connection={}", timeoutTimes, connection);
                return;
            }
            if (connection.isReadTimeout()) {
                if (++timeoutTimes > MAX_HEARTBEAT_TIMEOUT_TIMES) {
                    connection.close();
                    TigerLog.CONNECT.warn("client heartbeat timeout times={}, do close connection={}", timeoutTimes, connection);
                    return;
                } else {
                    TigerLog.CONNECT.info("client heartbeat timeout times={}, connection={}", timeoutTimes, connection);
                }
            } else {
                timeoutTimes = 0;
            }
            startTimeout();
        }

        @Override
        public Connection get() {
            return connection;
        }

        @Override
        public void close() {
            if (connection != null) {
                connection.close();
            }
        }

        private void startTimeout() {
            Connection connection = this.connection;
            if (connection != null && connection.isConnected()) {
                int timeout = connection.getSessionContext().heartbeat;
                timer.newTimeout(this, timeout, TimeUnit.MILLISECONDS);
            }
        }
    }

    @FunctionalInterface
    private interface ConnectionHolderFactory {

        /**
         * 创建连接信息的持有者
         * @param connection 链接信息
         * @return 连接持有者
         */
        ConnectionHolder create(Connection connection);
    }
}
