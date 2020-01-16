package org.tiger.api.connection;

/**
 * {@link ConnectionHolder}
 *
 * @author SongQingWei
 * @since 1.0.0
 * 2020-01-16 13:32 周四
 */
public interface ConnectionHolder {

    /**
     * 从连接持有器中获取连接信息
     * @return 连接信息
     */
    Connection get();

    /**
     * 关闭当前持有器所持有的连接
     */
    void close();
}
