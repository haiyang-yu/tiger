package org.tiger.tools.crypto;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

/**
 * {@link Base64Util}
 *
 * @author SongQingWei
 * @since 1.0.0
 * 2020-01-07 13:55 周二
 */
public class Base64Util {

    /**
     * BASE64字符串解码为二进制数据
     * @param base64 BASE64字符串(长度必须为4的倍数)
     * @return 二进制数据
     */
    public static  byte[] decode(String base64) {
        return Base64.getDecoder().decode(base64.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * 二进制数据编码为BASE64字符串
     * @param bytes 二进制数据
     * @return BASE64字符串
     */
    public static String encode(byte[] bytes) {
        return new String(Base64.getEncoder().encode(bytes), StandardCharsets.UTF_8);
    }
}
