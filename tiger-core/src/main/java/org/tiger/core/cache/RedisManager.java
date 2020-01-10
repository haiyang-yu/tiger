package org.tiger.core.cache;

import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ScanOptions;
import org.tiger.api.cache.CacheManager;
import org.tiger.tools.utils.JsonUtil;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * {@link RedisManager}
 *
 * @author SongQingWei
 * @since 1.0.0
 * 2020-01-10 11:01 周五
 */
public class RedisManager implements CacheManager {

    private final RedisTemplate<String, String> redisTemplate;

    public RedisManager(RedisTemplate<String, String> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    private <R> R call(Function<RedisTemplate<String, String>, R> function, R r) {
        try {
            return function.apply(redisTemplate);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return r;
    }

    private void call(Consumer<RedisTemplate<String, String>> consumer) {
        consumer.accept(redisTemplate);
    }

    @Override
    public long incr(String key) {
        return call(redisTemplate -> redisTemplate.opsForValue().increment(key), 0L);
    }

    @Override
    public long incrBy(String key, long delta) {
        return call(redisTemplate -> redisTemplate.opsForValue().increment(key, delta), 0L);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T get(String key, Class<T> clazz) {
        String value = call(redisTemplate -> redisTemplate.opsForValue().get(key), null);
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
        set(key, value, 0, TimeUnit.SECONDS);
    }

    @Override
    public void set(String key, String value, int timeout, TimeUnit unit) {
        call(redisTemplate -> {
            if (timeout > 0) {
                redisTemplate.opsForValue().set(key, value, timeout, unit);
            } else {
                redisTemplate.opsForValue().set(key, value);
            }
        });
    }

    @Override
    public void set(String key, Object value) {
        set(key, value, 0, TimeUnit.SECONDS);
    }

    @Override
    public void set(String key, Object value, int timeout, TimeUnit unit) {
        set(key, JsonUtil.toJson(value), timeout, unit);
    }

    @Override
    public void del(String key) {
        call(redisTemplate -> redisTemplate.delete(key));
    }

    @Override
    public void hSet(String key, String field, String value) {
        call(redisTemplate -> redisTemplate.opsForHash().put(key, field, value));
    }

    @Override
    public void hSet(String key, String field, Object value) {
        hSet(key, field, JsonUtil.toJson(value));
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T hGet(String key, String field, Class<T> clazz) {
        String value = (String) call(redisTemplate -> redisTemplate.opsForHash().get(key, field), null);
        if (value == null) {
            return null;
        }
        if (clazz == String.class) {
            return (T) value;
        }
        return JsonUtil.parseObject(value, clazz);
    }

    @Override
    public void hDel(String key, String field) {
        call(redisTemplate -> redisTemplate.opsForHash().delete(key, field));
    }

    @Override
    public Map<String, String> hGetAll(String key) {
        Map<Object, Object> map = call(redisTemplate -> redisTemplate.opsForHash().entries(key), Collections.emptyMap());
        Map<String, String> all = new HashMap<>(map.size());
        map.forEach((k, v) -> all.put(k.toString(), v.toString()));
        return all;
    }

    @Override
    public <T> Map<String, T> hGetAll(String key, Class<T> clazz) {
        Map<Object, Object> map = call(redisTemplate -> redisTemplate.opsForHash().entries(key), Collections.emptyMap());
        Map<String, T> all = new HashMap<>(map.size());
        map.forEach((k, v) -> all.put(k.toString(), JsonUtil.parseObject(v.toString(), clazz)));
        return all;
    }

    @Override
    public Set<String> hKeys(String key) {
        Set<Object> set = call(redisTemplate -> redisTemplate.opsForHash().keys(key), Collections.emptySet());
        return set.stream().map(Object::toString).collect(Collectors.toSet());
    }

    @Override
    public <T> List<T> hmGet(String key, Class<T> clazz, String... fields) {
        return call(redisTemplate -> redisTemplate.opsForHash().multiGet(key, Arrays.asList(fields)), Collections.emptyList())
                .stream()
                .map(o -> JsonUtil.parseObject(o.toString(), clazz))
                .collect(Collectors.toList());
    }

    @Override
    public void hmSet(String key, Map<String, String> hash, int timeout) {
        hmSet(key, hash);
        call(redisTemplate -> {
            redisTemplate.opsForHash().putAll(key, hash);
            if (timeout > 0) {
                redisTemplate.expire(key, timeout, TimeUnit.SECONDS);
            }
        });
    }

    @Override
    public void hmSet(String key, Map<String, String> hash) {
        hmSet(key, hash, 0);
    }

    @Override
    public long hIncrBy(String key, String field, long delta) {
        return call(redisTemplate -> redisTemplate.opsForHash().increment(key, field, delta), 0L);
    }

    @Override
    public void lPush(String key, String... value) {
        call(redisTemplate -> redisTemplate.opsForList().leftPushAll(key, value));
    }

    @Override
    public void lPush(String key, Object value) {
        call(redisTemplate -> redisTemplate.opsForList().leftPush(key, Objects.requireNonNull(JsonUtil.toJson(value))));
    }

    @Override
    public void rPush(String key, String... value) {
        call(redisTemplate -> redisTemplate.opsForList().rightPushAll(key, value));
    }

    @Override
    public void rPush(String key, Object value) {
        call(redisTemplate -> redisTemplate.opsForList().rightPush(key, Objects.requireNonNull(JsonUtil.toJson(value))));
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T lPop(String key, Class<T> clazz) {
        String value = call(redisTemplate -> redisTemplate.opsForList().leftPop(key), null);
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
        String value = call(redisTemplate -> redisTemplate.opsForList().rightPop(key), null);
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
        List<String> list = call(redisTemplate -> redisTemplate.opsForList().range(key, start, end), Collections.emptyList());
        return toList(list, clazz);
    }

    @Override
    public long lLen(String key) {
        return call(redisTemplate -> redisTemplate.opsForList().size(key), 0L);
    }

    @Override
    public void lRem(String key, String value) {
        call(redisTemplate -> redisTemplate.opsForList().remove(key, 0, value));
    }

    @Override
    public void sAdd(String key, String... value) {
        call(redisTemplate -> redisTemplate.opsForSet().add(key, value));
    }

    @Override
    public long sCard(String key) {
        return call(redisTemplate -> redisTemplate.opsForSet().size(key), 0L);
    }

    @Override
    public void sRem(String key, String value) {
        call(redisTemplate -> redisTemplate.opsForSet().remove(key, value));
    }

    @Override
    public <T> List<T> sScan(String key, Class<T> clazz, int start) {
        List<String> list = call(redisTemplate -> {
            Cursor<String> cursor = redisTemplate.opsForSet().scan(key, ScanOptions.scanOptions().count(10).build());
            List<String> resList = new ArrayList<>();
            while (cursor.hasNext()) {
                resList.add(cursor.next());
            }
            return resList;
        }, Collections.emptyList());
        return toList(list, clazz);
    }

    @Override
    public void zAdd(String key, String value) {
        call(redisTemplate -> redisTemplate.opsForZSet().add(key, value, 0));
    }

    @Override
    public long zCard(String key) {
        return call(redisTemplate -> redisTemplate.opsForZSet().size(key), 0L);
    }

    @Override
    public void zRem(String key, Object... value) {
        call(redisTemplate -> redisTemplate.opsForZSet().remove(key, value));
    }

    @Override
    public <T> List<T> zRange(String key, int start, int end, Class<T> clazz) {
        Set<String> set = call(redisTemplate -> redisTemplate.opsForZSet().range(key, start, end), Collections.emptySet());
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
}
