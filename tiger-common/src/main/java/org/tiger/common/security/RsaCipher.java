package org.tiger.common.security;

import org.tiger.api.security.Cipher;
import org.tiger.tools.crypto.RsaUtil;

import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;

import static org.tiger.tools.config.DefaultConfig.Tiger.Security.PRIVATE_KEY;
import static org.tiger.tools.config.DefaultConfig.Tiger.Security.PUBLIC_KEY;

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

    public static RsaCipher create() {
        return new RsaCipher(
                CipherBox.INSTANCE.getPrivateKey(PRIVATE_KEY),
                CipherBox.INSTANCE.getPublicKey(PUBLIC_KEY)
        );
    }
}
