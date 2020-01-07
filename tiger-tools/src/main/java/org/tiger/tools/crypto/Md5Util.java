package org.tiger.tools.crypto;

import org.tiger.tools.utils.IoUtil;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

/**
 * {@link Md5Util}
 *
 * @author SongQingWei
 * @since 1.0.0
 * 2020-01-07 14:05 周二
 */
public class Md5Util {

    private static final String MD5 = "MD5";
    private static final String HMAC_SHA1 = "HmacSHA1";
    private static final String SHA1 = "SHA-1";

    /**
     * 加密文件
     * @param file 待加密文件
     * @return 加密后的值
     */
    public static String encrypt(File file) {
        InputStream is = null;
        try {
            MessageDigest digest = MessageDigest.getInstance(MD5);
            is = new FileInputStream(file);
            byte[] bytes = new byte[1024];
            int readLen;
            while ((readLen = is.read(bytes)) != -1) {
                digest.update(bytes, 0, readLen);
            }
            return toHex(digest.digest());
        } catch (Exception e) {
            return null;
        } finally {
            IoUtil.close(is);
        }
    }

    public static String encrypt(String data) {
        return encrypt(data.getBytes(StandardCharsets.UTF_8));
    }

    public static String encrypt(byte[] data) {
        try {
            MessageDigest digest = MessageDigest.getInstance(MD5);
            digest.update(data);
            return toHex(digest.digest());
        } catch (Exception e) {
            return null;
        }
    }

    public static String hmacSha1(String data, String encryptKey) {
        SecretKeySpec keySpec = new SecretKeySpec(encryptKey.getBytes(StandardCharsets.UTF_8), HMAC_SHA1);
        try {
            Mac mac = Mac.getInstance(HMAC_SHA1);
            mac.init(keySpec);
            mac.update(data.getBytes(StandardCharsets.UTF_8));
            return toHex(mac.doFinal());
        } catch (Exception e) {
            return null;
        }
    }

    public static String sha1(String data) {
        try {
            MessageDigest digest = MessageDigest.getInstance(SHA1);
            digest.update(data.getBytes(StandardCharsets.UTF_8));
            return toHex(digest.digest());
        } catch (Exception e) {
            return null;
        }
    }

    private static String toHex(byte[] bytes) {
        StringBuilder builder = new StringBuilder(bytes.length * 2);
        for (byte aByte : bytes) {
            builder.append(Character.forDigit((aByte & 240) >> 4, 16));
            builder.append(Character.forDigit(aByte & 15, 16));
        }
        return builder.toString();
    }
}
