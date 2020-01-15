package org.tiger.boot;

import org.tiger.boot.job.BootChain;
import org.tiger.boot.job.ServiceBootJob;
import org.tiger.core.TigerServer;
import org.tiger.spi.factory.cache.CacheManagerFactory;
import org.tiger.spi.factory.zk.ServiceDiscoveryFactory;
import org.tiger.spi.factory.zk.ServiceRegistryFactory;

import java.util.Objects;

/**
 * {@link ServerLauncher}
 *
 * @author SongQingWei
 * @since 1.0.0
 * 2020-01-14 18:27 周二
 */
public class ServerLauncher {

    private TigerServer tigerServer;
    private BootChain chain;

    public void init() {
        if (Objects.isNull(tigerServer)) {
            tigerServer = new TigerServer();
        }
        if (Objects.isNull(chain)) {
            chain = BootChain.chain();
        }
        chain.setNext(new ServiceBootJob(CacheManagerFactory.create()))
                .setNext(new ServiceBootJob(ServiceRegistryFactory.create()))
                .setNext(new ServiceBootJob(ServiceDiscoveryFactory.create()))
                .setNext(new ServiceBootJob(tigerServer.getConnectionServer(), tigerServer.getConnectServiceNode()))
                .end();
    }

    public void start() {
        chain.start();
    }

    public void stop() {
        chain.stop();
    }
}
