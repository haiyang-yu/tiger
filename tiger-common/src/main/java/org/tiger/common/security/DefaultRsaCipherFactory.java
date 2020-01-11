package org.tiger.common.security;

import org.tiger.api.security.Cipher;
import org.tiger.api.spi.annotation.Spi;
import org.tiger.api.spi.factory.security.RsaCipherFactory;

/**
 * {@link DefaultRsaCipherFactory}
 *
 * @author SongQingWei
 * @since 1.0.0
 * 2020-01-11 17:57 周六
 */
@Spi
public class DefaultRsaCipherFactory implements RsaCipherFactory {

    private static final RsaCipher RSA_CIPHER = RsaCipher.create();

    @Override
    public Cipher get() {
        return RSA_CIPHER;
    }
}
