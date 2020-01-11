package org.tiger.cache.manager;

import lombok.extern.slf4j.Slf4j;
import org.tiger.api.spi.factory.cache.CacheManager;
import org.tiger.cache.connection.RedisConnectionFactory;
import org.tiger.tools.config.DefaultConfig;
import org.tiger.tools.exception.ServiceException;
import org.tiger.tools.utils.JsonUtil;
import redis.clients.jedis.*;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * {@link RedisCacheManager}
 *
 * @author SongQingWei
 * @since 1.0.0
 * 2020-01-10 11:01 周五
 */
@Slf4j
public class RedisCacheManager implements CacheManager {

    public static final RedisCacheManager INSTANCE = new RedisCacheManager();

    private final RedisConnectionFactory factory = new RedisConnectionFactory();

    @Override
    public void init() {
        log.info("begin init redis...");
        factory.setPoolConfig(DefaultConfig.Tiger.Redis.getPoolConfig(JedisPoolConfig.class));
        factory.setCluster(DefaultConfig.Tiger.Redis.isCluster());
        factory.setNodes(DefaultConfig.Tiger.Redis.NODES);
        factory.setPassword(DefaultConfig.Tiger.Redis.PASSWORD);
        if (DefaultConfig.Tiger.Redis.isSentinel()) {
            factory.setSentinelMaster(DefaultConfig.Tiger.Redis.SENTINEL_MASTER);
        }
        factory.init();
        testConnect();
        log.info("init redis success.");
    }

    @Override
    public void destroy() {
        factory.destroy();
    }

    private <R> R call(Function<JedisCommands, R> function, R r) {
        try {
            if (factory.isCluster()) {
                return function.apply(factory.getCluster());
            } else {
                Jedis jedis = factory.getJedisConnection();
                return function.apply(jedis);
            }
        } catch (Exception e) {
            log.error("redis occur exception", e);
        }
        return r;
    }

    private void call(Consumer<JedisCommands> consumer) {
        if (factory.isCluster()) {
            consumer.accept(factory.getCluster());
        } else {
            Jedis jedis = factory.getJedisConnection();
            consumer.accept(jedis);
        }
    }

    @Override
    public long incr(String key) {
        return call(jedis -> jedis.incr(key), 0L);
    }

    @Override
    public long incrBy(String key, long delta) {
        return call(jedis -> jedis.incrBy(key, delta), 0L);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T get(String key, Class<T> clazz) {
        String value = call(jedis -> jedis.get(key), null);
        if (value == null) {
            return null;
        }
        if (clazz == String.class) {
            return (T) value;
        }
        return JsonUtil.parseObject(value, clazz);
    }

    @Override
    public void set(String key, String value) {
        set(key, value, 0L, TimeUnit.SECONDS);
    }

    @Override
    public void set(String key, String value, long timeout, TimeUnit unit) {
        call(jedis -> {
            jedis.set(key, value);
            if (timeout > 0) {
                jedis.expire(key, (int) unit.toSeconds(timeout));
            }
        });
    }

    @Override
    public void set(String key, Object value) {
        set(key, value, 0L, TimeUnit.SECONDS);
    }

    @Override
    public void set(String key, Object value, long timeout, TimeUnit unit) {
        set(key, JsonUtil.toJson(value), timeout, unit);
    }

    @Override
    public void del(String key) {
        call(jedis -> jedis.del(key));
    }

    @Override
    public void hSet(String key, String field, String value) {
        call(jedis -> jedis.hset(key, field, value));
    }

    @Override
    public void hSet(String key, String field, Object value) {
        hSet(key, field, JsonUtil.toJson(value));
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T hGet(String key, String field, Class<T> clazz) {
        String value = call(jedis -> jedis.hget(key, field), null);
        if (value == null) {
            return null;
        }
        if (clazz == String.class) {
            return (T) value;
        }
        return JsonUtil.parseObject(value, clazz);
    }

    @Override
    public void hDel(String key, String... field) {
        call(jedis -> jedis.hdel(key, field));
    }

    @Override
    public Map<String, String> hGetAll(String key) {
        return call(jedis -> jedis.hgetAll(key), Collections.emptyMap());
    }

    @Override
    public <T> Map<String, T> hGetAll(String key, Class<T> clazz) {
        Map<String, String> map = call(jedis -> jedis.hgetAll(key), Collections.emptyMap());
        Map<String, T> all = new HashMap<>(map.size());
        map.forEach((k, v) -> all.put(k, JsonUtil.parseObject(v, clazz)));
        return all;
    }

    @Override
    public Set<String> hKeys(String key) {
        return call(jedis -> jedis.hkeys(key), Collections.emptySet());
    }

    @Override
    public <T> List<T> hmGet(String key, Class<T> clazz, String... fields) {
        return call(jedis -> jedis.hmget(key, fields), Collections.<String>emptyList())
                .stream()
                .map(s -> JsonUtil.parseObject(s, clazz))
                .collect(Collectors.toList());
    }

    @Override
    public void hmSet(String key, Map<String, String> hash, long timeout, TimeUnit unit) {
        call(jedis -> {
            jedis.hmset(key, hash);
            if (timeout > 0) {
                jedis.expire(key, (int) unit.toSeconds(timeout));
            }
        });
    }

    @Override
    public void hmSet(String key, Map<String, String> hash) {
        hmSet(key, hash, 0L, TimeUnit.MILLISECONDS);
    }

    @Override
    public long hIncrBy(String key, String field, long delta) {
        return call(jedis -> jedis.hincrBy(key, field, delta), 0L);
    }

    @Override
    public void lPush(String key, String... value) {
        call(jedis -> jedis.lpush(key, value));
    }

    @Override
    public void rPush(String key, String... value) {
        call(jedis -> jedis.rpush(key, value));
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T lPop(String key, Class<T> clazz) {
        String value = call(jedis -> jedis.lpop(key), null);
        if (value == null) {
            return null;
        }
        if (clazz == String.class) {
            return (T) value;
        }
        return JsonUtil.parseObject(value, clazz);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T rPop(String key, Class<T> clazz) {
        String value = call(jedis -> jedis.rpop(key), null);
        if (value == null) {
            return null;
        }
        if (clazz == String.class) {
            return (T) value;
        }
        return JsonUtil.parseObject(value, clazz);
    }

    @Override
    public <T> List<T> lRange(String key, long start, long end, Class<T> clazz) {
        List<String> list = call(jedis -> jedis.lrange(key, start, end), Collections.emptyList());
        return toList(list, clazz);
    }

    @Override
    public long lLen(String key) {
        return call(jedis -> jedis.llen(key), 0L);
    }

    @Override
    public void lRem(String key, String value) {
        call(jedis -> jedis.lrem(key, 0, value));
    }

    @Override
    public void sAdd(String key, String... value) {
        call(jedis -> jedis.sadd(key, value));
    }

    @Override
    public long sCard(String key) {
        return call(jedis -> jedis.scard(key), 0L);
    }

    @Override
    public void sRem(String key, String... value) {
        call(jedis -> jedis.srem(key, value));
    }

    @Override
    public <T> List<T> sScan(String key, Class<T> clazz, int start) {
        List<String> list = call(jedis -> jedis.sscan(key, Integer.toString(start), new ScanParams().count(10)).getResult(), null);
        return toList(list, clazz);
    }

    @Override
    public void zAdd(String key, String value) {
        call(jedis -> jedis.zadd(key, 0, value));
    }

    @Override
    public long zCard(String key) {
        return call(jedis -> jedis.zcard(key), 0L);
    }

    @Override
    public void zRem(String key, String... value) {
        call(jedis -> jedis.zrem(key, value));
    }

    @Override
    public <T> List<T> zRange(String key, int start, int end, Class<T> clazz) {
        Set<String> set = call(jedis -> jedis.zrange(key, start, end), Collections.emptySet());
        return toList(set, clazz);
    }

    @SuppressWarnings("unchecked")
    private <T> List<T> toList(Collection<String> collection, Class<T> clazz) {
        if (collection == null) {
            return null;
        }
        if (clazz == String.class) {
            return (List<T>) new ArrayList<>(collection);
        }
        return collection.stream()
                .map(str -> JsonUtil.parseObject(str, clazz))
                .collect(Collectors.toList());
    }

    private void testConnect() {
        if (factory.isCluster()) {
            JedisCluster cluster = factory.getCluster();
            if (cluster == null) {
                throw new ServiceException("init redis cluster error.");
            }
        } else {
            Jedis jedis = factory.getJedisConnection();
            if (jedis == null) {
                throw new ServiceException("init redis error, can not get connection.");
            }
            jedis.close();
        }
    }
}
