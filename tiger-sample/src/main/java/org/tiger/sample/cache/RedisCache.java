package org.tiger.sample.cache;

import org.tiger.api.cache.CacheManager;
import org.tiger.common.log.TigerLog;
import org.tiger.spi.factory.cache.CacheManagerFactory;

/**
 * {@link RedisCache}
 *
 * @author SongQingWei
 * @since 1.0.0
 * 2020-01-14 11:51 周二
 */
public class RedisCache {

    public static void main(String[] args) {
        TigerLog.init();
        CacheManager manager = CacheManagerFactory.create();
        manager.init();
        manager.set("tiger:test", "测试");
        System.out.println(manager.get("tiger:test", String.class));
        manager.destroy();
    }
}
