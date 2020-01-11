package org.tiger.common.security;

import org.tiger.api.security.Cipher;
import org.tiger.tools.crypto.AesUtil;

/**
 * {@link AesCipher}
 *
 * @author SongQingWei
 * @since 1.0.0
 * 2020-01-07 17:31 周二
 */
public class AesCipher implements Cipher {

    private final byte[] key;
    private final byte[] iv;

    public AesCipher(byte[] key, byte[] iv) {
        this.key = key;
        this.iv = iv;
    }

    @Override
    public byte[] decrypt(byte[] data) {
        return AesUtil.decrypt(data, key, iv);
    }

    @Override
    public byte[] encrypt(byte[] data) {
        return AesUtil.encrypt(data, key, iv);
    }
}
