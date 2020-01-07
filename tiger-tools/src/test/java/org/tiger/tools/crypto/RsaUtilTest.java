package org.tiger.tools.crypto;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.tiger.tools.common.Pair;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.util.Arrays;

import static org.tiger.tools.crypto.RsaUtil.RAS_KEY_SIZE;

/**
 * {@link RsaUtilTest}
 *
 * @author SongQingWei
 * @since 1.0.0
 * 2020-01-07 15:15 周二
 */
public class RsaUtilTest {

    private String data = "123456";
    private RSAPrivateKey privateKey;
    private RSAPublicKey publicKey;

    @Before
    public void genKeyPair() {
        Pair<RSAPublicKey, RSAPrivateKey> pair = RsaUtil.genKeyPair(RAS_KEY_SIZE);
        assert pair != null;
        privateKey = pair.getValue();
        publicKey = pair.getKey();
    }

    @Test
    public void decodePrivateKey() throws Exception {
        PrivateKey privateKey = RsaUtil.decodePrivateKey(RsaUtil.encodeBase64(this.privateKey));
        Assert.assertEquals(this.privateKey, privateKey);
    }

    @Test
    public void decodePublicKey() throws Exception {
        PublicKey publicKey = RsaUtil.decodePublicKey(RsaUtil.encodeBase64(this.publicKey));
        Assert.assertEquals(this.publicKey, publicKey);
    }

    @Test
    public void sign() throws Exception {
        byte[] bytes = data.getBytes(StandardCharsets.UTF_8);
        String sign = RsaUtil.sign(bytes, RsaUtil.encodeBase64(privateKey));
        System.out.println(sign);
        boolean verify = RsaUtil.verify(bytes, RsaUtil.encodeBase64(publicKey), sign);
        assert verify;
    }

    @Test
    public void getPublicKey() {
        BigInteger modulus = privateKey.getModulus();
        BigInteger exponent = privateKey.getPrivateExponent();
        RSAPublicKey publicKey = RsaUtil.getPublicKey(modulus.toString(), exponent.toString());
        System.out.println(publicKey);
    }

    @Test
    public void encryptByPublicKey() {
        byte[] bytes = RsaUtil.encryptByPublicKey(data.getBytes(StandardCharsets.UTF_8), publicKey);
        System.out.println("公钥加密：" + Arrays.toString(bytes));
        byte[] decrypt = RsaUtil.decryptByPrivateKey(bytes, privateKey);
        System.out.println("私钥解密：" + new String(decrypt, StandardCharsets.UTF_8));
    }

    @Test
    public void encryptByPrivateKey() throws Exception {
        byte[] bytes = RsaUtil.encryptByPrivateKey(data.getBytes(StandardCharsets.UTF_8), RsaUtil.encodeBase64(privateKey));
        System.out.println("私钥加密：" + Arrays.toString(bytes));
        byte[] decrypt = RsaUtil.decryptByPublicKey(bytes, RsaUtil.encodeBase64(publicKey));
        System.out.println("公钥解密：" + new String(decrypt, StandardCharsets.UTF_8));
    }
}
