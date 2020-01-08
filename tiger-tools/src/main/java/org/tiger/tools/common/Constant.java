package org.tiger.tools.common;

import org.apache.commons.lang3.StringUtils;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

/**
 * {@link Constant}
 *
 * @author SongQingWei
 * @since 1.0.0
 * 2020-01-07 16:41 周二
 */
public interface Constant {

    /**
     * 字符集 - UTF-8
     */
    Charset CHARSET = StandardCharsets.UTF_8;

    /**
     * 空字符串
     */
    String EMPTY_STRING = StringUtils.EMPTY;

    /**
     * 空 byte 数组
     */
    byte[] EMPTY_BYTES = new byte[0];

    /**
     * 私钥
     */
    String PRIVATE_KEY = "MIICeAIBADANBgkqhkiG9w0BAQEFAASCAmIwggJeAgEAAoGBAJMT6Mb5s4XAFYSqH+sfAhcDB35xFROcv65N8HiY513SmVDllLIB6ajefk6WCBENBLW/ZZnmdDXcCrjffM2eDJOptbvJBqTJdYqa5/eetSHLRmQjS2uhXIiKj8mjmxDivxFxpXSTc+AqISURm5oX1lZsgjiL8NIDuV8/yvqr5l7dAgMBAAECgYALvm463RthlaniMvdjfdFb5wkFJqBpNX30vBJ0frkSu8s0M3DBGff/XOy297HjMqfqn0LMnCMxXcWZZ3b+sB0O2fojG3MS7tHAqBb5gKs0WIbFICoPOu23wq0I/voAczw5PQom8SmbgnwVI2I2eZ93SydgUPLvIqofSQBhZ6wMHQJBAOR94PvLemA9H3ks9xxYEmPTksvHnkNpMEk4pFVv78RJBE2pp0W+n6ZzhWX4KZZkiH3q8df/M2/h6g0ejjrTkIcCQQCkyNmyXXCqclEdJmbquZFZgCQeRN8TpKWtRMSKVy+J9Hy3BNpmF6WUSDr4OwBjaQ1A5BtkfChbU3kniTriAiJ7AkEArehlSiBYF1HOV7NyrX8XGCXbcACSI6q3FrPm2CRncJVGkjnTV9E7wDvix3a3aIFCJdyhkA3sP3bKZovgI0QNlwJBAIE9yoh7guccPAQ531RP8PEacLmQ2MmmRA8utVuvX+i6aap90vFpsIMpV57jX72YYj236sgYHOrsAEUC+7zRYcsCQQDCBp5ZeoNMzCLaS3AkzB6QIGtiAmE2CNQXQqu6ctkDH31wykED0NHcyzrSSxzEPS01B+m0CmgjD82Wvz6Zz6aD";

    /**
     * 公钥
     */
    String PUBLIC_KEY = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCTE+jG+bOFwBWEqh/rHwIXAwd+cRUTnL+uTfB4mOdd0plQ5ZSyAemo3n5OlggRDQS1v2WZ5nQ13Aq433zNngyTqbW7yQakyXWKmuf3nrUhy0ZkI0troVyIio/Jo5sQ4r8RcaV0k3PgKiElEZuaF9ZWbII4i/DSA7lfP8r6q+Ze3QIDAQAB";

    /* ================================================================== */
    /*                                Netty                               */
    /* ================================================================== */
    /**
     * 允许的心跳连续超时的最大次数
     */
    int MAX_HEARTBEAT_TIMEOUT_TIMES = 2;

    /**
     * 心跳间隔（毫秒）
     */
    int HEARTBEAT = 30000;

    /**
     * 数据包启用压缩的临界值，超过该值后对数据进行压缩（10KB）
     */
    int COMPRESS_THRESHOLD = 1024 * 10;

    /**
     * 系统允许传输的最大包的大小
     */
    int MAX_PACKET_SIZE = 1024 * 10;

    /**
     * 是否启用流量整形，限流
     */
    boolean TRAFFIC_SHAPING_ENABLED = true;
}
