package org.tiger.client.connection;

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

import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * {@link ClientConnectionManager}
 *
 * @author SongQingWei
 * @since 1.0.0
 * 2020-01-16 13:16 周四
 */
public class ClientConnectionManager implements ConnectionManager {

    private final ConcurrentMap<ChannelId, ConnectionHolder> connections = new ConcurrentHashMap<>(1);
    private SimpleConnectionHolder defaultHolder = new SimpleConnectionHolder();
    private final ConnectionHolderFactory factory;
    private Timer timer;

    public ClientConnectionManager() {
        this.factory = connection -> new HeartbeatConnectionHolder(timer, connection, 1000, true);
    }

    @Override
    public void init() {
        this.timer = new HashedWheelTimer(new DefaultThreadFactory(ThreadName.T_CONN_TIMER, true));
    }

    @Override
    public void destroy() {
        if (Objects.nonNull(timer)) {
            timer.stop();
        }
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
        Connection connection;
        if (Objects.isNull(holder)) {
            connection = new NettyConnection();
            connection.init(channel, false);
        } else {
            connection = holder.get();
        }
        connection.close();
        return connection;
    }

    @Override
    public int getConnectionNumber() {
        return 1;
    }
}
