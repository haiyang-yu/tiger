package org.tiger.sdk.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.tiger.core.netty.server.connection.ConnectionServer;
import org.tiger.sdk.properties.NettyProperties;

import javax.annotation.Resource;

/**
 * {@link NettyConfig}
 *
 * @author SongQingWei
 * @since 1.0.0
 * 2020-01-09 10:45 周四
 */
@Configuration
public class NettyConfig {

    @Resource
    private NettyProperties nettyProperties;

    @Bean
    @ConditionalOnProperty(prefix = "netty", name = "open", havingValue = "true")
    public ConnectionServer connectionServer() {
        ConnectionServer server = new ConnectionServer(nettyProperties.getConnectServerHost(), nettyProperties.getConnectServerPort());
        if (!server.syncStart()) {
            System.exit(-1);
        }
        return server;
    }
}
