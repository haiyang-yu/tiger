package org.tiger.client.boot;

import org.tiger.api.listener.Listener;
import org.tiger.api.service.BaseService;
import org.tiger.client.connection.ConnectionClient;
import org.tiger.client.model.ClientConfig;
import org.tiger.spi.factory.cache.CacheManagerFactory;
import org.tiger.spi.factory.zk.ServiceDiscoveryFactory;

/**
 * {@link ConnectionClientBoot}
 *
 * @author SongQingWei
 * @since 1.0.0
 * 2020-01-17 10:48 周五
 */
public class ConnectionClientBoot extends BaseService {

    private final ConnectionClient client;

    public ConnectionClientBoot(ClientConfig config) {
        client = new ConnectionClient(config);
    }

    @Override
    protected void doStart(Listener listener) throws Exception {
        ServiceDiscoveryFactory.create().syncStart();
        CacheManagerFactory.create().syncStart();
        client.syncStart();
        listener.onSuccess();
    }

    @Override
    protected void doStop(Listener listener) throws Exception {
        ServiceDiscoveryFactory.create().syncStop();
        CacheManagerFactory.create().syncStop();
        client.syncStop();
        listener.onSuccess();
    }

    public ConnectionClient getClient() {
        return client;
    }
}
