package org.tiger.sdk.properties;

import lombok.Data;

/**
 * {@link NettyProperties}
 *
 * @author SongQingWei
 * @since 1.0.0
 * 2020-01-08 18:30 周三
 */
@Data
public class NettyProperties {

    private String connectServerHost;
    private Integer connectServerPort;
    private String gatewayServerHost;
    private Integer gatewayServerPort;
}
