package org.tiger.common.security.cipher;

import org.tiger.api.crypto.Cipher;
import org.tiger.common.security.crypto.RsaUtil;

import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;

import static org.tiger.common.config.TigerConfig.Tiger.Security.PRIVATE_KEY;
import static org.tiger.common.config.TigerConfig.Tiger.Security.PUBLIC_KEY;

/**
 * {@link RsaCipher}
 *
 * @author SongQingWei
 * @since 1.0.0
 * 2020-01-14 16:15 周二
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
