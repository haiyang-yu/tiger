package org.tiger.common.handler;

import org.tiger.api.connection.Connection;
import org.tiger.api.protocol.Packet;
import org.tiger.common.message.BaseMessage;
import org.tiger.handler.MessageHandler;

import java.util.Objects;

/**
 * {@link BaseMessageHandler}
 *
 * @author SongQingWei
 * @since 1.0.0
 * 2020-01-17 14:56 周五
 */
public abstract class BaseMessageHandler<T extends BaseMessage> implements MessageHandler {

    /**
     * 解码消息
     * @param packet {@link Packet}
     * @param connection {@link Connection}
     * @return {@link T} extends {@link BaseMessage}
     */
    public abstract T decode(Packet packet, Connection connection);

    /**
     * 处理消息
     * @param message {@link T} extends {@link BaseMessage}
     */
    public abstract void handle(T message);

    @Override
    public void handler(Packet packet, Connection connection) {
        T t = decode(packet, connection);
        if (Objects.nonNull(t)) {
            t.decodeBody();
            handle(t);
        }
    }
}
