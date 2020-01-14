package org.tiger.common.exception;

/**
 * {@link CryptoException}
 *
 * @author SongQingWei
 * @since 1.0.0
 * 2020-01-14 11:10 周二
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
