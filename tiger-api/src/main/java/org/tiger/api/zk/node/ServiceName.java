package org.tiger.api.zk.node;

/**
 * {@link ServiceName}
 *
 * @author SongQingWei
 * @since 1.0.0
 * 2020-01-15 10:32 周三
 */
public interface ServiceName {

    String CONNECT_SERVER = "/cluster/cs";
    String WEBSERVICE_SERVER = "/cluster/ws";
    String GATEWAY_SERVER = "/cluster/gs";
    String DNS_MAPPING = "/dns/mapping";

    String ATTR_PUBLIC_IP = "public_ip";
}
