package org.tiger.zk.factory;

import org.tiger.api.zk.registry.ServiceRegistry;
import org.tiger.spi.annotation.SPI;
import org.tiger.spi.factory.zk.ServiceRegistryFactory;
import org.tiger.zk.service.ZkServiceRegistryAndDiscovery;

/**
 * {@link ZkServiceRegistryFactory}
 *
 * @author SongQingWei
 * @since 1.0.0
 * 2020-01-14 15:34 周二
 */
@SPI
public class ZkServiceRegistryFactory implements ServiceRegistryFactory {

    @Override
    public ServiceRegistry get() {
        return ZkServiceRegistryAndDiscovery.INSTANCE;
    }
}
