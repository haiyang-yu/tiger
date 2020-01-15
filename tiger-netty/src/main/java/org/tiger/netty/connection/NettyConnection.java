package org.tiger.netty.connection;

import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import org.tiger.api.connection.Connection;
import org.tiger.api.connection.SessionContext;
import org.tiger.api.protocol.Packet;
import org.tiger.common.log.TigerLog;
import org.tiger.spi.factory.crypto.RsaCipherFactory;

import java.util.Objects;

/**
 * {@link NettyConnection}
 *
 * @author SongQingWei
 * @since 1.0.0
 * 2020-01-14 16:06 周二
 */
public class NettyConnection implements Connection, ChannelFutureListener {

    private Channel channel;
    private SessionContext context;
    private volatile byte status = STATUS_NEW;
    private long lastReadTime;
    private long lastWriteTime;

    @Override
    public void operationComplete(ChannelFuture future) {
        if (future.isSuccess()) {
            updateLastWriteTime();
        } else {
            TigerLog.CONNECT.error("connection send msg error={}, connect={}", future.cause().getMessage(), this);
        }
    }

    @Override
    public void init(Channel channel, boolean security) {
        this.channel = channel;
        this.context = new SessionContext();
        this.lastReadTime = System.currentTimeMillis();
        this.status = STATUS_CONNECTED;
        if (security) {
            this.context.cipher = RsaCipherFactory.create();
        }
    }

    @Override
    public String getId() {
        return channel.id().asShortText();
    }

    @Override
    public ChannelFuture close() {
        if (status == STATUS_DISCONNECTED) {
            return null;
        }
        this.status = STATUS_DISCONNECTED;
        return this.channel.close();
    }

    @Override
    public boolean isConnected() {
        return status == STATUS_CONNECTED;
    }

    @Override
    public Channel getChannel() {
        return channel;
    }

    @Override
    public SessionContext getSessionContext() {
        return context;
    }

    @Override
    public void setSessionContext(SessionContext context) {
        this.context = context;
    }

    @Override
    public ChannelFuture send(Packet packet) {
        return send(packet, null);
    }

    @Override
    public ChannelFuture send(Packet packet, ChannelFutureListener listener) {
        if (channel.isActive()) {
            ChannelFuture future = channel.writeAndFlush(packet).addListener(this);
            if (Objects.nonNull(listener)) {
                future.addListener(listener);
            }
            if (channel.isWritable()) {
                return future;
            }
            if (!future.channel().eventLoop().inEventLoop()) {
                future.awaitUninterruptibly(200);
            }
            return future;
        }
        return close();
    }

    @Override
    public boolean isReadTimeout() {
        return System.currentTimeMillis() - lastReadTime > context.heartbeat + 1000;
    }

    @Override
    public boolean isWriteTimeout() {
        return System.currentTimeMillis() - lastWriteTime > context.heartbeat - 1000;
    }

    @Override
    public void updateLastReadTime() {
        this.lastReadTime = System.currentTimeMillis();
    }

    @Override
    public void updateLastWriteTime() {
        this.lastWriteTime = System.currentTimeMillis();
    }

    @Override
    public String toString() {
        return "NettyConnection{" +
                "channel=" + channel +
                ", context=" + context +
                ", status=" + status +
                ", lastReadTime=" + lastReadTime +
                ", lastWriteTime=" + lastWriteTime +
                '}';
    }
}
