package org.tiger.common.config.data;

import com.google.common.base.Splitter;
import com.google.common.collect.Lists;

import java.util.ArrayList;

/**
 * {@link RedisNode}
 *
 * @author SongQingWei
 * @since 1.0.0
 * 2020-01-14 10:35 周二
 */
public class RedisNode {

    private String host;
    private int port;

    public RedisNode(String host, int port) {
        this.host = host;
        this.port = port;
    }

    public static RedisNode from(String hostAndPort) {
        Iterable<String> iterable = Splitter.on(":").trimResults().omitEmptyStrings().split(hostAndPort);
        ArrayList<String> list = Lists.newArrayList(iterable);
        if (list.size() > 1) {
            return new RedisNode(list.get(0), Integer.parseInt(list.get(1)));
        }
        throw new IllegalStateException("redis server host or port can't be empty, please check your conf.");
    }

    public String getHost() {
        return host;
    }

    public int getPort() {
        return port;
    }
}
