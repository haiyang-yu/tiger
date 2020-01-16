package org.tiger.common.security.cipher;

import org.tiger.api.crypto.Cipher;
import org.tiger.common.security.crypto.AesUtil;
import org.tiger.common.security.crypto.Base64Util;

import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import static org.tiger.common.security.crypto.AesUtil.KEY_ALGORITHM;

/**
 * {@link AesCipher}
 *
 * @author SongQingWei
 * @since 1.0.0
 * 2020-01-14 16:09 周二
 */
public class AesCipher implements Cipher {

    private final byte[] key;
    private final byte[] iv;
    private final IvParameterSpec zeroIv;
    private final SecretKeySpec keySpec;

    public AesCipher(byte[] key, byte[] iv) {
        this.key = key;
        this.iv = iv;
        this.zeroIv = new IvParameterSpec(iv);
        this.keySpec = new SecretKeySpec(key, KEY_ALGORITHM);
    }

    @Override
    public byte[] decrypt(byte[] data) {
        return AesUtil.decrypt(data, keySpec, zeroIv);
    }

    @Override
    public byte[] encrypt(byte[] data) {
        return AesUtil.encrypt(data, keySpec, zeroIv);
    }

    @Override
    public String toString() {
        return Base64Util.encode(key) + "," + Base64Util.encode(iv);
    }
}
