package org.tiger.api.zk.node;

/**
 * {@link ServiceNode}
 *
 * @author SongQingWei
 * @since 1.0.0
 * 2020-01-14 14:27 周二
 */
public interface ServiceNode {

    /**
     * 节点服务名称
     * @return 服务名称
     */
    String serviceName();

    /**
     * 节点ID
     * @return 节点ID
     */
    String nodeId();

    /**
     * 服务IP
     * @return 服务IP
     */
    String getHost();

    /**
     * 服务端口
     * @return 服务端口
     */
    int getPort();

    /**
     * 额外参数
     * @param name key
     * @param <T> T
     * @return value
     */
    default <T> T getAttr(String name) {
        return null;
    }

    /**
     * 是否为持久节点
     * @return true：持久节点
     */
    default boolean isPersistent() {
        return false;
    }

    /**
     * host:port
     * @return host:port
     */
    default String hostAndPort() {
        return getHost() + ":" + getPort();
    }

    /**
     * serviceName/nodeId
     * @return serviceName/nodeId
     */
    default String nodePath() {
        return serviceName() + '/' + nodeId();
    }
}
