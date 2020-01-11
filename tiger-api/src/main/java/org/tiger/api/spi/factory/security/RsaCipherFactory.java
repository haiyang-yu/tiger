package org.tiger.api.spi.factory.security;

import org.tiger.api.security.Cipher;
import org.tiger.api.spi.SpiLoader;
import org.tiger.api.spi.factory.Factory;

/**
 * {@link RsaCipherFactory}
 *
 * @author SongQingWei
 * @since 1.0.0
 * 2020-01-11 17:55 周六
 */
public interface RsaCipherFactory extends Factory<Cipher> {

    /**
     * 加载RSA加解密工具
     * @return {@link Cipher}
     */
    static Cipher create() {
        return SpiLoader.load(RsaCipherFactory.class).get();
    }
}
