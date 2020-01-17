package org.tiger.api.message;

import io.netty.channel.ChannelFutureListener;
import org.tiger.api.connection.Connection;
import org.tiger.api.protocol.Packet;

/**
 * {@link Message}
 *
 * @author SongQingWei
 * @since 1.0.0
 * 2020-01-17 11:13 周五
 */
public interface Message {

    /**
     * 获取连接信息
     * @return {@link Connection}
     */
    Connection getConnection();

    /**
     * 获取数据包信息
     * @return {@link Packet}
     */
    Packet getPacket();

    /**
     * 编码消息
     */
    void encodeBody();

    /**
     * 解码消息
     */
    void decodeBody();

    /**
     * 发送消息, 并根据情况最body进行数据压缩、加密
     * @param listener 发送结果回调
     */
    void send(ChannelFutureListener listener);

    /**
     * 发送消息, 不会对body进行数据压缩、加密，原样发送
     * @param listener 发送结果回调
     */
    void sendRaw(ChannelFutureListener listener);
}
