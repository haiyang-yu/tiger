package org.tiger.tools.crypto;

import org.junit.Test;

import java.io.File;

/**
 * {@link Md5UtilTest}
 *
 * @author SongQingWei
 * @since 1.0.0
 * 2020-01-07 14:38 周二
 */
public class Md5UtilTest {

    @Test
    public void encrypt() {
        File file = new File("D:\\data\\city_area.json");
        String fileEncrypt = Md5Util.encrypt(file);
        System.out.println("文件MD5:" + fileEncrypt);
        String str = "123456";
        System.out.println("字符串MD5:" + Md5Util.encrypt(str));
    }

    @Test
    public void hmacSha1() {
        String str = "123456";
        System.out.println("字符串hmacSha1:" + Md5Util.hmacSha1(str, str));
    }

    @Test
    public void sha1() {
        String str = "123456";
        System.out.println("字符串hmacSha1:" + Md5Util.sha1(str));
    }
}
