package org.tiger.api.zk.listener;

import org.tiger.api.zk.node.ServiceNode;

/**
 * {@link ServiceListener}
 *
 * @author SongQingWei
 * @since 1.0.0
 * 2020-01-14 14:45 周二
 */
public interface ServiceListener {

    /**
     * 添加节点信息时触发
     * @param path 路径
     * @param node {@link ServiceNode}
     */
    void onServiceAdded(String path, ServiceNode node);

    /**
     * 更新节点信息时触发
     * @param path 路径
     * @param node {@link ServiceNode}
     */
    void onServiceUpdated(String path, ServiceNode node);

    /**
     * 删除节点信息时触发
     * @param path 路径
     * @param node {@link ServiceNode}
     */
    void onServiceRemoved(String path, ServiceNode node);
}
