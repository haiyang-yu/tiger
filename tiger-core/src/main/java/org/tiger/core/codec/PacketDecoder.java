package org.tiger.core.codec;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.handler.codec.TooLongFrameException;
import org.tiger.tools.common.Constant;
import org.tiger.tools.protocol.Packet;

import java.util.List;

/**
 * {@link PacketDecoder}
 *
 * @author SongQingWei
 * @since 1.0.0
 * 2020-01-08 14:50 周三
 */
public class PacketDecoder extends ByteToMessageDecoder {

    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf, List<Object> list) throws Exception {
        decodeHeartbeat(byteBuf, list);
    }

    private void decodeHeartbeat(ByteBuf buf, List<Object> list) {
        while (buf.isReadable()) {
            if (buf.readByte() == Packet.HEARTBEAT_PACKET_BYTE) {
                list.add(Packet.HEARTBEAT_PACKET);
            } else {
                buf.readerIndex(buf.readerIndex() - 1);
                break;
            }
        }
    }

    private void decodeFrames(ByteBuf buf, List<Object> list) {
        if (buf.readableBytes() >= Packet.HEADER_LEN) {
            // 记录当前读取位置位置.如果读取到非完整的frame,要恢复到该位置,便于下次读取
            buf.markReaderIndex();
            Packet packet = decodeFrame(buf);
            if (packet != null) {
                list.add(packet);
            } else {
                // 读取到不完整的frame,恢复到最近一次正常读取的位置,便于下次读取
                buf.resetReaderIndex();
            }
        }
    }

    private Packet decodeFrame(ByteBuf buf) {
        int readableBytes = buf.readableBytes();
        // 读取消息长度
        int bodyLength = buf.readInt();
        if (readableBytes < bodyLength + Packet.HEADER_LEN) {
            return null;
        }
        if (bodyLength > Constant.MAX_PACKET_SIZE) {
            throw new TooLongFrameException("packet body length over limit:" + bodyLength);
        }
        // 读取命令码
        byte cmd = buf.readByte();
        // 读取特性
        byte flags = buf.readByte();
        // 读取会话ID
        int sessionId = buf.readInt();
        Packet packet = new Packet(cmd, sessionId);
        packet.flags = flags;
        // 读取消息
        if (bodyLength > 0) {
            buf.readBytes(packet.body = new byte[bodyLength]);
        }
        return packet;
    }
}
