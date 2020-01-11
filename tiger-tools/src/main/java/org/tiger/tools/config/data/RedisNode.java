package org.tiger.tools.config.data;

import com.typesafe.config.ConfigException;
import lombok.Data;

/**
 * {@link RedisNode}
 *
 * @author SongQingWei
 * @since 1.0.0
 * 2020-01-11 14:27 周六
 */
@Data
public class RedisNode {

    private String host;
    private Integer port;

    public RedisNode(String host, Integer port) {
        this.host = host;
        this.port = port;
    }

    public static RedisNode from(String hostAndPort) {
        String[] array = hostAndPort.split(":");
        if (array.length != (1 + 1)) {
            throw new ConfigException.BadBean("Redis have to host and port.");
        }
        return new RedisNode(array[0], Integer.parseInt(array[1]));
    }
}
