package org.tiger.common.security.factory;

import org.tiger.api.crypto.Cipher;
import org.tiger.common.security.cipher.RsaCipher;
import org.tiger.spi.factory.crypto.RsaCipherFactory;

/**
 * {@link DefaultRsaCipherFactory}
 *
 * @author SongQingWei
 * @since 1.0.0
 * 2020-01-14 16:25 周二
 */
public class DefaultRsaCipherFactory implements RsaCipherFactory {

    private static final RsaCipher RSA_CIPHER = RsaCipher.create();

    @Override
    public Cipher get() {
        return RSA_CIPHER;
    }
}
