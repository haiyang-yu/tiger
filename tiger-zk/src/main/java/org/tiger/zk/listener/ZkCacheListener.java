package org.tiger.zk.listener;

import org.apache.commons.lang3.StringUtils;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.cache.ChildData;
import org.apache.curator.framework.recipes.cache.TreeCacheEvent;
import org.apache.curator.framework.recipes.cache.TreeCacheListener;
import org.tiger.api.zk.listener.ServiceListener;
import org.tiger.api.zk.node.CommonServiceNode;
import org.tiger.common.log.TigerLog;
import org.tiger.common.utils.JsonUtil;

/**
 * {@link ZkCacheListener}
 *
 * @author SongQingWei
 * @since 1.0.0
 * 2020-01-14 15:32 周二
 */
public class ZkCacheListener implements TreeCacheListener {

    private final String watchPath;
    private final ServiceListener listener;

    public ZkCacheListener(String watchPath, ServiceListener listener) {
        this.watchPath = watchPath;
        this.listener = listener;
    }

    @Override
    public void childEvent(CuratorFramework curatorFramework, TreeCacheEvent treeCacheEvent) throws Exception {
        ChildData data = treeCacheEvent.getData();
        if (data == null) {
            return;
        }
        String path = data.getPath();
        if (StringUtils.isBlank(path)) {
            return;
        }
        if (path.startsWith(watchPath)) {
            switch (treeCacheEvent.getType()) {
                case NODE_ADDED:
                    listener.onServiceAdded(path, JsonUtil.parseObject(data.getData(), CommonServiceNode.class));
                    break;
                case NODE_UPDATED:
                    listener.onServiceUpdated(path, JsonUtil.parseObject(data.getData(), CommonServiceNode.class));
                    break;
                case NODE_REMOVED:
                    listener.onServiceRemoved(path, JsonUtil.parseObject(data.getData(), CommonServiceNode.class));
                    break;
                default:
                    TigerLog.ZK.warn("zookeeper service node state change, type={}", treeCacheEvent.getType());
            }
        }
    }
}
