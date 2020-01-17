package org.tiger.handler;

import org.tiger.api.connection.Connection;
import org.tiger.api.protocol.Packet;

/**
 * {@link MessageHandler}
 *
 * @author SongQingWei
 * @since 1.0.0
 * 2020-01-17 14:55 周五
 */
public interface MessageHandler {

    /**
     * 处理消息
     * @param packet {@link Packet}
     * @param connection  {@link Connection}
     */
    void handler(Packet packet, Connection connection);
}
