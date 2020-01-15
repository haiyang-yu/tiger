package org.tiger.spi.factory.crypto;

import org.tiger.api.crypto.Cipher;
import org.tiger.spi.ExtensionLoader;
import org.tiger.spi.factory.Factory;

/**
 * {@link RsaCipherFactory}
 *
 * @author SongQingWei
 * @since 1.0.0
 * 2020-01-14 16:23 周二
 */
public interface RsaCipherFactory extends Factory<Cipher> {

    /**
     * 加载RSA组件
     * @return {@link Cipher}
     */
    static Cipher create() {
        return ExtensionLoader.loadExtensionClasses(RsaCipherFactory.class).get();
    }
}
