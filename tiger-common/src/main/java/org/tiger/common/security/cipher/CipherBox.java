package org.tiger.common.security.cipher;

import org.tiger.common.security.crypto.RsaUtil;
import org.tiger.common.exception.CryptoException;

import java.security.SecureRandom;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;

import static org.tiger.common.config.TigerConfig.Tiger.Security.AES_KEY_LENGTH;

/**
 * {@link CipherBox}
 *
 * @author SongQingWei
 * @since 1.0.0
 * 2020-01-14 16:15 周二
 */
public class CipherBox {

    public static final CipherBox INSTANCE = new CipherBox();

    private SecureRandom random = new SecureRandom();
    private RSAPrivateKey privateKey;
    private RSAPublicKey publicKey;

    public RSAPrivateKey getPrivateKey(String privateKey) {
        if (this.privateKey == null) {
            try {
                this.privateKey = (RSAPrivateKey) RsaUtil.decodePrivateKey(privateKey);
            } catch (Exception e) {
                throw new CryptoException("load private key occur exception", e);
            }
        }
        return this.privateKey;
    }

    public RSAPublicKey getPublicKey(String publicKey) {
        if (this.publicKey == null) {
            try {
                this.publicKey = (RSAPublicKey) RsaUtil.decodePublicKey(publicKey);
            } catch (Exception e) {
                throw new CryptoException("load public key occur exception", e);
            }
        }
        return this.publicKey;
    }

    public byte[] randomAesKey() {
        byte[] bytes = new byte[AES_KEY_LENGTH];
        random.nextBytes(bytes);
        return bytes;
    }

    public byte[] randomAesIv() {
        byte[] bytes = new byte[AES_KEY_LENGTH];
        random.nextBytes(bytes);
        return bytes;
    }

    /**
     * 混淆key
     * @param clientKey 客户端key
     * @param serverKey 服务端key
     * @return 混淆后的key
     */
    public byte[] mixKey(byte[] clientKey, byte[] serverKey) {
        byte[] bytes = new byte[AES_KEY_LENGTH];
        for (int i = 0; i < AES_KEY_LENGTH; i++) {
            byte b1 = clientKey[i];
            byte b2 = serverKey[i];
            int sum = Math.abs(b1 + b2);
            int b3 = sum % 2 == 0 ? b1 ^ b2 : b2 ^ b1;
            bytes[i] = (byte) b3;
        }
        return bytes;
    }

    public int getKeyLength() {
        return AES_KEY_LENGTH;
    }
}
