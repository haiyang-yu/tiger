package org.tiger.sample.config;

import com.typesafe.config.ConfigRenderOptions;
import org.tiger.common.config.TigerConfig;
import org.tiger.common.log.TigerLog;

/**
 * {@link LoadConfig}
 *
 * @author SongQingWei
 * @since 1.0.0
 * 2020-01-14 11:48 周二
 */
public class LoadConfig {

    public static void main(String[] args) {
        TigerLog.init();
        TigerLog.CONSOLE.info(TigerConfig.CONFIG.root().render(ConfigRenderOptions.concise().setFormatted(true)));
        TigerLog.CONSOLE.info("--------------------------------->");
        TigerLog.CONSOLE.info(TigerConfig.Tiger.Net.CONNECT_SERVER_REGISTER_ATTR.toString());
    }
}
