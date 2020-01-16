package org.tiger.netty.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tiger.common.utils.OsUtil;

/**
 * {@link EpollUtil}
 *
 * @author SongQingWei
 * @since 1.0.0
 * 2020-01-16 10:03 周四
 */
public class EpollUtil {

    private static final Logger LOGGER = LoggerFactory.getLogger(EpollUtil.class);

    public static boolean useNettyEpoll() {
        if (OsUtil.isUnix()) {
            try {
                Class.forName("io.netty.channel.epoll.Native");
                return true;
            } catch (ClassNotFoundException e) {
                LOGGER.warn("can not load netty epoll, switch nio model.");
            }
        }
        return false;
    }
}
