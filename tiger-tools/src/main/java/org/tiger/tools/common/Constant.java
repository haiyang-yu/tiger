package org.tiger.tools.common;

import org.apache.commons.lang3.StringUtils;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

/**
 * {@link Constant}
 *
 * @author SongQingWei
 * @since 1.0.0
 * 2020-01-07 16:41 周二
 */
public interface Constant {

    /**
     * 字符集 - UTF-8
     */
    Charset CHARSET = StandardCharsets.UTF_8;

    /**
     * 空字符串
     */
    String EMPTY_STRING = StringUtils.EMPTY;

    /**
     * 空 byte 数组
     */
    byte[] EMPTY_BYTES = new byte[0];
}
