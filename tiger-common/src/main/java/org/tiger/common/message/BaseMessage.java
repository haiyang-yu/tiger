package org.tiger.common.message;

import io.netty.channel.ChannelFutureListener;
import org.tiger.api.connection.Connection;
import org.tiger.api.crypto.Cipher;
import org.tiger.api.message.Message;
import org.tiger.api.protocol.Packet;
import org.tiger.common.utils.IoUtil;

import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

import static org.tiger.common.config.TigerConfig.Tiger.Core.COMPRESS_THRESHOLD;

/**
 * {@link BaseMessage}
 *
 * @author SongQingWei
 * @since 1.0.0
 * 2020-01-17 11:21 周五
 */
public abstract class BaseMessage implements Message {

    private static final byte STATUS_DECODED = 1;
    private static final byte STATUS_ENCODED = 2;
    private static final AtomicInteger ID_SEQ = new AtomicInteger();

    protected transient Packet packet;
    protected transient Connection connection;
    private transient byte status = 0;

    public BaseMessage(Packet packet, Connection connection) {
        this.packet = packet;
        this.connection = connection;
    }

    @Override
    public Connection getConnection() {
        return connection;
    }

    @Override
    public Packet getPacket() {
        return packet;
    }

    @Override
    public void encodeBody() {
        if ((status & STATUS_ENCODED) == 0) {
            status |= STATUS_ENCODED;
            encodeBinaryBody();
        }
    }

    @Override
    public void decodeBody() {
        if ((status & STATUS_DECODED) == 0) {
            status |= STATUS_DECODED;
            decodeBinaryBody();
        }
    }

    @Override
    public void send(ChannelFutureListener listener) {
        encodeBody();
        connection.send(packet, listener);
    }

    @Override
    public void sendRaw(ChannelFutureListener listener) {
        encodeBodyRaw();
        connection.send(packet, listener);
    }

    public void send() {
        send(ChannelFutureListener.FIRE_EXCEPTION_ON_FAILURE);
    }

    public void sendRaw() {
        sendRaw(ChannelFutureListener.FIRE_EXCEPTION_ON_FAILURE);
    }

    public void close() {
        send(ChannelFutureListener.CLOSE);
    }

    /**
     * 编码
     * @return 编码后的数据
     */
    public abstract byte[] encode();

    /**
     * 解码
     * @param bytes 待解码的数据
     */
    public abstract void decode(byte[] bytes);

    protected Cipher getCipher() {
        return connection.getSessionContext().cipher;
    }

    protected int getSessionId() {
        return packet.sessionId;
    }

    protected static int buildSessionId() {
        return ID_SEQ.incrementAndGet();
    }

    private void encodeBinaryBody() {
        byte[] bytes = encode();
        if (Objects.nonNull(bytes) && bytes.length > 0) {
            // 压缩
            if (bytes.length > COMPRESS_THRESHOLD) {
                byte[] compress = IoUtil.compress(bytes);
                if (compress.length > 0) {
                    bytes = compress;
                    packet.addFlag(Packet.FLAG_COMPRESS);
                }
            }
            // 加密
            Cipher cipher = getCipher();
            if (Objects.nonNull(cipher)) {
                byte[] encrypt = cipher.encrypt(bytes);
                if (encrypt.length > 0) {
                    bytes = encrypt;
                    packet.addFlag(Packet.FLAG_CRYPTO);
                }
            }
            packet.body = bytes;
        }
    }

    private void decodeBinaryBody() {
        // 解密
        byte[] bytes = packet.body;
        if (packet.hasFlag(Packet.FLAG_CRYPTO)) {
            Cipher cipher = getCipher();
            if (Objects.nonNull(cipher)) {
                bytes = cipher.decrypt(bytes);
            }
        }
        // 压缩
        if (packet.hasFlag(Packet.FLAG_COMPRESS)) {
            bytes = IoUtil.decompress(bytes);
        }
        if (bytes.length == 0) {
            throw new RuntimeException("message decode occur exception");
        }
        decode(bytes);
        packet.body = null;
    }

    private void encodeBodyRaw() {
        if ((status & STATUS_ENCODED) == 0) {
            status |= STATUS_ENCODED;
            packet.body = encode();
        }
    }
}
