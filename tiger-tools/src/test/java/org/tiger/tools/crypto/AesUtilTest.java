package org.tiger.tools.crypto;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.Arrays;

/**
 * {@link AesUtilTest}
 *
 * @author SongQingWei
 * @since 1.0.0
 * 2020-01-07 14:57 周二
 */
public class AesUtilTest {

    private String data = "123456";
    private byte[] key;
    private byte[] iv;
    private byte[] encrypt;

    @Before
    public void getSecretKey() {
        byte[] key = new byte[16];
        SecureRandom random = new SecureRandom();
        random.nextBytes(key);
        this.key = key;
        byte[] iv = new byte[16];
        random.nextBytes(iv);
        this.iv = iv;
    }

    @Test
    public void encrypt() {
        Assert.assertNotNull(key);
        Assert.assertNotNull(iv);
        encrypt = AesUtil.encrypt(data.getBytes(StandardCharsets.UTF_8), key, iv);
        System.out.println(Arrays.toString(encrypt));
    }

    @After
    public void decrypt() {
        byte[] decrypt = AesUtil.decrypt(encrypt, key, iv);
        Assert.assertEquals(data, new String(decrypt, StandardCharsets.UTF_8));
    }
}
