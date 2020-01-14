package org.tiger.zk.connection;

import static org.tiger.common.config.TigerConfig.Tiger.Zk.*;

/**
 * {@link ZkConfig}
 *
 * @author SongQingWei
 * @since 1.0.0
 * 2020-01-14 14:48 周二
 */
public class ZkConfig {

    /**
     * 最大重试次数
     */
    public static final int ZK_MAX_RETRY = 3;
    /**
     * 最小睡眠时间
     */
    public static final int ZK_MIN_TIME = 5000;
    /**
     * 最大睡眠时间
     */
    public static final int ZK_MAX_TIME = 5000;
    /**
     * 会话超时时间
     */
    public static final int ZK_SESSION_TIMEOUT = 5000;
    /**
     * 连接超时时间
     */
    public static final int ZK_CONNECTION_TIMEOUT = 5000;
    /**
     * 默认监听路径
     */
    public static final String ZK_DEFAULT_CACHE_PATH = "/";

    private String hosts;

    private String digest;

    private String namespace;

    private int maxRetries = ZK_MAX_RETRY;

    private int baseSleepTimeMs = ZK_MIN_TIME;

    private int maxSleepMs = ZK_MAX_TIME;

    private int sessionTimeout = ZK_SESSION_TIMEOUT;

    private int connectionTimeout = ZK_CONNECTION_TIMEOUT;

    private String watchPath = ZK_DEFAULT_CACHE_PATH;

    public ZkConfig(String hosts) {
        this.hosts = hosts;
    }

    public static ZkConfig build() {
        return new ZkConfig(SERVER_ADDRESS)
                .setDigest(DIGEST)
                .setWatchPath(WATCH_PATH)
                .setNamespace(NAMESPACE)
                .setConnectionTimeout(CONNECTION_TIMEOUT)
                .setSessionTimeout(SESSION_TIMEOUT)
                .setMaxRetries(Retry.MAX_RETRIES)
                .setMaxSleepMs(Retry.MAX_SLEEP)
                .setBaseSleepTimeMs(Retry.BASE_SLEEP_TIME);
    }

    public String getHosts() {
        return hosts;
    }

    public String getDigest() {
        return digest;
    }

    public ZkConfig setDigest(String digest) {
        this.digest = digest;
        return this;
    }

    public String getNamespace() {
        return namespace;
    }

    public ZkConfig setNamespace(String namespace) {
        this.namespace = namespace;
        return this;
    }

    public int getMaxRetries() {
        return maxRetries;
    }

    public ZkConfig setMaxRetries(int maxRetries) {
        this.maxRetries = maxRetries;
        return this;
    }

    public int getBaseSleepTimeMs() {
        return baseSleepTimeMs;
    }

    public ZkConfig setBaseSleepTimeMs(int baseSleepTimeMs) {
        this.baseSleepTimeMs = baseSleepTimeMs;
        return this;
    }

    public int getMaxSleepMs() {
        return maxSleepMs;
    }

    public ZkConfig setMaxSleepMs(int maxSleepMs) {
        this.maxSleepMs = maxSleepMs;
        return this;
    }

    public int getSessionTimeout() {
        return sessionTimeout;
    }

    public ZkConfig setSessionTimeout(int sessionTimeout) {
        this.sessionTimeout = sessionTimeout;
        return this;
    }

    public int getConnectionTimeout() {
        return connectionTimeout;
    }

    public ZkConfig setConnectionTimeout(int connectionTimeout) {
        this.connectionTimeout = connectionTimeout;
        return this;
    }

    public String getWatchPath() {
        return watchPath;
    }

    public ZkConfig setWatchPath(String watchPath) {
        this.watchPath = watchPath;
        return this;
    }

    @Override
    public String toString() {
        return "ZkConfig{" +
                "hosts='" + hosts + '\'' +
                ", digest='" + digest + '\'' +
                ", namespace='" + namespace + '\'' +
                ", maxRetries=" + maxRetries +
                ", baseSleepTimeMs=" + baseSleepTimeMs +
                ", maxSleepMs=" + maxSleepMs +
                ", sessionTimeout=" + sessionTimeout +
                ", connectionTimeout=" + connectionTimeout +
                ", watchPath='" + watchPath + '\'' +
                '}';
    }
}
