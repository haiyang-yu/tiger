package org.tiger.common.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.IOException;
import java.util.zip.DeflaterOutputStream;
import java.util.zip.InflaterInputStream;

/**
 * {@link IoUtil}
 *
 * @author SongQingWei
 * @since 1.0.0
 * 2020-01-14 11:07 周二
 */
public final class IoUtil {

    private static final Logger LOGGER = LoggerFactory.getLogger(IoUtil.class);

    /**
     * 释放资源
     * @param closeables {@link Closeable}
     */
    public static void close(Closeable... closeables) {
        if (closeables != null && closeables.length > 0) {
            for (Closeable closeable : closeables) {
                try {
                    closeable.close();
                } catch (IOException e) {
                    e.printStackTrace();
                    LOGGER.error("close closeable ex", e);
                }
            }
        }
    }

    /**
     * 数据压缩
     * @param data 待压缩数据
     * @return 压缩后数据
     */
    public static byte[] compress(byte[] data) {
        ByteArrayOutputStream bos = new ByteArrayOutputStream(data.length / 4);
        DeflaterOutputStream dos = new DeflaterOutputStream(bos);
        try {
            dos.write(data);
            dos.flush();
            dos.finish();
        } catch (IOException e) {
            LOGGER.error("compress ex", e);
            return new byte[0];
        } finally {
            close(dos, bos);
        }
        return bos.toByteArray();
    }

    /**
     * 数据解压缩
     * @param data 待解压缩数据
     * @return 解压缩后的数据
     */
    public static byte[] decompress(byte[] data) {
        ByteArrayInputStream bis = new ByteArrayInputStream(data);
        InflaterInputStream iis = new InflaterInputStream(bis);
        ByteArrayOutputStream bos = new ByteArrayOutputStream(data.length * 4);
        byte[] bytes = new byte[1024];
        int readLen;
        try {
            while ((readLen = iis.read(bytes)) != -1) {
                bos.write(bytes, 0, readLen);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            close(bis, iis, bos);
        }
        return bos.toByteArray();
    }
}
