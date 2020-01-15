package org.tiger.common.config;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import org.tiger.common.config.data.ConfigBean;
import org.tiger.common.config.data.RedisNode;
import org.tiger.common.utils.OsUtil;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * {@link TigerConfig}
 *
 * @author SongQingWei
 * @since 1.0.0
 * 2020-01-14 10:51 周二
 */
public interface TigerConfig {

    Config CONFIG = load();

    /**
     * 加载配置文件
     * @return {@link Config}
     */
    static Config load() {
        // 扫描加载所有可用的配置文件
        Config config = ConfigFactory.load();
        // 加载自定义配置
        String custom = "tiger.conf";
        if (config.hasPath(custom)) {
            File file = new File(config.getString(custom));
            if (file.exists()) {
                Config customConfig = ConfigFactory.parseFile(file);
                // 将两个配置文件合并
                customConfig.withFallback(config);
            }
        }
        return config;
    }

    interface Tiger {
        Config CONFIG = TigerConfig.CONFIG.getConfig("tiger");
        String LOG_LEVEL = CONFIG.getString("log-level");
        String LOG_DIR = CONFIG.getString("log-dir");
        String LOG_CONF_PATH = CONFIG.getString("log-conf-path");

        interface Core {
            Config CONFIG = Tiger.CONFIG.getConfig("core");
            int MAX_PACKET_SIZE = (int) CONFIG.getMemorySize("max-packet-size").toBytes();
            long COMPRESS_THRESHOLD = CONFIG.getMemorySize("compress-threshold").toBytes();
            int MIN_HEARTBEAT = (int) CONFIG.getDuration("min-heartbeat", TimeUnit.MILLISECONDS);
            int MAX_HEARTBEAT = (int) CONFIG.getDuration("max-heartbeat", TimeUnit.MILLISECONDS);
            int MAX_HEARTBEAT_TIMEOUT_TIMES = CONFIG.getInt("max-heartbeat-timeout-times");
            int SESSION_EXPIRED_TIME = (int) CONFIG.getDuration("session-expired-time").getSeconds();
            String EPOLL_PROVIDER = CONFIG.getString("epoll-provider");

            /**
             * 是否能够启用epoll创建netty
             * @return true：epoll
             */
            static boolean useNettyEpoll() {
                String provider = "netty";
                if (!provider.equalsIgnoreCase(EPOLL_PROVIDER)) {
                    return false;
                }
                return OsUtil.isUnix();
            }
        }

        interface Security {
            Config CONFIG = Tiger.CONFIG.getConfig("security");
            String PRIVATE_KEY = CONFIG.getString("private-key");
            String PUBLIC_KEY = CONFIG.getString("public-key");
            int AES_KEY_LENGTH = CONFIG.getInt("aes-key-length");
        }

        interface Net {
            Config CONFIG = Tiger.CONFIG.getConfig("net");
            String LOCAL_IP =CONFIG.getString("local-ip");
            String PUBLIC_IP =CONFIG.getString("public-ip");
            String CONNECT_SERVER_BIND_IP = CONFIG.getString("connect-server-bind-ip");
            int CONNECT_SERVER_PORT = CONFIG.getInt("connect-server-port");
            String CONNECT_SERVER_REGISTER_IP = CONFIG.getString("connect-server-register-ip");
            Map<String, Object> CONNECT_SERVER_REGISTER_ATTR = CONFIG.getObject("connect-server-register-attr").unwrapped();

            String GATEWAY_SERVER_BIND_IP = CONFIG.getString("gateway-server-bind-ip");
            int GATEWAY_SERVER_PORT = CONFIG.getInt("gateway-server-port");
            String GATEWAY_SERVER_REGISTER_IP = CONFIG.getString("gateway-server-register-ip");
            String GATEWAY_SERVER_NET = CONFIG.getString("gateway-server-net");
            String GATEWAY_SERVER_MULTICAST = CONFIG.getString("gateway-server-multicast");

            int GATEWAY_CLIENT_PORT = CONFIG.getInt("gateway-client-port");
            String GATEWAY_CLIENT_MULTICAST = CONFIG.getString("gateway-client-multicast");
            int GATEWAY_CLIENT_NUM = CONFIG.getInt("gateway-client-num");

            int ADMIN_SERVER_PORT = CONFIG.getInt("admin-server-port");

            int WEBSOCKET_SERVER_PORT = CONFIG.getInt("ws-server-port");
            String WEBSOCKET_PATH = CONFIG.getString("ws-path");

            /**
             * 是否使用 tcp
             * @return true：tcp
             */
            static boolean tcpGateway() {
                return "tcp".equalsIgnoreCase(GATEWAY_SERVER_NET);
            }

            /**
             * 是否使用 udp
             * @return true：udp
             */
            static boolean udpGateway() {
                return "udp".equalsIgnoreCase(GATEWAY_SERVER_NET);
            }

            /**
             * 是否使用 udt
             * @return true：udt
             */
            static boolean udtGateway() {
                return "udt".equalsIgnoreCase(GATEWAY_SERVER_NET);
            }

            /**
             * 是否使用 sctp
             * @return true：sctp
             */
            static boolean sctpGateway() {
                return "sctp".equalsIgnoreCase(GATEWAY_SERVER_NET);
            }

            /**
             * 是否启用websocket
             * @return true：启用
             */
            static boolean wsEnabled() {
                return WEBSOCKET_SERVER_PORT > 0;
            }

            interface SndBuf {
                Config CONFIG = Net.CONFIG.getConfig("snd-buf");
                int CONNECT_SERVER = (int) CONFIG.getMemorySize("connect-server").toBytes();
                int GATEWAY_SERVER = (int) CONFIG.getMemorySize("gateway-server").toBytes();
                int GATEWAY_CLIENT = (int) CONFIG.getMemorySize("gateway-client").toBytes();
            }

            interface RcvBuf {
                Config CONFIG = Net.CONFIG.getConfig("rcv-buf");
                int CONNECT_SERVER = (int) CONFIG.getMemorySize("connect-server").toBytes();
                int GATEWAY_SERVER = (int) CONFIG.getMemorySize("gateway-server").toBytes();
                int GATEWAY_CLIENT = (int) CONFIG.getMemorySize("gateway-client").toBytes();
            }

            interface WriteBufferWaterMark {
                Config CONFIG = Net.CONFIG.getConfig("write-buffer-water-mark");
                int CONNECT_SERVER_LOW = (int) CONFIG.getMemorySize("connect-server-low").toBytes();
                int CONNECT_SERVER_HIGH = (int) CONFIG.getMemorySize("connect-server-high").toBytes();
                int GATEWAY_SERVER_LOW = (int) CONFIG.getMemorySize("gateway-server-low").toBytes();
                int GATEWAY_SERVER_HIGH = (int) CONFIG.getMemorySize("gateway-server-high").toBytes();
            }

            interface TrafficShaping {
                Config CONFIG = Net.CONFIG.getConfig("traffic-shaping");

                interface ConnectServer {
                    Config CONFIG = TrafficShaping.CONFIG.getConfig("connect-server");
                    boolean ENABLED = CONFIG.getBoolean("enabled");
                    long CHECK_INTERVAL = CONFIG.getDuration("check-interval", TimeUnit.MILLISECONDS);
                    long WRITE_GLOBAL_LIMIT = CONFIG.getMemorySize("write-global-limit").toBytes();
                    long READ_GLOBAL_LIMIT = CONFIG.getMemorySize("read-global-limit").toBytes();
                    long WRITE_CHANNEL_LIMIT = CONFIG.getMemorySize("write-channel-limit").toBytes();
                    long READ_CHANNEL_LIMIT = CONFIG.getMemorySize("read-channel-limit").toBytes();
                }

                interface GatewayServer {
                    Config CONFIG = TrafficShaping.CONFIG.getConfig("gateway-server");
                    boolean ENABLED = CONFIG.getBoolean("enabled");
                    long CHECK_INTERVAL = CONFIG.getDuration("check-interval", TimeUnit.MILLISECONDS);
                    long WRITE_GLOBAL_LIMIT = CONFIG.getMemorySize("write-global-limit").toBytes();
                    long READ_GLOBAL_LIMIT = CONFIG.getMemorySize("read-global-limit").toBytes();
                    long WRITE_CHANNEL_LIMIT = CONFIG.getMemorySize("write-channel-limit").toBytes();
                    long READ_CHANNEL_LIMIT = CONFIG.getMemorySize("read-channel-limit").toBytes();
                }

                interface GatewayClient {
                    Config CONFIG = TrafficShaping.CONFIG.getConfig("gateway-client");
                    boolean ENABLED = CONFIG.getBoolean("enabled");
                    long CHECK_INTERVAL = CONFIG.getDuration("check-interval", TimeUnit.MILLISECONDS);
                    long WRITE_GLOBAL_LIMIT = CONFIG.getMemorySize("write-global-limit").toBytes();
                    long READ_GLOBAL_LIMIT = CONFIG.getMemorySize("read-global-limit").toBytes();
                    long WRITE_CHANNEL_LIMIT = CONFIG.getMemorySize("write-channel-limit").toBytes();
                    long READ_CHANNEL_LIMIT = CONFIG.getMemorySize("read-channel-limit").toBytes();
                }
            }
        }

        interface Zk {
            Config CONFIG = Tiger.CONFIG.getConfig("zk");
            String SERVER_ADDRESS = CONFIG.getString("server-address");
            String NAMESPACE = CONFIG.getString("namespace");
            String DIGEST = CONFIG.getString("digest");
            String WATCH_PATH = CONFIG.getString("watch-path");
            int CONNECTION_TIMEOUT = (int) CONFIG.getDuration("connection-timeout", TimeUnit.MILLISECONDS);
            int SESSION_TIMEOUT = (int) CONFIG.getDuration("session-timeout", TimeUnit.MILLISECONDS);

            interface Retry {
                Config CONFIG = Zk.CONFIG.getConfig("retry");
                int BASE_SLEEP_TIME = (int) CONFIG.getDuration("base-sleep-time", TimeUnit.MILLISECONDS);
                int MAX_RETRIES = CONFIG.getInt("max-retries");
                int MAX_SLEEP = (int) CONFIG.getDuration("max-sleep", TimeUnit.MILLISECONDS);
            }
        }

        interface Redis {
            Config CONFIG =Tiger.CONFIG.getConfig("redis");
            String CLUSTER_MODEL = CONFIG.getString("cluster-model");
            String SENTINEL_MASTER = CONFIG.getString("sentinel-master");
            String PASSWORD = CONFIG.getString("password");
            int DATABASE = CONFIG.getInt("database");
            List<RedisNode> NODES = CONFIG.getList("nodes").stream()
                    .map(hostAndPort -> RedisNode.from(hostAndPort.unwrapped().toString()))
                    .distinct()
                    .collect(Collectors.toList());

            /**
             * 是否是集群模式
             * @return true：是
             */
            static boolean isCluster() {
                return "cluster".equalsIgnoreCase(CLUSTER_MODEL);
            }

            /**
             * 是否是哨兵模式
             * @return true：是
             */
            static boolean isSentinel() {
                return "sentinel".equalsIgnoreCase(CLUSTER_MODEL);
            }

            /**
             * 加载连接池配置
             * @param clazz Class<T>
             * @param <T> T
             * @return 连接池配置 T
             */
            static <T> T getPoolConfig(Class<T> clazz) {
                return ConfigBean.createInternal(CONFIG.getObject("pool").toConfig(), clazz);
            }
        }

        interface Http {
            Config CONFIG = Tiger.CONFIG.getConfig("http");
            boolean PROXY_ENABLED = CONFIG.getBoolean("proxy-enabled");
            int MAX_CONN_PER_HOST = CONFIG.getInt("max-conn-per-host");
            int DEFAULT_READ_TIMEOUT = (int) CONFIG.getDuration("default-read-timeout", TimeUnit.MILLISECONDS);
            long MAX_CONTENT_LENGTH = CONFIG.getBytes("max-content-length");
        }

        interface ThreadPool {
            Config CONFIG = Tiger.CONFIG.getConfig("thread-pool");
            int CONN_WORK = CONFIG.getInt("conn-work");
            int GATEWAY_SERVER_WORK = CONFIG.getInt("gateway-server-work");
            int HTTP_WORK = CONFIG.getInt("http-work");
            int ACK_TIMER = CONFIG.getInt("ack-timer");
            int PUSH_TASK = CONFIG.getInt("push-task");
            int GATEWAY_CLIENT_WORK = CONFIG.getInt("gateway-client-work");
            int PUSH_CLIENT = CONFIG.getInt("push-client");
        }

        interface PushFlowControl {
            Config CONFIG = Tiger.CONFIG.getConfig("push-flow-control");

            interface Global {
                Config CONFIG = PushFlowControl.CONFIG.getConfig("global");
                int LIMIT = CONFIG.getNumber("limit").intValue();
                int MAX = CONFIG.getInt("max");
                int DURATION = (int) CONFIG.getDuration("duration", TimeUnit.MILLISECONDS);
            }

            interface Broadcast {
                Config CONFIG = PushFlowControl.CONFIG.getConfig("broadcast");
                int LIMIT = CONFIG.getNumber("limit").intValue();
                int MAX = CONFIG.getInt("max");
                int DURATION = (int) CONFIG.getDuration("duration", TimeUnit.MILLISECONDS);
            }
        }
    }
}
