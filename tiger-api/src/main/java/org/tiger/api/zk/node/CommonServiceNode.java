package org.tiger.api.zk.node;

import org.apache.commons.lang3.StringUtils;

import java.util.Map;
import java.util.Objects;
import java.util.UUID;

/**
 * {@link CommonServiceNode}
 *
 * @author SongQingWei
 * @since 1.0.0
 * 2020-01-14 14:36 周二
 */
public class CommonServiceNode implements ServiceNode {

    private String host;

    private int port;

    private Map<String, Object> attrs;

    private transient String name;

    private transient String nodeId;

    private transient boolean persistent;

    @Override
    public String serviceName() {
        return name;
    }

    @Override
    public String nodeId() {
        if (StringUtils.isBlank(nodeId)) {
            nodeId = UUID.randomUUID().toString();
        }
        return nodeId;
    }

    @Override
    public String getHost() {
        return host;
    }

    @Override
    public int getPort() {
        return port;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T getAttr(String name) {
        if (Objects.isNull(attrs) || attrs.isEmpty()) {
            return null;
        }
        return (T) attrs.get(name);
    }

    @Override
    public boolean isPersistent() {
        return persistent;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public void setAttrs(Map<String, Object> attrs) {
        this.attrs = attrs;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPersistent(boolean persistent) {
        this.persistent = persistent;
    }

    @Override
    public String toString() {
        return "CommonServiceNode{" +
                "host='" + host + '\'' +
                ", port=" + port +
                ", attrs=" + attrs +
                ", name='" + name + '\'' +
                ", nodeId='" + nodeId + '\'' +
                ", persistent=" + persistent +
                '}';
    }
}
