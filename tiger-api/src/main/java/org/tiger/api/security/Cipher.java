package org.tiger.api.security;

/**
 * {@link Cipher}
 *
 * @author SongQingWei
 * @since 1.0.0
 * 2020-01-07 16:46 周二
 */
public interface Cipher {

    /**
     * 解密
     * @param data 待解密的数据
     * @return 解密后的数据
     */
    byte[] decrypt(byte[] data);

    /**
     * 加密
     * @param data 待加密的数据
     * @return 加密后的数据
     */
    byte[] encrypt(byte[] data);
}
