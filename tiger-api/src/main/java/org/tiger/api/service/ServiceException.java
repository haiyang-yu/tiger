package org.tiger.api.service;

/**
 * {@link ServiceException}
 *
 * @author SongQingWei
 * @since 1.0.0
 * 2020-01-14 11:37 周二
 */
public class ServiceException extends RuntimeException {

    public ServiceException() {
    }

    public ServiceException(String message) {
        super(message);
    }

    public ServiceException(String message, Throwable cause) {
        super(message, cause);
    }

    public ServiceException(Throwable cause) {
        super(cause);
    }
}
