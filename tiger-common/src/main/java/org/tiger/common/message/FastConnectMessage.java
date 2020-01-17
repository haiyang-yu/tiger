package org.tiger.common.message;

import io.netty.buffer.ByteBuf;
import org.tiger.api.connection.Connection;
import org.tiger.api.protocol.Command;
import org.tiger.api.protocol.Packet;

/**
 * {@link FastConnectMessage}
 *
 * @author SongQingWei
 * @since 1.0.0
 * 2020-01-17 14:17 周五
 */
public class FastConnectMessage extends BaseByteBufMessage {

    private String clientSessionId;
    private String deviceId;

    public FastConnectMessage(Connection connection) {
        this(new Packet(Command.FAST_CONNECT, buildSessionId()), connection);

    }

    public FastConnectMessage(Packet packet, Connection connection) {
        super(packet, connection);
    }

    @Override
    public void encode(ByteBuf buf) {
        encodeString(buf, clientSessionId);
        encodeString(buf, deviceId);
    }

    @Override
    public void decode(ByteBuf buf) {
        clientSessionId = decodeString(buf);
        deviceId = decodeString(buf);
    }

    public String getClientSessionId() {
        return clientSessionId;
    }

    public void setClientSessionId(String clientSessionId) {
        this.clientSessionId = clientSessionId;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    @Override
    public String toString() {
        return "FastConnectMessage{" +
                "clientSessionId='" + clientSessionId + '\'' +
                ", deviceId='" + deviceId + '\'' +
                '}';
    }
}
