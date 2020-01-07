package org.tiger.tools.exception;

/**
 * {@link CryptoException}
 *
 * @author SongQingWei
 * @since 1.0.0
 * 2020-01-07 14:12 周二
 */
public class CryptoException extends RuntimeException {

    public CryptoException(String message) {
        super(message);
    }

    public CryptoException(String message, Throwable cause) {
        super(message, cause);
    }

    public CryptoException(Throwable cause) {
        super(cause);
    }
}
