package org.tiger.api.connection;

import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import org.tiger.tools.protocol.Packet;

/**
 * {@link Connection}
 *
 * @author SongQingWei
 * @since 1.0.0
 * 2020-01-08 09:24 周三
 */
public interface Connection {

    /** 初始化状态 */
    byte STATUS_NEW = 0;
    /** 连接状态 */
    byte STATUS_CONNECTED = 1;
    /** 关闭连接状态 */
    byte STATUS_DISCONNECTED = 2;

    /**
     * 初始化
     * @param channel netty channel
     * @param security 是否安全认证
     */
    void init(Channel channel, boolean security);

    /**
     * 返回channel的唯一ID
     * @return channel的唯一ID
     */
    String getId();

    /**
     * 关闭连接
     * @return 关闭连接结果
     */
    ChannelFuture close();

    /**
     * 是否为连接状态
     * @return true：连接中
     */
    boolean isConnected();

    /**
     * 返回消息通道
     * @return 消息通道channel
     */
    Channel getChannel();

    /**
     * 获取设备链接信息
     * @return SessionContext
     */
    SessionContext getSessionContext();

    /**
     * 设置设备链接信息
     * @param context SessionContext
     */
    void setSessionContext(SessionContext context);

    /**
     * 发送消息
     * @param packet 消息
     * @return 发送结果
     */
    ChannelFuture send(Packet packet);

    /**
     * 发送消息
     * @param packet 消息
     * @param listener 发送结果监听
     * @return 发送结果
     */
    ChannelFuture send(Packet packet, ChannelFutureListener listener);

    /**
     * 是否读超时
     * @return true：超时
     */
    boolean isReadTimeout();

    /**
     * 是否写超时
     * @return true：超时
     */
    boolean isWriteTimeout();

    /**
     * 更新最后读时间
     */
    void updateLastReadTime();

    /**
     * 更新最后写时间
     */
    void updateLastWriteTime();
}
