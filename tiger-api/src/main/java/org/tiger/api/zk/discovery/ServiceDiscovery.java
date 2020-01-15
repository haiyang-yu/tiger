package org.tiger.api.zk.discovery;

import org.tiger.api.service.Server;
import org.tiger.api.zk.listener.ServiceListener;
import org.tiger.api.zk.node.ServiceNode;

import java.util.List;

/**
 * {@link ServiceDiscovery}
 *
 * @author SongQingWei
 * @since 1.0.0
 * 2020-01-14 14:44 周二
 */
public interface ServiceDiscovery extends Server {

    /**
     * 获取指定路径下的节点信息列表
     * @param path 路径
     * @return 节点信息列表
     */
    List<ServiceNode> lookup(String path);

    /**
     * 订阅指定路径
     * @param path 路径
     * @param listener 节点监听
     */
    void subscribe(String path, ServiceListener listener);

    /**
     * 取消订阅指定路径
     * @param path 路径
     * @param listener 节点监听
     */
    void unsubscribe(String path, ServiceListener listener);
}
