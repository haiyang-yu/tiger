package org.tiger.core.codec;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import org.tiger.tools.protocol.Command;
import org.tiger.tools.protocol.Packet;

/**
 * {@link PacketEncoder}
 *
 * @author SongQingWei
 * @since 1.0.0
 * 2020-01-08 15:07 周三
 */
@ChannelHandler.Sharable
public class PacketEncoder extends MessageToByteEncoder<Packet> {

    public static final PacketEncoder INSTANCE = new PacketEncoder();

    @Override
    protected void encode(ChannelHandlerContext context, Packet packet, ByteBuf buf) throws Exception {
        if (Command.HEARTBEAT.cmd == packet.cmd) {
            buf.writeByte(Packet.HEARTBEAT_PACKET_BYTE);
        } else {
            buf.writeInt(packet.getBodyLength());
            buf.writeByte(packet.cmd);
            buf.writeByte(packet.flags);
            buf.writeInt(packet.sessionId);
            if (packet.getBodyLength() > 0) {
                buf.writeBytes(packet.body);
            }
        }
        packet.body = null;
    }
}
