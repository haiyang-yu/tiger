package org.tiger.spi.factory.cache;

import org.tiger.api.cache.CacheManager;
import org.tiger.spi.ExtensionLoader;
import org.tiger.spi.factory.Factory;

/**
 * {@link CacheManagerFactory}
 *
 * @author SongQingWei
 * @since 1.0.0
 * 2020-01-14 10:25 周二
 */
public interface CacheManagerFactory extends Factory<CacheManager> {

    /**
     * 加载缓存
     * @return {@link CacheManager}
     */
    static CacheManager create() {
        return ExtensionLoader.loadExtensionClasses(CacheManagerFactory.class).get();
    }
}
