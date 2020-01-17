package org.tiger.common.message;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import org.apache.commons.lang3.StringUtils;
import org.tiger.api.connection.Connection;
import org.tiger.api.protocol.Packet;

import java.nio.charset.StandardCharsets;
import java.util.Objects;

/**
 * {@link BaseByteBufMessage}
 *
 * @author SongQingWei
 * @since 1.0.0
 * 2020-01-17 13:37 周五
 */
public abstract class BaseByteBufMessage extends BaseMessage {

    public BaseByteBufMessage(Packet packet, Connection connection) {
        super(packet, connection);
    }

    @Override
    public byte[] encode() {
        ByteBuf buf = connection.getChannel().alloc().heapBuffer();
        try {
            encode(buf);
            byte[] bytes = new byte[buf.readableBytes()];
            buf.readBytes(bytes);
            return bytes;
        } finally {
            buf.release();
        }
    }

    @Override
    public void decode(byte[] bytes) {
        decode(Unpooled.wrappedBuffer(bytes));
    }

    /**
     * 编码
     * @param buf {@link ByteBuf}
     */
    public abstract void encode(ByteBuf buf);

    /**
     * 解码
     * @param buf {@link ByteBuf}
     */
    public abstract void decode(ByteBuf buf);

    protected void encodeString(ByteBuf buf, String field) {
        encodeBytes(buf, StringUtils.isBlank(field) ? null : field.getBytes(StandardCharsets.UTF_8));
    }

    protected void encodeByte(ByteBuf buf, byte field) {
        buf.writeByte(field);
    }

    protected void encodeShort(ByteBuf buf, short field) {
        buf.writeShort(field);
    }

    protected void encodeInt(ByteBuf buf, int field) {
        buf.writeInt(field);
    }

    protected void encodeLong(ByteBuf buf, long field) {
        buf.writeLong(field);
    }

    protected void encodeBytes(ByteBuf buf, byte[] field) {
        if (Objects.isNull(field) || field.length == 0) {
            encodeShort(buf, (short) 0);
        } else if (field.length < Short.MAX_VALUE) {
            encodeShort(buf, (short) field.length);
            buf.writeBytes(field);
        } else {
            encodeShort(buf, Short.MAX_VALUE);
            encodeInt(buf, field.length - Short.MAX_VALUE);
            buf.writeBytes(field);
        }
    }

    protected String decodeString(ByteBuf buf) {
        byte[] bytes = decodeBytes(buf);
        if (Objects.isNull(bytes)) {
            return null;
        }
        return new String(bytes, StandardCharsets.UTF_8);
    }

    protected byte decodeByte(ByteBuf buf) {
        return buf.readByte();
    }

    protected short decodeShort(ByteBuf buf) {
        return buf.readShort();
    }

    protected int decodeInt(ByteBuf buf) {
        return buf.readInt();
    }

    protected long decodeLong(ByteBuf buf) {
        return buf.readLong();
    }

    protected byte[] decodeBytes(ByteBuf buf) {
        int fieldLength = decodeShort(buf);
        if (fieldLength == 0) {
            return null;
        }
        if (fieldLength == Short.MAX_VALUE) {
            fieldLength += decodeInt(buf);
        }
        byte[] bytes = new byte[fieldLength];
        buf.readBytes(bytes);
        return bytes;
    }
}
