package org.tiger.sample.client;

import io.netty.channel.ChannelFuture;
import org.tiger.api.zk.discovery.ServiceDiscovery;
import org.tiger.api.zk.node.ServiceName;
import org.tiger.api.zk.node.ServiceNode;
import org.tiger.client.config.ClientConfig;
import org.tiger.client.connection.ConnectionClient;
import org.tiger.common.config.TigerConfig;
import org.tiger.common.log.TigerLog;
import org.tiger.common.security.cipher.CipherBox;
import org.tiger.spi.factory.zk.ServiceDiscoveryFactory;

import java.util.List;
import java.util.UUID;

/**
 * {@link ClientTest}
 *
 * @author SongQingWei
 * @since 1.0.0
 * 2020-01-16 16:09 周四
 */
public class ClientTest {

    public static void main(String[] args) {
        TigerLog.init();
        ServiceDiscovery discovery = ServiceDiscoveryFactory.create();
        discovery.syncStart();
        List<ServiceNode> list = discovery.lookup(ServiceName.CONNECT_SERVER);
        if (list.isEmpty()) {
            TigerLog.CONSOLE.warn("No service started");
            return;
        }
        ClientConfig config = new ClientConfig();
        config.setOsName(TigerConfig.CONFIG.getString("os.name"));
        config.setOsVersion(TigerConfig.CONFIG.getString("os.version"));
        config.setDeviceId(UUID.randomUUID().toString());
        config.setClientVersion("1.0.0");
        config.setKey(CipherBox.INSTANCE.randomAesKey());
        config.setIv(CipherBox.INSTANCE.randomAesIv());
        ConnectionClient client = new ConnectionClient(config);
        client.syncStart();
        int size = list.size();
        int index = (int) (Math.random() % size) * size;
        ServiceNode node = list.get(index);
        ChannelFuture future = client.connect(node.getHost(), node.getPort()).awaitUninterruptibly();
        if (future.isSuccess()) {
            TigerLog.CONSOLE.info("client start success");
        } else {
            TigerLog.CONSOLE.error("client start failure", future.cause());
        }
    }
}
