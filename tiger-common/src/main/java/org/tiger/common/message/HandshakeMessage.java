package org.tiger.common.message;

import io.netty.buffer.ByteBuf;
import org.tiger.api.connection.Connection;
import org.tiger.api.crypto.Cipher;
import org.tiger.api.protocol.Command;
import org.tiger.api.protocol.Packet;
import org.tiger.spi.factory.crypto.RsaCipherFactory;

import java.util.Arrays;

/**
 * {@link HandshakeMessage}
 *
 * @author SongQingWei
 * @since 1.0.0
 * 2020-01-17 13:59 周五
 */
public final class HandshakeMessage extends BaseByteBufMessage {

    private String deviceId;
    private String osName;
    private String osVersion;
    private String clientVersion;
    private byte[] clientKey;
    private byte[] iv;
    private int minHeartbeat;
    private int maxHeartbeat;

    public HandshakeMessage(Connection connection) {
        this(new Packet(Command.HANDSHAKE, buildSessionId()), connection);
    }

    public HandshakeMessage(Packet packet, Connection connection) {
        super(packet, connection);
    }

    @Override
    public void encode(ByteBuf buf) {
        encodeString(buf, deviceId);
        encodeString(buf, osName);
        encodeString(buf, osVersion);
        encodeString(buf, clientVersion);
        encodeBytes(buf, clientKey);
        encodeBytes(buf, iv);
        encodeInt(buf, minHeartbeat);
        encodeInt(buf, maxHeartbeat);
    }

    @Override
    public void decode(ByteBuf buf) {
        deviceId = decodeString(buf);
        osName = decodeString(buf);
        osVersion = decodeString(buf);
        clientVersion = decodeString(buf);
        clientKey = decodeBytes(buf);
        iv = decodeBytes(buf);
        minHeartbeat = decodeInt(buf);
        maxHeartbeat = decodeInt(buf);
    }

    @Override
    protected Cipher getCipher() {
        return RsaCipherFactory.create();
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public String getOsName() {
        return osName;
    }

    public void setOsName(String osName) {
        this.osName = osName;
    }

    public String getOsVersion() {
        return osVersion;
    }

    public void setOsVersion(String osVersion) {
        this.osVersion = osVersion;
    }

    public String getClientVersion() {
        return clientVersion;
    }

    public void setClientVersion(String clientVersion) {
        this.clientVersion = clientVersion;
    }

    public byte[] getClientKey() {
        return clientKey;
    }

    public void setClientKey(byte[] clientKey) {
        this.clientKey = clientKey;
    }

    public byte[] getIv() {
        return iv;
    }

    public void setIv(byte[] iv) {
        this.iv = iv;
    }

    public int getMinHeartbeat() {
        return minHeartbeat;
    }

    public void setMinHeartbeat(int minHeartbeat) {
        this.minHeartbeat = minHeartbeat;
    }

    public int getMaxHeartbeat() {
        return maxHeartbeat;
    }

    public void setMaxHeartbeat(int maxHeartbeat) {
        this.maxHeartbeat = maxHeartbeat;
    }

    @Override
    public String toString() {
        return "HandshakeMessage{" +
                "deviceId='" + deviceId + '\'' +
                ", osName='" + osName + '\'' +
                ", osVersion='" + osVersion + '\'' +
                ", clientVersion='" + clientVersion + '\'' +
                ", clientKey=" + Arrays.toString(clientKey) +
                ", iv=" + Arrays.toString(iv) +
                ", minHeartbeat=" + minHeartbeat +
                ", maxHeartbeat=" + maxHeartbeat +
                '}';
    }
}
