package org.tiger.zk.connection;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.api.ACLProvider;
import org.apache.curator.framework.recipes.cache.ChildData;
import org.apache.curator.framework.recipes.cache.TreeCache;
import org.apache.curator.framework.recipes.cache.TreeCacheListener;
import org.apache.curator.framework.state.ConnectionState;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.ZooDefs;
import org.apache.zookeeper.data.ACL;
import org.tiger.api.listener.Listener;
import org.tiger.api.service.BaseService;
import org.tiger.common.log.TigerLog;
import org.tiger.zk.exception.ZkException;

import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

/**
 * {@link ZkClient}
 *
 * @author SongQingWei
 * @since 1.0.0
 * 2020-01-14 15:16 周二
 */
public class ZkClient extends BaseService {

    public static final ZkClient INSTANCE = getInstance();

    private ZkConfig config;
    private CuratorFramework framework;
    private TreeCache cache;
    /**
     * 临时节点
     */
    private final Map<String, String> ephemeralNodes = new ConcurrentHashMap<>();
    /**
     * 临时顺序节点
     */
    private final Map<String, String> ephemeralSequentialNodes = new ConcurrentHashMap<>();

    private synchronized static ZkClient getInstance() {
        return INSTANCE == null ? new ZkClient() : INSTANCE;
    }

    @Override
    public void init() {
        if (framework != null) {
            return;
        }
        if (config == null) {
            config = ZkConfig.build();
        }
        CuratorFrameworkFactory.Builder builder = CuratorFrameworkFactory.builder()
                .connectString(config.getHosts())
                .retryPolicy(new ExponentialBackoffRetry(config.getBaseSleepTimeMs(), config.getMaxRetries(), config.getMaxSleepMs()))
                .namespace(config.getNamespace());
        if (config.getConnectionTimeout() > 0) {
            builder.connectionTimeoutMs(config.getConnectionTimeout());
        }
        if (config.getSessionTimeout() > 0) {
            builder.sessionTimeoutMs(config.getSessionTimeout());
        }
        if (config.getDigest() != null) {
            builder.authorization("digest", config.getDigest().getBytes(StandardCharsets.UTF_8));
            builder.aclProvider(new ACLProvider() {
                @Override
                public List<ACL> getDefaultAcl() {
                    return ZooDefs.Ids.CREATOR_ALL_ACL;
                }

                @Override
                public List<ACL> getAclForPath(String path) {
                    return ZooDefs.Ids.CREATOR_ALL_ACL;
                }
            });
        }
        framework = builder.build();
        TigerLog.ZK.info("init zookeeper client success, config={}", config);
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
        framework.start();
        TigerLog.ZK.info("start zookeeper client waiting for connected...");
        try {
            boolean connectedRes = framework.blockUntilConnected(1, TimeUnit.MINUTES);
            if (!connectedRes) {
                throw new ZkException("start zookeeper error, config=" + config);
            }
            initLocalCache(config.getWatchPath());
            addConnectionStateListener();
            TigerLog.ZK.info("zookeeper client start success, server lists is:{}", config.getHosts());
            listener.onSuccess(config.getHosts());
        } catch (Exception e) {
            listener.onFailure(e);
            throw new ZkException("start zookeeper error, config=" + config);
        }
    }

    @Override
    protected void doStop(Listener listener) {
        try {
            TigerLog.ZK.info("zookeeper client waiting for closed...");
            if (cache != null) {
                cache.close();
            }
            TimeUnit.MILLISECONDS.sleep(600);
            framework.close();
            TigerLog.ZK.info("zookeeper client closed success.");
            listener.onSuccess();
        } catch (InterruptedException e) {
            listener.onFailure(e);
        }
    }

    /**
     * 初始化本地缓存
     * @param watchPath 监听路径
     * @throws Exception e
     */
    private void initLocalCache(String watchPath) throws Exception {
        cache = new TreeCache(framework, watchPath);
        cache.start();
    }

    /**
     * 注册连接状态监听器
     */
    private void addConnectionStateListener() {
        framework.getConnectionStateListenable().addListener((listener, newState) -> {
            if (newState == ConnectionState.RECONNECTED) {
                ephemeralNodes.forEach(this::reRegisterEphemeral);
                ephemeralSequentialNodes.forEach(this::reRegisterEphemeralSequential);
            }
            TigerLog.ZK.warn("zookeeper connection state changed new state={}, isConnected={}", newState, newState.isConnected());
        });
    }

    /**
     * 注册临时节点
     * @param key key
     * @param value value
     */
    public void registerEphemeral(String key, String value) {
        // 重新注册不需要保存
        registerEphemeral(key, value, true);
    }

    /**
     * 重新注册临时节点
     * @param key key
     * @param value value
     */
    private void reRegisterEphemeral(String key, String value) {
        // 重新注册不需要保存
        registerEphemeral(key, value, false);
    }

    /**
     * 重新注册临时顺序节点
     * @param key key
     * @param value value
     */
    public void registerEphemeralSequential(String key, String value) {
        registerEphemeralSequential(key, value, true);
    }

    /**
     * 重新注册临时顺序节点
     * @param key key
     * @param value value
     */
    private void reRegisterEphemeralSequential(String key, String value) {
        registerEphemeralSequential(key, value, false);
    }

    /**
     * 注册临时节点
     * @param key key
     * @param value value
     * @param cacheNode 是否保存，
     */
    private void registerEphemeral(String key, String value, boolean cacheNode) {
        try {
            if (isExisted(key)) {
                remove(key);
            }
            framework.create().creatingParentsIfNeeded().withMode(CreateMode.EPHEMERAL).forPath(key, value.getBytes(StandardCharsets.UTF_8));
            if (cacheNode) {
                ephemeralNodes.put(key, value);
            }
        } catch (Exception e) {
            TigerLog.ZK.error("registry ephemeral node occur exception, key={}, value={}", key, value, e);
            throw new ZkException(e);
        }
    }

    /**
     * 注册临时顺序节点
     * @param key key
     * @param value value
     * @param cacheNode 是否保存，
     */
    private void registerEphemeralSequential(String key, String value, boolean cacheNode) {
        try {
            framework.create().creatingParentsIfNeeded().withMode(CreateMode.EPHEMERAL_SEQUENTIAL).forPath(key, value.getBytes(StandardCharsets.UTF_8));
            if (cacheNode) {
                ephemeralSequentialNodes.put(key, value);
            }
        } catch (Exception e) {
            TigerLog.ZK.error("registry ephemeral sequential node occur exception, key={}, value={}", key, value, e);
            throw new ZkException(e);
        }
    }

    /**
     * 注册持久节点
     * @param key key
     * @param value value
     */
    public void registerPersistent(String key, String value) {
        try {
            if (isExisted(key)) {
                update(key, value);
            } else {
                framework.create().creatingParentsIfNeeded().withMode(CreateMode.PERSISTENT).forPath(key, value.getBytes(StandardCharsets.UTF_8));
            }
        } catch (Exception e) {
            TigerLog.ZK.error("registry persistent node occur exception, key={}, value={}", key, value, e);
            throw new ZkException(e);
        }
    }

    /**
     * 删除节点
     * @param key key
     */
    public void remove(String key) {
        try {
            framework.delete().deletingChildrenIfNeeded().forPath(key);
        } catch (Exception e) {
            TigerLog.ZK.error("remove node occur exception, key={}", key, e);
            throw new ZkException(e);
        }
    }

    /**
     * 添加监听
     * @param listener {@link TreeCacheListener}
     */
    public void registerListener(TreeCacheListener listener) {
        cache.getListenable().addListener(listener);
    }

    /**
     * 更新数据
     * @param key key
     * @param value value
     */
    private void update(String key, String value) {
        try {
            framework.inTransaction().check().forPath(key).and().setData().forPath(key, value.getBytes(StandardCharsets.UTF_8)).and().commit();
        } catch (Exception e) {
            TigerLog.ZK.error("update node occur exception, key={}, value={}", key, value, e);
            throw new ZkException(e);
        }
    }

    /**
     * 判断路径是否存在
     * @param key 路径
     * @return true：存在
     */
    private boolean isExisted(String key) {
        try {
            return null != framework.checkExists().forPath(key);
        } catch (Exception e) {
            TigerLog.ZK.error("check node is existed for {} occur exception", key);
        }
        return false;
    }

    /**
     * 获取数据，先从本地找，如果找不到，再从远程找
     * @param key key
     * @return 数据
     */
    public String get(String key) {
        if (null == cache) {
            return null;
        }
        ChildData childData = cache.getCurrentData(key);
        if (childData != null) {
            return childData.getData() == null ? null : new String(childData.getData(), StandardCharsets.UTF_8);
        }
        return getFromRemote(key);
    }

    /**
     * 从远程获取数据
     * @param key key
     * @return 远程数据
     */
    private String getFromRemote(String key) {
        if (isExisted(key)) {
            try {
                return new String(framework.getData().forPath(key), StandardCharsets.UTF_8);
            } catch (Exception e) {
                TigerLog.ZK.error("get value from remote occur exception, key={}", key, e);
            }
        }
        return null;
    }

    /**
     * 获取子节点
     * @param key key
     * @return 子节点
     */
    public List<String> getChildrenKeys(String key) {
        try {
            if (!isExisted(key)) {
                return Collections.emptyList();
            }
            List<String> list = framework.getChildren().forPath(key);
            list.sort(Comparator.reverseOrder());
        } catch (Exception e) {
            TigerLog.ZK.error("get children occur exception, key={}", key, e);
        }
        return Collections.emptyList();
    }
}
