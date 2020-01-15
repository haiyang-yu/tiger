package org.tiger.api.zk.registry;

import org.tiger.api.service.Server;
import org.tiger.api.zk.node.ServiceNode;

/**
 * {@link ServiceRegistry}
 *
 * @author SongQingWei
 * @since 1.0.0
 * 2020-01-14 14:27 周二
 */
public interface ServiceRegistry extends Server {

    /**
     * 注册节点
     * @param node {@link ServiceNode}
     */
    void register(ServiceNode node);

    /**
     * 注销节点
     * @param node {@link ServiceNode}
     */
    void deregister(ServiceNode node);
}
