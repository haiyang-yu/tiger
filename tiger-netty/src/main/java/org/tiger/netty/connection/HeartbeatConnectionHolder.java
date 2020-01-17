package org.tiger.netty.connection;

import io.netty.util.Timeout;
import io.netty.util.Timer;
import io.netty.util.TimerTask;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tiger.api.connection.Connection;
import org.tiger.api.connection.ConnectionHolder;
import org.tiger.api.protocol.Packet;
import org.tiger.common.log.TigerLog;

import java.util.concurrent.TimeUnit;

import static org.tiger.common.config.TigerConfig.Tiger.Core.MAX_HEARTBEAT_TIMEOUT_TIMES;

/**
 * {@link HeartbeatConnectionHolder}
 *
 * @author SongQingWei
 * @since 1.0.0
 * 2020-01-16 13:37 周四
 */
public class HeartbeatConnectionHolder implements ConnectionHolder, TimerTask {

    private static final Logger LOGGER = LoggerFactory.getLogger(HeartbeatConnectionHolder.class);

    private byte timeoutTimes = 0;
    private final Connection connection;
    private final Timer timer;
    private final Integer timeDiff;
    private final boolean isSendHeartbeat;

    public HeartbeatConnectionHolder(Timer timer, Connection connection) {
        this(timer, connection, 0, false);
    }

    public HeartbeatConnectionHolder(Timer timer, Connection connection, int timeDiff, boolean isSendHeartbeat) {
        this.timer = timer;
        this.connection = connection;
        this.timeDiff = timeDiff;
        this.isSendHeartbeat = isSendHeartbeat;
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
        if (isSendHeartbeat) {
            if (connection.isWriteTimeout()) {
                LOGGER.info("send heartbeat ping...");
                connection.send(Packet.HEARTBEAT_PACKET);
            }
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
            System.out.println(">>>>>" + timeout);
            timer.newTimeout(this, timeout - timeDiff, TimeUnit.MILLISECONDS);
        }
    }
}
