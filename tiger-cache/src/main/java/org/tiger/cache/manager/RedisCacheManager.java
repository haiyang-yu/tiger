package org.tiger.cache.manager;

import org.apache.commons.lang3.StringUtils;
import org.tiger.api.cache.CacheManager;
import org.tiger.api.listener.Listener;
import org.tiger.api.service.BaseService;
import org.tiger.cache.connection.RedisConnectionProvider;
import org.tiger.cache.exception.RedisException;
import org.tiger.common.config.TigerConfig;
import org.tiger.common.log.TigerLog;
import org.tiger.common.utils.JsonUtil;
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
 * 2020-01-14 11:36 周二
 */
public class RedisCacheManager extends BaseService implements CacheManager {

    public static final RedisCacheManager INSTANCE = new RedisCacheManager();

    private final RedisConnectionProvider provider = new RedisConnectionProvider();

    @Override
    public void init() {
        TigerLog.CACHE.info("begin init redis...");
        provider.setPoolConfig(TigerConfig.Tiger.Redis.getPoolConfig(JedisPoolConfig.class));
        provider.setCluster(TigerConfig.Tiger.Redis.isCluster());
        provider.setNodes(TigerConfig.Tiger.Redis.NODES);
        provider.setPassword(StringUtils.isBlank(TigerConfig.Tiger.Redis.PASSWORD) ? null : TigerConfig.Tiger.Redis.PASSWORD);
        provider.setDatabase(TigerConfig.Tiger.Redis.DATABASE);
        if (TigerConfig.Tiger.Redis.isSentinel()) {
            provider.setSentinelMaster(TigerConfig.Tiger.Redis.SENTINEL_MASTER);
        }
        provider.init();
        TigerLog.CACHE.info("init redis success.");
    }

    @Override
    public void start(Listener listener) {
        if (isRunning()) {
            listener.onSuccess();
        } else {
            super.start(listener);
        }
    }

    @Override
    public void stop(Listener listener) {
        if (isRunning()) {
            super.stop(listener);
        } else {
            listener.onSuccess();
        }
    }

    @Override
    protected void doStart(Listener listener) {
        testConnect(listener);
    }

    @Override
    protected void doStop(Listener listener) {
        provider.destroy(listener);
    }

    private <R> R call(Function<JedisCommands, R> function, R r) {
        try {
            if (provider.isCluster()) {
                return function.apply(provider.getCluster());
            } else {
                Jedis jedis = provider.getJedisConnection();
                return function.apply(jedis);
            }
        } catch (Exception e) {
            TigerLog.CACHE.error("redis occur exception", e);
        }
        return r;
    }

    private void call(Consumer<JedisCommands> consumer) {
        if (provider.isCluster()) {
            consumer.accept(provider.getCluster());
        } else {
            Jedis jedis = provider.getJedisConnection();
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

    @Override
    public String get(String key) {
        return call(jedis -> jedis.get(key), null);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T get(String key, Class<T> clazz) {
        if (clazz == String.class) {
            return (T) get(key);
        }
        String value = call(jedis -> jedis.get(key), null);
        if (value == null) {
            return null;
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

    private void testConnect(Listener listener) {
        if (provider.isCluster()) {
            JedisCluster cluster = provider.getCluster();
            if (Objects.isNull(cluster)) {
                listener.onFailure(new RedisException("init redis cluster error."));
            }
        } else {
            Jedis jedis = provider.getJedisConnection();
            if (Objects.isNull(jedis)) {
                listener.onFailure(new RedisException("init redis error, can not get connection."));
            }
            jedis.close();
        }
        listener.onSuccess(provider.getPort());
    }
}
