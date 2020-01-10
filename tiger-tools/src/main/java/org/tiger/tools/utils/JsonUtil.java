package org.tiger.tools.utils;

import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 * {@link JsonUtil}
 *
 * @author SongQingWei
 * @since 1.0.0
 * 2020-01-10 11:13 周五
 */
@Slf4j
public class JsonUtil {

    public static String toJson(Object bean) {
        try {
            return JSON.toJSONString(bean);
        } catch (Exception e) {
            log.error("JsonUtil.toJson occur ex", e);
        }
        return null;
    }

    public static <T> T parseObject(String json, Class<T> clazz) {
        try {
            return JSON.parseObject(json, clazz);
        } catch (Exception e) {
            log.error("JsonUtil.parseObject occur ex, json=" + json + ", class=" + clazz, e);
        }
        return null;
    }

    public static <T> T parseObject(byte[] bytes, Class<T> clazz) {
        return parseObject(new String(bytes, StandardCharsets.UTF_8), clazz);
    }

    public static <T> T parseObject(String json, Type type) {
        try {
            return JSON.parseObject(json, type);
        } catch (Exception e) {
            log.error("JsonUtil.parseObject occur ex, json=" + json + ", type=" + type, e);
        }
        return null;
    }

    public static <T> List<T> parseArray(String json, Class<T> clazz) {
        try {
            return JSON.parseArray(json, clazz);
        } catch (Exception e) {
            log.error("JsonUtil.parseArray occur ex, json=" + json + ", class=" + clazz, e);
        }
        return new ArrayList<>();
    }

    public static boolean mayJson(String json) {
        if (StringUtils.isBlank(json)) {
            return false;
        }
        return (json.charAt(0) == '{' && json.charAt(json.length() - 1) == '}')
                || (json.charAt(0) == '[' && json.charAt(json.length() - 1) == ']');
    }
}
