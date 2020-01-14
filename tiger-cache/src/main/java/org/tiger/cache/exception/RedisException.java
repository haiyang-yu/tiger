package org.tiger.cache.exception;

import org.tiger.api.service.ServiceException;

/**
 * {@link RedisException}
 *
 * @author SongQingWei
 * @since 1.0.0
 * 2020-01-14 12:24 周二
 */
public class RedisException extends ServiceException {

    public RedisException() {
    }

    public RedisException(String message) {
        super(message);
    }

    public RedisException(String message, Throwable cause) {
        super(message, cause);
    }

    public RedisException(Throwable cause) {
        super(cause);
    }
}
