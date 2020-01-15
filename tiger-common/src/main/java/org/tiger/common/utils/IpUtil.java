package org.tiger.common.utils;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Enumeration;

/**
 * {@link IpUtil}
 *
 * @author SongQingWei
 * @since 1.0.0
 * 2020-01-15 09:40 周三
 */
public final class IpUtil {

    private static final Logger LOGGER = LoggerFactory.getLogger(IpUtil.class);

    /**
     * 内网IP
     */
    private static String local_ip;
    /**
     * 外网IP
     */
    private static String extranet_ip;

    /**
     * 只获取第一块网卡绑定的ip地址
     * @param isLocalNetwork 是否获取局域网IP
     * @return 局域网IP
     */
    private static String getNetworkAddress(boolean isLocalNetwork) {
        try {
            Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
            while (interfaces.hasMoreElements()) {
                Enumeration<InetAddress> addresses = interfaces.nextElement().getInetAddresses();
                while (addresses.hasMoreElements()) {
                    InetAddress address = addresses.nextElement();
                    if (address instanceof Inet6Address || address.isLoopbackAddress() || address.getHostAddress().contains(":")) {
                        continue;
                    }
                    if (isLocalNetwork) {
                        if (address.isSiteLocalAddress()) {
                            return address.getHostAddress();
                        }
                    } else {
                        if (!address.isSiteLocalAddress()) {
                            return address.getHostAddress();
                        }
                    }
                }
            }
            LOGGER.debug("get network address is null, isLocalNetwork={}", isLocalNetwork);
            return isLocalNetwork ? InetAddress.getLocalHost().getHostAddress() : null;
        } catch (Exception e) {
            LOGGER.error("get network address occur exception", e);
            return isLocalNetwork ? "127.0.0.1" : null;
        }
    }

    /**
     * 查找内网IP
     * @return 内网IP
     */
    public static String lookupLocalIp() {
        if (StringUtils.isBlank(local_ip)) {
            local_ip = getNetworkAddress(true);
        }
        return local_ip;
    }

    /**
     * 查找外网IP
     * @return 内网IP
     */
    public static String lookupExtranetIp() {
        if (StringUtils.isBlank(extranet_ip)) {
            extranet_ip = getNetworkAddress(false);
        }
        return extranet_ip;
    }
}
