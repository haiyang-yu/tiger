package org.tiger.common.log;

import ch.qos.logback.classic.util.ContextInitializer;
import com.typesafe.config.ConfigRenderOptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tiger.common.config.TigerConfig;

/**
 * {@link TigerLog}
 *
 * @author SongQingWei
 * @since 1.0.0
 * 2020-01-14 10:50 周二
 */
public interface TigerLog {

    boolean LOG_INIT = init();

    /**
     * 初始化日志模块
     * @return true：初始化成功
     */
    static boolean init() {
        if (LOG_INIT) {
            return true;
        }
        System.setProperty("log.home", TigerConfig.Tiger.LOG_DIR);
        System.setProperty("log.level", TigerConfig.Tiger.LOG_LEVEL);
        System.setProperty(ContextInitializer.CONFIG_FILE_PROPERTY, TigerConfig.Tiger.LOG_CONF_PATH);
        LoggerFactory.getLogger("tiger.console.log")
                .info(TigerConfig.CONFIG.root().render(ConfigRenderOptions.concise().setFormatted(true)));
        return true;
    }

    Logger CONSOLE = LoggerFactory.getLogger("tiger.console.log");

    Logger CONNECT = LoggerFactory.getLogger("tiger.connect.log");

    Logger CACHE = LoggerFactory.getLogger("tiger.cache.log");

    Logger ZK = LoggerFactory.getLogger("tiger.zk.log");

    Logger HEARTBEAT = LoggerFactory.getLogger("tiger.heartbeat.log");

    Logger HTTP = LoggerFactory.getLogger("tiger.http.log");

    Logger PUSH = LoggerFactory.getLogger("tiger.push.log");

    Logger PROFILE = LoggerFactory.getLogger("tiger.profile.log");
}
