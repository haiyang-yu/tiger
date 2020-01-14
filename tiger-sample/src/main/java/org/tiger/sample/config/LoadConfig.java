package org.tiger.sample.config;

import org.tiger.common.config.TigerConfig;
import org.tiger.common.log.TigerLog;
import redis.clients.jedis.JedisPoolConfig;

/**
 * {@link LoadConfig}
 *
 * @author SongQingWei
 * @since 1.0.0
 * 2020-01-14 11:48 周二
 */
public class LoadConfig {

    public static void main(String[] args) {
        TigerLog.init();
        JedisPoolConfig config = TigerConfig.Tiger.Redis.getPoolConfig(JedisPoolConfig.class);

        System.out.println(config);
    }
}
