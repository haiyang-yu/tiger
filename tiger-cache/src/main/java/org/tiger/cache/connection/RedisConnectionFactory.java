package org.tiger.cache.connection;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.tiger.tools.config.data.RedisNode;
import redis.clients.jedis.*;
import redis.clients.util.Pool;

import java.io.IOException;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * {@link RedisConnectionFactory}
 *
 * @author SongQingWei
 * @since 1.0.0
 * 2020-01-11 10:56 周六
 */
@Slf4j
@Data
public class RedisConnectionFactory {

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

    public void destroy() {
        if (pool != null) {
            pool.destroy();
            pool = null;
        }
        if (cluster != null) {
            try {
                cluster.close();
            } catch (IOException e) {
                log.warn("Cannot properly close Jedis cluster", e);
            }
            cluster = null;
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
        if (StringUtils.isNotEmpty(getPassword())) {
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
        return new JedisSentinelPool(sentinelMaster, hostAndPorts, poolConfig, getShardInfo().getSoTimeout(), password);
    }

    private Pool<Jedis> createRedisPool() {
        return new JedisPool(getPoolConfig(), host, port, shardInfo.getSoTimeout(), password);
    }

    private Jedis fetchJedisConnector() {
        if (pool != null) {
            return pool.getResource();
        }
        Jedis jedis = new Jedis(getShardInfo());
        jedis.connect();
        return jedis;
    }

    public void setNodes(List<RedisNode> nodes) {
        if (nodes == null || nodes.isEmpty()) {
            throw new IllegalArgumentException("redis server node can not be empty, please check your conf.");
        }
        this.nodes = nodes;
        this.host = nodes.get(0).getHost();
        this.port = nodes.get(0).getPort();
    }
}
