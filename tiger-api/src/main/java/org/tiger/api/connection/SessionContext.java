package org.tiger.api.connection;

import lombok.Builder;
import org.apache.commons.lang3.StringUtils;
import org.tiger.api.security.Cipher;

import java.io.Serializable;
import java.util.Objects;

/**
 * {@link SessionContext}
 *
 * @author SongQingWei
 * @since 1.0.0
 * 2020-01-07 18:06 周二
 */
@Builder
public class SessionContext implements Serializable {

    /**
     * 设备唯一标识符
     */
    private String deviceId;

    /**
     * 心跳周期
     */
    @Builder.Default
    private int heartbeat = 10000;

    /**
     * 设备名称
     */
    private String osName;

    /**
     * 设备版本
     */
    private String osVersion;

    /**
     * 软件版本
     */
    private String clientVersion;

    /**
     * 加解密
     */
    private Cipher cipher;

    /**
     * 是否握手成功
     * @return true：握手成功
     */
    public boolean handshakeOk() {
        return StringUtils.isNotBlank(deviceId);
    }

    /**
     * 是否使用安全认证
     * @return true：使用安全认证
     */
    public boolean isSecurity() {
        return Objects.nonNull(cipher);
    }
}
