package org.tiger.boot.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.tiger.api.listener.Listener;
import org.tiger.core.server.connection.ConnectionServer;

/**
 * {@link NettyConfig}
 *
 * @author SongQingWei
 * @since 1.0.0
 * 2020-01-08 16:13 周三
 */
@Slf4j
@Configuration
public class NettyConfig {

    @Bean
    public ConnectionServer connectionServer() {
        ConnectionServer server = new ConnectionServer(null, 9000);
        server.init();
        server.start(new Listener() {

            @Override
            public void onSuccess(Object... args) {

            }

            @Override
            public void onFailure(Throwable cause) {
                System.exit(-1);
            }
        });
        return server;
    }
}
