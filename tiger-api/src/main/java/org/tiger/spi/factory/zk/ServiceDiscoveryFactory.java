package org.tiger.spi.factory.zk;

import org.tiger.api.zk.discovery.ServiceDiscovery;
import org.tiger.spi.ExtensionLoader;
import org.tiger.spi.factory.Factory;

/**
 * {@link ServiceDiscoveryFactory}
 *
 * @author SongQingWei
 * @since 1.0.0
 * 2020-01-14 15:36 周二
 */
public interface ServiceDiscoveryFactory extends Factory<ServiceDiscovery> {

    /**
     * 加载服务发现组建
     * @return {@link ServiceDiscovery}
     */
    static ServiceDiscovery create() {
        return ExtensionLoader.loadExtensionClasses(ServiceDiscoveryFactory.class).get();
    }
}
