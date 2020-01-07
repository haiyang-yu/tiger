package org.tiger.tools.crypto;

import lombok.extern.slf4j.Slf4j;
import org.tiger.tools.exception.CryptoException;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.SecureRandom;
import java.util.Arrays;

/**
 * {@link AesUtil}
 *
 * @author SongQingWei
 * @since 1.0.0
 * 2020-01-07 14:10 周二
 */
@Slf4j
public class AesUtil {

    private static final String KEY_ALGORITHM = "AES";
    private static final String KEY_ALGORITHM_PADDING = "AES/CBC/PKCS5Padding";

    public static SecretKey getSecretKey(byte[] bytes) throws Exception {
        SecureRandom random = new SecureRandom(bytes);
        KeyGenerator generator = KeyGenerator.getInstance(KEY_ALGORITHM);
        generator.init(random);
        return generator.generateKey();
    }

    public static byte[] encrypt(byte[] bytes, byte[] encryptKey, byte[] iv) {
        SecretKeySpec keySpec = new SecretKeySpec(encryptKey, KEY_ALGORITHM);
        IvParameterSpec zeroIv = new IvParameterSpec(iv);
        return encrypt(bytes, keySpec, zeroIv);
    }

    public static byte[] encrypt(byte[] bytes, SecretKeySpec keySpec, IvParameterSpec zeroIv) {
        try {
            Cipher cipher = Cipher.getInstance(KEY_ALGORITHM_PADDING);
            cipher.init(Cipher.ENCRYPT_MODE, keySpec, zeroIv);
            return cipher.doFinal(bytes);
        } catch (Exception e) {
            log.error("AES encrypt ex, key={}, iv={}",
                    Arrays.toString(keySpec.getEncoded()),
                    Arrays.toString(zeroIv.getIV()));
            throw new CryptoException("AES encrypt ex", e);
        }
    }

    public static byte[] decrypt(byte[] bytes, byte[] decryptKey, byte[] iv) {
        SecretKeySpec keySpec = new SecretKeySpec(decryptKey, KEY_ALGORITHM);
        IvParameterSpec zeroIv = new IvParameterSpec(iv);
        return decrypt(bytes, keySpec, zeroIv);
    }

    public static byte[] decrypt(byte[] bytes, SecretKeySpec keySpec, IvParameterSpec zeroIv) {
        try {
            Cipher cipher = Cipher.getInstance(KEY_ALGORITHM_PADDING);
            cipher.init(Cipher.DECRYPT_MODE, keySpec, zeroIv);
            return cipher.doFinal(bytes);
        } catch (Exception e) {
            log.error("AES decrypt ex, key={}, iv={}",
                    Arrays.toString(keySpec.getEncoded()),
                    Arrays.toString(zeroIv.getIV()));
            throw new CryptoException("AES decrypt ex", e);
        }
    }
}
