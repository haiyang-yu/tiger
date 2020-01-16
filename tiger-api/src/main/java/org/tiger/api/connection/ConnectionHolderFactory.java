package org.tiger.api.connection;

/**
 * {@link ConnectionHolderFactory}
 *
 * @author SongQingWei
 * @since 1.0.0
 * 2020-01-16 13:31 周四
 */
@FunctionalInterface
public interface ConnectionHolderFactory {

    /**
     * 创建连接信息的持有者
     * @param connection 链接信息
     * @return 连接持有者
     */
    ConnectionHolder create(Connection connection);
}
