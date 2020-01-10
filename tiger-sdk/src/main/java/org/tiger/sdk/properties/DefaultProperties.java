package org.tiger.sdk.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

/**
 * {@link DefaultProperties}
 *
 * @author SongQingWei
 * @since 1.0.0
 * 2020-01-09 10:59 周四
 */
@Configuration
@PropertySource(value = {
        "classpath:/netty.properties",
        "classpath:/redis.properties"
}, ignoreResourceNotFound = true)
public class DefaultProperties {

    @Bean
    @ConfigurationProperties(prefix = "netty")
    public NettyProperties nettyProperties() {
        return new NettyProperties();
    }
}
