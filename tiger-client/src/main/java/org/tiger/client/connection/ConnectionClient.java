package org.tiger.client.connection;

import io.netty.channel.ChannelHandler;
import org.tiger.api.connection.ConnectionManager;
import org.tiger.api.listener.Listener;
import org.tiger.client.config.ClientConfig;
import org.tiger.client.handler.ClientChannelHandler;
import org.tiger.common.constants.ThreadName;
import org.tiger.netty.client.BaseNettyTcpClient;

/**
 * {@link ConnectionClient}
 *
 * @author SongQingWei
 * @since 1.0.0
 * 2020-01-16 09:55 周四
 */
public class ConnectionClient extends BaseNettyTcpClient {

    private ClientChannelHandler channelHandler;
    private ConnectionManager connectionManager;

    public ConnectionClient(ClientConfig clientConfig) {
        this.connectionManager = new ClientConnectionManager();
        this.channelHandler = new ClientChannelHandler(clientConfig, connectionManager);
    }

    @Override
    public void init() {
        super.init();
        connectionManager.init();
    }

    @Override
    protected void doStop(Listener listener) throws Exception {
        super.doStop(listener);
        connectionManager.destroy();
    }

    @Override
    public ChannelHandler getChannelHandler() {
        return channelHandler;
    }

    @Override
    protected int getWorkThreadNumber() {
        return 1;
    }

    @Override
    protected String getWorkThreadName() {
        return ThreadName.T_TCP_CLIENT;
    }
}
