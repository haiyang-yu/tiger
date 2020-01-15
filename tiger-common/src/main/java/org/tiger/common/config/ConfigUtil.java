package org.tiger.common.config;

import org.apache.commons.lang3.StringUtils;
import org.tiger.common.utils.IpUtil;

import static org.tiger.common.config.TigerConfig.Tiger.Net.*;

/**
 * {@link ConfigUtil}
 *
 * @author SongQingWei
 * @since 1.0.0
 * 2020-01-15 09:32 周三
 */
public final class ConfigUtil {

    public static String getLocalIp() {
        if (StringUtils.isNotBlank(LOCAL_IP)) {
            return LOCAL_IP;
        }
        return IpUtil.lookupLocalIp();
    }

    public static String getPublicIp() {
        if (StringUtils.isNotBlank(PUBLIC_IP)) {
            return PUBLIC_IP;
        }
        String extranetIp = IpUtil.lookupExtranetIp();
        return StringUtils.isBlank(extranetIp) ? IpUtil.lookupLocalIp() : extranetIp;
    }

    public static String getConnectServerRegisterIp() {
        if (StringUtils.isNotBlank(CONNECT_SERVER_REGISTER_IP)) {
            return CONNECT_SERVER_REGISTER_IP;
        }
        return getPublicIp();
    }

    public static String getGatewayServerRegisterIp() {
        if (StringUtils.isNotBlank(GATEWAY_SERVER_REGISTER_IP)) {
            return GATEWAY_SERVER_REGISTER_IP;
        }
        return getLocalIp();
    }
}
