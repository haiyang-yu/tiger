package org.tiger.core;

import org.tiger.api.zk.node.ServiceNode;
import org.tiger.common.node.ServerNode;
import org.tiger.core.server.ConnectionServer;

/**
 * {@link TigerServer}
 *
 * @author SongQingWei
 * @since 1.0.0
 * 2020-01-15 10:41 周三
 */
public class TigerServer {

    private ServiceNode connectServiceNode;
    private ServiceNode gatewayServiceNode;
    private ServiceNode websocketServiceNode;

    private ConnectionServer connectionServer;

    public TigerServer() {
        connectServiceNode = ServerNode.connectService();
        gatewayServiceNode = ServerNode.gatewayService();
        websocketServiceNode = ServerNode.websocketService();

        connectionServer = new ConnectionServer(this);
    }

    public ServiceNode getConnectServiceNode() {
        return connectServiceNode;
    }

    public ServiceNode getGatewayServiceNode() {
        return gatewayServiceNode;
    }

    public ServiceNode getWebsocketServiceNode() {
        return websocketServiceNode;
    }

    public ConnectionServer getConnectionServer() {
        return connectionServer;
    }
}
