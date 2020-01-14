package org.tiger.zk.exception;

import org.tiger.api.service.ServiceException;

/**
 * {@link ZkException}
 *
 * @author SongQingWei
 * @since 1.0.0
 * 2020-01-14 15:23 周二
 */
public class ZkException extends ServiceException {

    public ZkException(String message) {
        super(message);
    }

    public ZkException(String message, Throwable cause) {
        super(message, cause);
    }

    public ZkException(Throwable cause) {
        super(cause);
    }
}
