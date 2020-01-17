package org.tiger.client;

import io.netty.channel.ChannelFuture;
import org.tiger.api.zk.node.ServiceName;
import org.tiger.api.zk.node.ServiceNode;
import org.tiger.client.boot.ConnectionClientBoot;
import org.tiger.client.connection.ConnectionClient;
import org.tiger.client.model.ClientConfig;
import org.tiger.common.config.TigerConfig;
import org.tiger.common.log.TigerLog;
import org.tiger.common.security.cipher.CipherBox;
import org.tiger.spi.factory.zk.ServiceDiscoveryFactory;

import java.util.List;
import java.util.UUID;

/**
 * {@link TigerClientApplication}
 *
 * @author SongQingWei
 * @since 1.0.0
 * 2020-01-16 18:28 周四
 */
public class TigerClientApplication {

    public static void main(String[] args) {
        TigerLog.init();
        ClientConfig config = new ClientConfig();
        config.setKey(CipherBox.INSTANCE.randomAesKey());
        config.setIv(CipherBox.INSTANCE.randomAesIv());
        config.setOsName(TigerConfig.CONFIG.getString("os.name"));
        config.setOsVersion(TigerConfig.CONFIG.getString("os.version"));
        config.setDeviceId(UUID.randomUUID().toString());
        config.setClientVersion("1.0.0");
        ConnectionClientBoot boot = new ConnectionClientBoot(config);
        boot.syncStart();
        List<ServiceNode> list = ServiceDiscoveryFactory.create().lookup(ServiceName.CONNECT_SERVER);
        if (list.isEmpty()) {
            TigerLog.CONSOLE.warn("No service information found");
            return;
        }
        int size = list.size();
        int index = (int) (Math.random() % size) * size;
        ServiceNode node = list.get(index);
        ConnectionClient client = new ConnectionClientBoot(config).getClient();
        ChannelFuture future = client.connect(node.getHost(), node.getPort());
        if (future.isSuccess()) {
            TigerLog.CONSOLE.info("netty client start success");
        } else {
            TigerLog.CONSOLE.error("netty client start failure", future.cause());
        }
    }
}
