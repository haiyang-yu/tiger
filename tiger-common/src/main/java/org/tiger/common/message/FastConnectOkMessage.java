package org.tiger.common.message;

import io.netty.buffer.ByteBuf;
import org.tiger.api.connection.Connection;
import org.tiger.api.protocol.Command;
import org.tiger.api.protocol.Packet;

/**
 * {@link FastConnectOkMessage}
 *
 * @author SongQingWei
 * @since 1.0.0
 * 2020-01-17 14:20 周五
 */
public class FastConnectOkMessage extends BaseByteBufMessage {

    private int heartbeat;

    public FastConnectOkMessage(Packet packet, Connection connection) {
        super(packet, connection);
    }

    @Override
    public void encode(ByteBuf buf) {
        encodeInt(buf, heartbeat);
    }

    @Override
    public void decode(ByteBuf buf) {
        heartbeat = decodeInt(buf);
    }

    public static FastConnectOkMessage from(BaseMessage message) {
        return new FastConnectOkMessage(new Packet(Command.FAST_CONNECT, message.packet.sessionId), message.connection);
    }

    public int getHeartbeat() {
        return heartbeat;
    }

    public void setHeartbeat(int heartbeat) {
        this.heartbeat = heartbeat;
    }

    @Override
    public String toString() {
        return "FastConnectOkMessage{" +
                "heartbeat=" + heartbeat +
                '}';
    }
}
