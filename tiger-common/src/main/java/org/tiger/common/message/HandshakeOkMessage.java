package org.tiger.common.message;

import io.netty.buffer.ByteBuf;
import org.tiger.api.connection.Connection;
import org.tiger.api.protocol.Command;
import org.tiger.api.protocol.Packet;

import java.util.Arrays;

/**
 * {@link HandshakeOkMessage}
 *
 * @author SongQingWei
 * @since 1.0.0
 * 2020-01-17 14:07 周五
 */
public class HandshakeOkMessage extends BaseByteBufMessage {

    private byte[] serverKey;
    private int heartbeat;
    private String clientSessionId;
    private long expireTime;

    public HandshakeOkMessage(Packet packet, Connection connection) {
        super(packet, connection);
    }

    @Override
    public void encode(ByteBuf buf) {
        encodeBytes(buf, serverKey);
        encodeInt(buf, heartbeat);
        encodeString(buf, clientSessionId);
        encodeLong(buf, expireTime);
    }

    @Override
    public void decode(ByteBuf buf) {
        serverKey = decodeBytes(buf);
        heartbeat = decodeInt(buf);
        clientSessionId = decodeString(buf);
        expireTime = decodeLong(buf);
    }

    public static HandshakeOkMessage from(BaseMessage message) {
        return new HandshakeOkMessage(new Packet(Command.HANDSHAKE, message.packet.sessionId), message.connection);
    }

    public byte[] getServerKey() {
        return serverKey;
    }

    public HandshakeOkMessage setServerKey(byte[] serverKey) {
        this.serverKey = serverKey;
        return this;
    }

    public int getHeartbeat() {
        return heartbeat;
    }

    public HandshakeOkMessage setHeartbeat(int heartbeat) {
        this.heartbeat = heartbeat;
        return this;
    }

    public String getClientSessionId() {
        return clientSessionId;
    }

    public HandshakeOkMessage setClientSessionId(String clientSessionId) {
        this.clientSessionId = clientSessionId;
        return this;
    }

    public long getExpireTime() {
        return expireTime;
    }

    public HandshakeOkMessage setExpireTime(long expireTime) {
        this.expireTime = expireTime;
        return this;
    }

    @Override
    public String toString() {
        return "HandshakeOkMessage{" +
                "serverKey=" + Arrays.toString(serverKey) +
                ", heartbeat=" + heartbeat +
                ", clientSessionId='" + clientSessionId + '\'' +
                ", expireTime=" + expireTime +
                '}';
    }
}
