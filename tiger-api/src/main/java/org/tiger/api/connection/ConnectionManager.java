package org.tiger.api.connection;

import io.netty.channel.Channel;

/**
 * {@link ConnectionManager}
 *
 * @author SongQingWei
 * @since 1.0.0
 * 2020-01-08 10:42 周三
 */
public interface ConnectionManager {

    /**
     * 初始化连接管理器
     */
    void init();

    /**
     * 销毁连接管理器
     */
    void destroy();

    /**
     * 添加连接信息到管理器中
     * @param connection 连接信息
     */
    void add(Connection connection);

    /**
     * 查询连接信息
     * @param channel {@link Channel}
     * @return 连接信息
     */
    Connection get(Channel channel);

    /**
     * 从管理器中移除并关闭连接
     * @param channel {@link Channel}
     * @return 当前关闭的连接信息
     */
    Connection removeAndClose(Channel channel);

    /**
     * 返回当前连接数
     * @return 连接数
     */
    int getConnectionNumber();
}
