package org.tiger.core.server;

import io.netty.channel.Channel;
import io.netty.channel.ChannelId;
import io.netty.util.HashedWheelTimer;
import io.netty.util.Timer;
import io.netty.util.concurrent.DefaultThreadFactory;
import org.tiger.api.connection.Connection;
import org.tiger.api.connection.ConnectionHolder;
import org.tiger.api.connection.ConnectionHolderFactory;
import org.tiger.api.connection.ConnectionManager;
import org.tiger.common.constants.ThreadName;
import org.tiger.netty.connection.HeartbeatConnectionHolder;
import org.tiger.netty.connection.NettyConnection;
import org.tiger.netty.connection.SimpleConnectionHolder;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;

import static org.tiger.common.config.TigerConfig.Tiger.Core.MAX_HEARTBEAT;

/**
 * {@link ServerConnectionManager}
 *
 * @author SongQingWei
 * @since 1.0.0
 * 2020-01-14 17:27 周二
 */
public class ServerConnectionManager implements ConnectionManager {

    private final ConcurrentMap<ChannelId, ConnectionHolder> connections = new ConcurrentHashMap<>();
    private final ConnectionHolder defaultHolder = new SimpleConnectionHolder();
    private final boolean enabledHeartbeat;
    private final ConnectionHolderFactory factory;
    private Timer timer;

    public ServerConnectionManager(boolean enabledHeartbeat) {
        this.enabledHeartbeat = enabledHeartbeat;
        this.factory = enabledHeartbeat
                ? (connection -> new HeartbeatConnectionHolder(timer, connection))
                : SimpleConnectionHolder::new;
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
        return connections.getOrDefault(channel.id(), defaultHolder).get();
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
}
