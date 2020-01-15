package org.tiger.cache.connection;

import org.apache.commons.lang3.StringUtils;
import org.tiger.api.listener.Listener;
import org.tiger.cache.exception.RedisException;
import org.tiger.common.config.data.RedisNode;
import org.tiger.common.log.TigerLog;
import redis.clients.jedis.*;
import redis.clients.util.Pool;

import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * {@link RedisConnectionProvider}
 *
 * @author SongQingWei
 * @since 1.0.0
 * 2020-01-14 10:28 周二
 */
public class RedisConnectionProvider {

    private String host = Protocol.DEFAULT_HOST;
    private int port = Protocol.DEFAULT_PORT;
    private int timeout = Protocol.DEFAULT_TIMEOUT;
    private String password;

    private String sentinelMaster;
    private List<RedisNode> nodes;

    private boolean isCluster = false;
    private int database = Protocol.DEFAULT_DATABASE;

    private JedisShardInfo shardInfo;
    private Pool<Jedis> pool;
    private JedisCluster cluster;
    private JedisPoolConfig poolConfig = new JedisPoolConfig();

    public void init() {
        if (shardInfo == null) {
            shardInfo = new JedisShardInfo(host, port);
            if (StringUtils.isNotBlank(password)) {
                shardInfo.setPassword(password);
            }
            if (timeout > 0) {
                shardInfo.setConnectionTimeout(timeout);
            }
        }
        if (isCluster) {
            this.cluster = createCluster();
        } else {
            this.pool = createPool();
        }
    }

    public void destroy(Listener listener) {
        if (pool != null) {
            pool.destroy();
            pool = null;
        }
        if (cluster != null) {
            try {
                cluster.close();
            } catch (IOException e) {
                if (Objects.nonNull(listener)) {
                    listener.onFailure(new RedisException("Cannot properly close Jedis cluster", e));
                }
                TigerLog.CACHE.warn("Cannot properly close Jedis cluster", e);
            }
            cluster = null;
        }
        if (Objects.nonNull(listener)) {
            listener.onSuccess(port);
        }
    }

    public Jedis getJedisConnection() {
        Jedis jedis = fetchJedisConnector();
        if (database > 0 && jedis != null) {
            jedis.select(database);
        }
        return jedis;
    }

    private JedisCluster createCluster() {
        Set<HostAndPort> hostAndPorts = nodes
                .stream()
                .map(redisNode -> new HostAndPort(redisNode.getHost(), redisNode.getPort()))
                .collect(Collectors.toSet());
        if (StringUtils.isNotEmpty(password)) {
            throw new IllegalArgumentException("Jedis does not support password protected Redis Cluster configurations!");
        }
        int redirects = 5;
        return new JedisCluster(hostAndPorts, timeout, redirects, poolConfig);
    }

    private Pool<Jedis> createPool() {
        if (StringUtils.isNotBlank(sentinelMaster)) {
            return createRedisSentinelPool();
        }
        return createRedisPool();
    }

    private Pool<Jedis> createRedisSentinelPool() {
        Set<String> hostAndPorts = nodes
                .stream()
                .map(redisNode -> new HostAndPort(redisNode.getHost(), redisNode.getPort()).toString())
                .collect(Collectors.toSet());
        return new JedisSentinelPool(sentinelMaster, hostAndPorts, poolConfig, timeout, password);
    }

    private Pool<Jedis> createRedisPool() {
        return new JedisPool(poolConfig, host, port, timeout, password);
    }

    private Jedis fetchJedisConnector() {
        if (pool != null) {
            return pool.getResource();
        }
        Jedis jedis = new Jedis(shardInfo);
        jedis.connect();
        return jedis;
    }

    public int getPort() {
        return port;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setSentinelMaster(String sentinelMaster) {
        this.sentinelMaster = sentinelMaster;
    }

    public void setNodes(List<RedisNode> nodes) {
        if (nodes == null || nodes.isEmpty()) {
            throw new IllegalArgumentException("redis server node can not be empty, please check your conf.");
        }
        this.nodes = nodes;
        this.host = nodes.get(0).getHost();
        this.port = nodes.get(0).getPort();
    }

    public boolean isCluster() {
        return isCluster;
    }

    public void setCluster(boolean cluster) {
        isCluster = cluster;
    }

    public JedisCluster getCluster() {
        return cluster;
    }

    public void setDatabase(int database) {
        this.database = database;
    }

    public void setPoolConfig(JedisPoolConfig poolConfig) {
        this.poolConfig = poolConfig;
    }
}
