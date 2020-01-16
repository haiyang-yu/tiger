package org.tiger.netty.connection;

import io.netty.util.Timer;
import org.tiger.api.connection.Connection;
import org.tiger.api.connection.ConnectionHolder;

/**
 * {@link SimpleConnectionHolder}
 *
 * @author SongQingWei
 * @since 1.0.0
 * 2020-01-16 13:44 周四
 */
public class SimpleConnectionHolder implements ConnectionHolder {

    private final Connection connection;

    public SimpleConnectionHolder() {
        this(null, null, 0);
    }

    public SimpleConnectionHolder(Connection connection) {
        this(null, connection, 0);
    }

    public SimpleConnectionHolder(Timer timer, Connection connection, int timerDiff) {
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
