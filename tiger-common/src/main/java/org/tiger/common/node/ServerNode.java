package org.tiger.common.node;

import org.tiger.api.zk.node.CommonServiceNode;
import org.tiger.api.zk.node.ServiceName;
import org.tiger.api.zk.node.ServiceNode;
import org.tiger.common.config.ConfigUtil;

import static org.tiger.common.config.TigerConfig.Tiger.Net.*;

/**
 * {@link ServerNode}
 *
 * @author SongQingWei
 * @since 1.0.0
 * 2020-01-15 09:30 周三
 */
public class ServerNode {

    public static ServiceNode connectService() {
        CommonServiceNode node = new CommonServiceNode();
        node.setHost(ConfigUtil.getConnectServerRegisterIp());
        node.setPort(CONNECT_SERVER_PORT);
        node.setPersistent(false);
        node.setName(ServiceName.CONNECT_SERVER);
        return node;
    }

    public static ServiceNode gatewayService() {
        CommonServiceNode node = new CommonServiceNode();
        node.setHost(ConfigUtil.getGatewayServerRegisterIp());
        node.setPort(GATEWAY_SERVER_PORT);
        node.setPersistent(false);
        node.setName(ServiceName.GATEWAY_SERVER);
        return node;
    }

    public static ServiceNode websocketService() {
        CommonServiceNode node = new CommonServiceNode();
        node.setHost(ConfigUtil.getConnectServerRegisterIp());
        node.setPort(WEBSOCKET_SERVER_PORT);
        node.setPersistent(false);
        node.setName(ServiceName.WEBSERVICE_SERVER);
        return node;
    }
}
