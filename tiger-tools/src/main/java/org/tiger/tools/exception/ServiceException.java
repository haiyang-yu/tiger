package org.tiger.tools.exception;

/**
 * {@link ServiceException}
 *
 * @author SongQingWei
 * @since 1.0.0
 * 2020-01-08 13:32 周三
 */
public class ServiceException extends RuntimeException {

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
