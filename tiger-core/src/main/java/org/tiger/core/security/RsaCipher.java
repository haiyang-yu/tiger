package org.tiger.core.security;

import org.tiger.api.security.Cipher;
import org.tiger.tools.crypto.RsaUtil;

import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;

/**
 * {@link RsaCipher}
 *
 * @author SongQingWei
 * @since 1.0.0
 * 2020-01-07 17:36 周二
 */
public class RsaCipher implements Cipher {

    private final RSAPrivateKey privateKey;
    private final RSAPublicKey publicKey;

    private RsaCipher(RSAPrivateKey privateKey, RSAPublicKey publicKey) {
        this.privateKey = privateKey;
        this.publicKey = publicKey;
    }

    @Override
    public byte[] decrypt(byte[] data) {
        return RsaUtil.decryptByPrivateKey(data, privateKey);
    }

    @Override
    public byte[] encrypt(byte[] data) {
        return RsaUtil.encryptByPublicKey(data, publicKey);
    }

    public static RsaCipher create(String privateKey, String publicKey) {
        return new RsaCipher(CipherBox.INSTANCE.getPrivateKey(privateKey), CipherBox.INSTANCE.getPublicKey(publicKey));
    }
}
