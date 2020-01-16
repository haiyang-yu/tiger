package org.tiger.zk.service;

import org.tiger.api.listener.Listener;
import org.tiger.api.service.BaseService;
import org.tiger.api.zk.discovery.ServiceDiscovery;
import org.tiger.api.zk.listener.ServiceListener;
import org.tiger.api.zk.node.CommonServiceNode;
import org.tiger.api.zk.node.ServiceNode;
import org.tiger.api.zk.registry.ServiceRegistry;
import org.tiger.common.utils.JsonUtil;
import org.tiger.zk.connection.ZkClient;
import org.tiger.zk.listener.ZkCacheListener;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static org.apache.curator.utils.ZKPaths.PATH_SEPARATOR;

/**
 * {@link ZkServiceRegistryAndDiscovery}
 *
 * @author SongQingWei
 * @since 1.0.0
 * 2020-01-14 15:29 周二
 */
public class ZkServiceRegistryAndDiscovery extends BaseService implements ServiceRegistry, ServiceDiscovery {

    public static final ZkServiceRegistryAndDiscovery INSTANCE = new ZkServiceRegistryAndDiscovery();

    private final ZkClient client;

    private ZkServiceRegistryAndDiscovery() {
        this.client = ZkClient.INSTANCE;
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
        client.start(listener);
    }

    @Override
    protected void doStop(Listener listener) {
        client.stop(listener);
    }

    @Override
    public void register(ServiceNode node) {
        if (node.isPersistent()) {
            client.registerPersistent(node.nodePath(), JsonUtil.toJson(node));
        } else {
            client.registerEphemeral(node.nodePath(), JsonUtil.toJson(node));
        }
    }

    @Override
    public void deregister(ServiceNode node) {
        if (client.isRunning()) {
            client.remove(node.nodePath());
        }
    }

    @Override
    public List<ServiceNode> lookup(String path) {
        List<String> childrenList = client.getChildrenKeys(path);
        if (childrenList == null || childrenList.isEmpty()) {
            return Collections.emptyList();
        }
        return childrenList.stream()
                .map(key -> path + PATH_SEPARATOR + key)
                .map(client::get)
                .filter(Objects::nonNull)
                .map(childData -> JsonUtil.parseObject(childData, CommonServiceNode.class))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    @Override
    public void subscribe(String path, ServiceListener listener) {
        client.registerListener(new ZkCacheListener(path, listener));
    }

    @Override
    public void unsubscribe(String path, ServiceListener listener) {

    }
}
