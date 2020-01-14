package org.tiger.zk.factory;

import org.tiger.api.zk.discovery.ServiceDiscovery;
import org.tiger.spi.annotation.SPI;
import org.tiger.spi.factory.zk.ServiceDiscoveryFactory;
import org.tiger.zk.service.ZkServiceRegistryAndDiscovery;

/**
 * {@link ZkServiceDiscoveryFactory}
 *
 * @author SongQingWei
 * @since 1.0.0
 * 2020-01-14 15:38 周二
 */
@SPI
public class ZkServiceDiscoveryFactory implements ServiceDiscoveryFactory {

    @Override
    public ServiceDiscovery get() {
        return ZkServiceRegistryAndDiscovery.INSTANCE;
    }
}
