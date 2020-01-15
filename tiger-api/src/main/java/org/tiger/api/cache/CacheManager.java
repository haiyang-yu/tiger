package org.tiger.api.cache;

import org.tiger.api.service.Server;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * {@link CacheManager}
 *
 * @author SongQingWei
 * @since 1.0.0
 * 2020-01-14 10:24 周二
 */
public interface CacheManager extends Server {

    /**
     * 自增值
     * @param key key
     * @return 自增后的值
     */
    long incr(String key);

    /**
     * 自增指定步长
     * @param key key
     * @param delta 步长
     * @return 自增后的值
     */
    long incrBy(String key, long delta);

    /**
     * 取出指定key的值
     * @param key key
     * @return key的值
     */
    String get(String key);

    /**
     * 取出指定key的值
     * @param key key
     * @param clazz Class
     * @param <T> 泛型
     * @return key的值
     */
    <T> T get(String key, Class<T> clazz);

    /**
     * 设置字符串
     * @param key key
     * @param value 字符串
     */
    void set(String key, String value);

    /**
     * 设置字符串，并设置过期时间
     * @param key key
     * @param value 字符串
     * @param timeout 过期时间
     * @param unit 时间单位
     */
    void set(String key, String value, long timeout, TimeUnit unit);

    /**
     * 设置对象
     * @param key key
     * @param value 对象
     */
    void set(String key, Object value);

    /**
     * 设置对象，并设置过期时间
     * @param key key
     * @param value 对象
     * @param timeout 过期时间
     * @param unit 时间单位
     */
    void set(String key, Object value, long timeout, TimeUnit unit);

    /**
     * 删除指定的key
     * @param key key
     */
    void del(String key);

    /**
     * 设置hash类型的字符串
     * @param key key
     * @param field field
     * @param value 字符串
     */
    void hSet(String key, String field, String value);

    /**
     * 设置hash类型的对象
     * @param key key
     * @param field field
     * @param value 对象
     */
    void hSet(String key, String field, Object value);

    /**
     * 获取hash值
     * @param key key
     * @param field field
     * @param clazz Class
     * @param <T> 泛型
     * @return T
     */
    <T> T hGet(String key, String field, Class<T> clazz);

    /**
     * 删除hash对应的key
     * @param key key
     * @param field field
     */
    void hDel(String key, String... field);

    /**
     * 获取hash中key对应的所有值
     * @param key key
     * @return key对应的值
     */
    Map<String, String> hGetAll(String key);

    /**
     * 获取hash中key对应的所有值
     * @param key key
     * @param clazz Class
     * @return key对应的值
     */
    <T> Map<String, T> hGetAll(String key, Class<T> clazz);

    /**
     * 返回 key 指定的哈希集中所有字段的名字。
     * @param key key
     * @return 字段的名字
     */
    Set<String> hKeys(String key);

    /**
     * 返回 key 指定的哈希集中指定字段的值
     * @param key key
     * @param clazz Class
     * @param fields 字段
     * @param <T> 泛型
     * @return 值
     */
    <T> List<T> hmGet(String key, Class<T> clazz, String... fields);

    /**
     * 设置 key 指定的哈希集中指定字段的值。该命令将重写所有在哈希集中存在的字段。
     * 如果 key 指定的哈希集不存在，会创建一个新的哈希集并与 key关联
     * @param key key
     * @param hash 字段与值
     * @param timeout 过期时间
     * @param unit 过期单位
     */
    void hmSet(String key, Map<String, String> hash, long timeout, TimeUnit unit);

    /**
     * 设置 key 指定的哈希集中指定字段的值。该命令将重写所有在哈希集中存在的字段。
     * 如果 key 指定的哈希集不存在，会创建一个新的哈希集并与 key关联
     * @param key key
     * @param hash 字段与值
     */
    void hmSet(String key, Map<String, String> hash);

    /**
     * 增加 key 指定的哈希集中指定字段的数值
     * 如果 key 不存在，会创建一个新的哈希集并与 key 关联。如果字段不存在，则字段的值在该操作执行前被设置为 0
     * @param key key
     * @param field 字段
     * @param delta 字段的数值
     * @return 操作后的值
     */
    long hIncrBy(String key, String field, long delta);

    /**
     * 从队列的左边入队
     * @param key key
     * @param value value
     */
    void lPush(String key, String... value);

    /**
     * 从队列的右边入队
     * @param key key
     * @param value value
     */
    void rPush(String key, String... value);

    /**
     * 移除并且返回 key 对应的 list 的第一个元素
     * @param key key
     * @param clazz Class
     * @param <T> 泛型
     * @return 第一个元素
     */
    <T> T lPop(String key, Class<T> clazz);

    /**
     * 移除并且返回 key 对应的 list 的最后一个元素
     * @param key key
     * @param clazz Class
     * @param <T> 泛型
     * @return 最后一个元素
     */
    <T> T rPop(String key, Class<T> clazz);

    /**
     * 从列表中获取指定返回的元素 start 和 end
     * 偏移量都是基于0的下标，即list的第一个元素下标是0（list的表头），第二个元素下标是1，以此类推。
     * 偏移量也可以是负数，表示偏移量是从list尾部开始计数。 例如， -1 表示列表的最后一个元素，-2 是倒数第二个，以此类推。
     * @param key key
     * @param start start
     * @param end end
     * @param clazz Class
     * @param <T> T
     * @return List
     */
    <T> List<T> lRange(String key, long start, long end, Class<T> clazz);

    /**
     * 返回存储在 key 里的list的长度。
     * @param key key
     * @return list的长度
     */
    long lLen(String key);

    /**
     * 移除表中所有与 value 相等的值
     * @param key key
     * @param value value
     */
    void lRem(String key, String value);

    /**
     * 添加一个或多个指定的member元素到集合的 key中
     * @param key key
     * @param value value
     */
    void sAdd(String key, String... value);

    /**
     * 返回集合存储的key的基数 (集合元素的数量).
     * @param key key
     * @return 集合元素的数量
     */
    long sCard(String key);

    /**
     * 在key集合中移除指定的元素. 如果指定的元素不是key集合中的元素则忽略 如果key集合不存在则被视为一个空的集合，该命令返回0.
     * @param key key
     * @param value value
     */
    void sRem(String key, String... value);

    /**
     * 迭代SET集合中的元素, 默认使用每页10个
     * @param key key
     * @param clazz Class
     * @param start start
     * @param <T> T
     * @return List
     */
    <T> List<T> sScan(String key, Class<T> clazz, int start);

    /**
     * 将所有指定成员添加到键为key有序集合（sorted set）里面
     * @param key key
     * @param value value
     */
    void zAdd(String key, String value);

    /**
     * 返回有序集合存储的key的基数 (有序集合元素的数量).
     * @param key key
     * @return 有序集合元素的数量
     */
    long zCard(String key);

    /**
     * 在key集合中移除指定的元素
     * @param key key
     * @param value value
     */
    void zRem(String key, String... value);

    /**
     * 从列表中获取指定返回的元素 start 和 end
     * @param key key
     * @param start start
     * @param end end
     * @param clazz Class
     * @param <T> T
     * @return List
     */
    <T> List<T> zRange(String key, int start, int end, Class<T> clazz);
}
