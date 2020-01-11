package org.tiger.cache.manager;

import org.tiger.api.spi.annotation.Spi;
import org.tiger.api.spi.factory.cache.CacheManager;
import org.tiger.api.spi.factory.cache.CacheManagerFactory;

/**
 * {@link RedisCacheManagerFactory}
 * redis缓存工厂实现
 *
 * @author SongQingWei
 * @since 1.0.0
 * 2020-01-11 10:36 周六
 */
@Spi
public class RedisCacheManagerFactory implements CacheManagerFactory {

    @Override
    public CacheManager get() {
        return RedisCacheManager.INSTANCE;
    }
}
