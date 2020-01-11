package org.tiger.netty.server.connection;

import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import lombok.extern.slf4j.Slf4j;
import org.tiger.api.connection.Connection;
import org.tiger.api.connection.SessionContext;
import org.tiger.api.spi.factory.security.RsaCipherFactory;
import org.tiger.tools.protocol.Packet;

/**
 * {@link NettyConnection}
 *
 * @author SongQingWei
 * @since 1.0.0
 * 2020-01-08 10:16 周三
 */
@Slf4j
public class NettyConnection implements Connection, ChannelFutureListener {

    private Channel channel;
    private SessionContext context;
    private volatile byte status = STATUS_NEW;
    private long lastReadTime;
    private long lastWriteTime;

    @Override
    public void init(Channel channel, boolean security) {
        this.channel = channel;
        this.context = new SessionContext();
        this.status = STATUS_CONNECTED;
        this.lastReadTime = System.currentTimeMillis();
        if (security) {
            this.context.changeCipher(RsaCipherFactory.create());
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
            ChannelFuture channelFuture = channel.writeAndFlush(packet).addListener(this);
            if (listener != null) {
                channelFuture.addListener(listener);
            }
            if (channel.isWritable()) {
                return channelFuture;
            }
            if (!channelFuture.channel().eventLoop().inEventLoop()) {
                channelFuture.awaitUninterruptibly(200);
            }
            return channelFuture;
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
    public void operationComplete(ChannelFuture future) {
        if (future.isSuccess()) {
            updateLastWriteTime();
        } else {
            log.error("connection send msg error", future.cause());
        }
    }
}
