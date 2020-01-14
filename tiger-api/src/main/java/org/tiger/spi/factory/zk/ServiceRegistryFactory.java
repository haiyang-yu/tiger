package org.tiger.spi.factory.zk;

import org.tiger.api.zk.registry.ServiceRegistry;
import org.tiger.spi.ExtensionLoader;
import org.tiger.spi.factory.Factory;

/**
 * {@link ServiceRegistryFactory}
 *
 * @author SongQingWei
 * @since 1.0.0
 * 2020-01-14 15:34 周二
 */
public interface ServiceRegistryFactory extends Factory<ServiceRegistry> {

    /**
     * 加载服务注册组件
     * @return {@link ServiceRegistry}
     */
    static ServiceRegistry create() {
        return ExtensionLoader.loadExtensionClasses(ServiceRegistryFactory.class).get();
    }
}
