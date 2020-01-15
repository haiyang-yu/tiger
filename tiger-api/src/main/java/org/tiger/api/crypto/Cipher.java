package org.tiger.api.crypto;

/**
 * {@link Cipher}
 *
 * @author SongQingWei
 * @since 1.0.0
 * 2020-01-14 16:08 周二
 */
public interface Cipher {

    /**
     * 解码
     * @param data 待解码数据
     * @return 解码后的数据
     */
    byte[] decrypt(byte[] data);

    /**
     * 编码
     * @param data 待编码的数据
     * @return 编码后的数据
     */
    byte[] encrypt(byte[] data);
}
