package org.tiger.cache.factory;

import org.tiger.api.cache.CacheManager;
import org.tiger.cache.manager.RedisCacheManager;
import org.tiger.spi.annotation.SPI;
import org.tiger.spi.factory.cache.CacheManagerFactory;

/**
 * {@link RedisCacheManagerFactory}
 *
 * @author SongQingWei
 * @since 1.0.0
 * 2020-01-14 10:27 周二
 */
@SPI
public class RedisCacheManagerFactory implements CacheManagerFactory {

    @Override
    public CacheManager get() {
        return RedisCacheManager.INSTANCE;
    }
}
