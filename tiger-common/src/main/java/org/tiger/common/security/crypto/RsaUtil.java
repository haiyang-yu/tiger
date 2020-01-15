package org.tiger.common.security.crypto;

import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tiger.common.exception.CryptoException;
import org.tiger.common.utils.IoUtil;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import java.io.ByteArrayOutputStream;
import java.math.BigInteger;
import java.security.*;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.RSAPrivateKeySpec;
import java.security.spec.RSAPublicKeySpec;
import java.security.spec.X509EncodedKeySpec;

/**
 * {@link RsaUtil}
 *
 * @author SongQingWei
 * @since 1.0.0
 * 2020-01-14 11:13 周二
 */
public class RsaUtil {

    private static final Logger LOGGER = LoggerFactory.getLogger(RsaUtil.class);

    /**
     * 密钥位数
     */
    public static final int RAS_KEY_SIZE = 1024;

    /**
     * 加密算法RSA
     */
    private static final String KEY_ALGORITHM = "RSA";

    /**
     * 填充方式
     */
    private static final String KEY_ALGORITHM_PADDING = "RSA/ECB/PKCS1Padding";

    /**
     * 签名算法
     */
    private static final String SIGNATURE_ALGORITHM = "MD5withRSA";

    /**
     * RSA最大解密密文大小
     */
    private static final int MAX_DECRYPT_BLOCK = 128;

    /**
     * RSA最大加密明文大小
     */
    private static final int MAX_ENCRYPT_BLOCK = MAX_DECRYPT_BLOCK - 11;

    /**
     * 生成公钥和私钥
     * @param rsaKeySize 密钥长度
     * @return 密钥对
     */
    public static Pair<RSAPublicKey, RSAPrivateKey> genKeyPair(int rsaKeySize) {
        try {
            KeyPairGenerator generator = KeyPairGenerator.getInstance(KEY_ALGORITHM);
            generator.initialize(rsaKeySize);
            KeyPair keyPair = generator.generateKeyPair();
            RSAPublicKey publicKey = (RSAPublicKey) keyPair.getPublic();
            RSAPrivateKey privateKey = (RSAPrivateKey) keyPair.getPrivate();
            return Pair.of(publicKey, privateKey);
        } catch (NoSuchAlgorithmException e) {
            LOGGER.error("Generator key pair ex", e);
        }
        return null;
    }

    /**
     * 编码密钥，便于存储
     * @param key 密钥
     * @return base64后的字符串
     */
    public static String encodeBase64(Key key) {
        return Base64Util.encode(key.getEncoded());
    }

    /**
     * 从字符串解码私钥
     * @param key 密钥
     * @return 私钥
     * @throws Exception e
     */
    public static PrivateKey decodePrivateKey(String key) throws Exception {
        byte[] bytes = Base64Util.decode(key);
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(bytes);
        KeyFactory factory = KeyFactory.getInstance(KEY_ALGORITHM);
        return factory.generatePrivate(keySpec);
    }

    /**
     * 从字符串解码公钥
     * @param key 密钥
     * @return 公钥
     * @throws Exception e
     */
    public static PublicKey decodePublicKey(String key) throws Exception {
        byte[] bytes = Base64Util.decode(key);
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(bytes);
        KeyFactory factory = KeyFactory.getInstance(KEY_ALGORITHM);
        return factory.generatePublic(keySpec);
    }

    /**
     * 用私钥对信息生成数字签名
     * @param data 已加密数据
     * @param privateKey 私钥(BASE64编码)
     * @return 数字签名
     * @throws Exception e
     */
    public static String sign(byte[] data, String privateKey) throws Exception {
        Signature signature = Signature.getInstance(SIGNATURE_ALGORITHM);
        signature.initSign(decodePrivateKey(privateKey));
        signature.update(data);
        return Base64Util.encode(signature.sign());
    }

    /**
     * 校验数字签名
     * @param data 已加密数据
     * @param publicKey 公钥(BASE64编码)
     * @param sign 数字签名
     * @return 是否通过校验
     * @throws Exception e
     */
    public static boolean verify(byte[] data, String publicKey, String sign) throws Exception {
        Signature signature = Signature.getInstance(SIGNATURE_ALGORITHM);
        signature.initVerify(decodePublicKey(publicKey));
        signature.update(data);
        return signature.verify(Base64Util.decode(sign));
    }

    /**
     * 使用模和指数生成RSA公钥
     * @param modulus 模
     * @param exponent 指数
     * @return RSA公钥
     */
    public static RSAPublicKey getPublicKey(String modulus, String exponent) {
        try {
            KeyFactory factory = KeyFactory.getInstance(KEY_ALGORITHM);
            RSAPublicKeySpec keySpec = new RSAPublicKeySpec(new BigInteger(modulus), new BigInteger(exponent));
            return (RSAPublicKey) factory.generatePublic(keySpec);
        } catch (Exception e) {
            LOGGER.error("getPublicKey ex, modulus={}, exponent={}", modulus, exponent, e);
            throw new CryptoException("Get PublicKey ex", e);
        }
    }

    /**
     * 使用模和指数生成RSA私钥
     * 注意：【此代码用了默认补位方式，为RSA/None/PKCS1Padding，不同JDK默认的补位方式可能不同，如Android默认是RSA/None/NoPadding】
     * @param modulus 模
     * @param exponent 指数
     * @return RSA公钥
     */
    public static RSAPrivateKey getPrivateKey(String modulus, String exponent) {
        try {
            KeyFactory factory = KeyFactory.getInstance(KEY_ALGORITHM);
            RSAPrivateKeySpec keySpec = new RSAPrivateKeySpec(new BigInteger(modulus), new BigInteger(exponent));
            return (RSAPrivateKey) factory.generatePrivate(keySpec);
        } catch (Exception e) {
            LOGGER.error("getPrivateKey ex, modulus={}, exponent={}", modulus, exponent, e);
            throw new CryptoException("Get PrivateKey ex", e);
        }
    }

    /**
     * 公钥加密
     * @param data 待加密数据
     * @param publicKey 公钥
     * @return 加密后的值
     */
    public static byte[] encryptByPublicKey(byte[] data, RSAPublicKey publicKey) {
        try {
            Cipher cipher = Cipher.getInstance(KEY_ALGORITHM_PADDING);
            cipher.init(Cipher.ENCRYPT_MODE, publicKey);
            // 模长
            int keyLen = publicKey.getModulus().bitLength() / 8;
            // 如果明文长度大于(模长-11)则要分组加密
            return doFinal(cipher, data, keyLen - 11);
        } catch (Exception e) {
            LOGGER.error("Encrypt by publicKey ex", e);
            throw new CryptoException("PublicKey encrypt ex", e);
        }
    }

    /**
     * 私钥解密
     * @param data 待解密数据
     * @param privateKey 私钥
     * @return 解密后的值
     */
    public static byte[] decryptByPrivateKey(byte[] data, RSAPrivateKey privateKey) {
        try {
            Cipher cipher = Cipher.getInstance(KEY_ALGORITHM_PADDING);
            cipher.init(Cipher.DECRYPT_MODE, privateKey);
            // 模长
            int keyLen = privateKey.getModulus().bitLength() / 8;
            // 如果密文长度大于模长则要分组解密
            return doFinal(cipher, data, keyLen);
        } catch (Exception e) {
            LOGGER.error("Decrypt by privateKey ex", e);
            throw new CryptoException("PrivateKey decrypt ex", e);
        }
    }

    /**
     * 公钥解密
     * @param data 已加密数据
     * @param publicKey 公钥(BASE64编码)
     * @return 解密后的值
     * @throws Exception e
     */
    public static byte[] decryptByPublicKey(byte[] data, String publicKey) throws Exception {
        PublicKey key = decodePublicKey(publicKey);
        Cipher cipher = Cipher.getInstance(KEY_ALGORITHM_PADDING);
        cipher.init(Cipher.DECRYPT_MODE, key);
        return doFinal(cipher, data, MAX_DECRYPT_BLOCK);
    }

    /**
     * 私钥解密
     * @param data 已加密数据
     * @param privateKey 私钥(BASE64编码)
     * @return 解密后的值
     * @throws Exception e
     */
    public static byte[] decryptByPrivateKey(byte[] data, String privateKey) throws Exception {
        PrivateKey key = decodePrivateKey(privateKey);
        Cipher cipher = Cipher.getInstance(KEY_ALGORITHM_PADDING);
        cipher.init(Cipher.DECRYPT_MODE, key);
        return doFinal(cipher, data, MAX_DECRYPT_BLOCK);
    }

    /**
     * 公钥加密
     * @param data 待加密数据
     * @param publicKey 公钥(BASE64编码)
     * @return 加密后的值
     * @throws Exception e
     */
    public static byte[] encryptByPublicKey(byte[] data, String publicKey) throws Exception {
        PublicKey key = decodePublicKey(publicKey);
        Cipher cipher = Cipher.getInstance(KEY_ALGORITHM_PADDING);
        cipher.init(Cipher.ENCRYPT_MODE, key);
        return doFinal(cipher, data, MAX_ENCRYPT_BLOCK);
    }

    /**
     * 私钥加密
     * @param data 待加密数据
     * @param privateKey 私钥(BASE64编码)
     * @return 加密后的值
     * @throws Exception e
     */
    public static byte[] encryptByPrivateKey(byte[] data, String privateKey) throws Exception {
        PrivateKey key = decodePrivateKey(privateKey);
        Cipher cipher = Cipher.getInstance(KEY_ALGORITHM_PADDING);
        cipher.init(Cipher.ENCRYPT_MODE, key);
        return doFinal(cipher, data, MAX_ENCRYPT_BLOCK);
    }

    /**
     * 注意：【RSA加密明文最大长度117字节，解密要求密文最大长度为128字节，所以在加密和解密的过程中需要分块进行。】
     * @param cipher 密钥
     * @param data 待处理的数据
     * @param keyLen 模长
     * @return 处理后的值
     * @throws BadPaddingException e
     * @throws IllegalBlockSizeException e
     */
    private static byte[] doFinal(Cipher cipher, byte[] data, int keyLen) throws BadPaddingException, IllegalBlockSizeException {
        int inputLen = data.length, offset = 0;
        byte[] bytes;
        ByteArrayOutputStream bos = new ByteArrayOutputStream(getTempArrayLength(inputLen));
        try {
            while (inputLen > 0) {
                bytes = cipher.doFinal(data, offset, Math.min(keyLen, inputLen));
                bos.write(bytes, 0, bytes.length);
                offset += keyLen;
                inputLen -= keyLen;
            }
            return bos.toByteArray();
        } finally {
            IoUtil.close(bos);
        }
    }

    private static int getTempArrayLength(int len) {
        int max = MAX_DECRYPT_BLOCK;
        while (max < len) {
            max <<= 1;
        }
        return max;
    }
}
