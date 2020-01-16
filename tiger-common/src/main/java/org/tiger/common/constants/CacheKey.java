package org.tiger.common.constants;

/**
 * {@link CacheKey}
 *
 * @author SongQingWei
 * @since 1.0.0
 * 2020-01-16 11:13 周四
 */
public class CacheKey {

    private static final String FAST_CONNECT_DEVICE_PREFIX = "tiger:fc:";

    public static String getDeviceIdKey(String deviceId) {
        return FAST_CONNECT_DEVICE_PREFIX + deviceId;
    }
}
