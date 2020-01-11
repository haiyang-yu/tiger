package org.tiger.api.spi.factory.cache;

import org.tiger.api.spi.SpiLoader;
import org.tiger.api.spi.factory.Factory;

/**
 * {@link CacheManagerFactory}
 * 缓存管理工厂
 *
 * @author SongQingWei
 * @since 1.0.0
 * 2020-01-11 10:31 周六
 */
public interface CacheManagerFactory extends Factory<CacheManager> {

    /**
     * 加载缓存管理器
     *
     * @return {@link CacheManager}
     */
    static CacheManager create() {
        return SpiLoader.load(CacheManagerFactory.class).get();
    }
}
