package org.tiger.tools.utils;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.nio.charset.StandardCharsets;

/**
 * {@link IoUtilTest}
 *
 * @author SongQingWei
 * @since 1.0.0
 * 2020-01-07 14:19 周二
 */
public class IoUtilTest {

    private String compressStr = "兰陵美酒郁金香，玉碗盛来琥珀光。但使主人能醉客，不知何处是他乡。";
    private byte[] compress;

    @Before
    public void compress() {
        // 压缩数据测试
        byte[] bytes = compressStr.getBytes(StandardCharsets.UTF_8);
        compress = IoUtil.compress(bytes);
        System.out.println("原始长度：" + bytes.length + "，压缩后长度：" + compress.length);
    }

    @Test
    public void decompress() {
        byte[] bytes = IoUtil.decompress(compress);
        String newCompressStr = new String(bytes, StandardCharsets.UTF_8);
        Assert.assertEquals(newCompressStr, compressStr);
    }
}
