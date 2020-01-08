package org.tiger.api.connection;

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
public class SessionContext implements Serializable {

    private static final long serialVersionUID = 3567092969159990961L;

    /**
     * 设备名称
     */
    public String osName;

    /**
     * 设备版本
     */
    public String osVersion;

    /**
     * 设备唯一标识符
     */
    public String deviceId;

    /**
     * 软件版本
     */
    public String clientVersion;

    /**
     * 心跳周期（毫秒）
     */
    public int heartbeat = 10000;

    /**
     * 加解密
     */
    public Cipher cipher;

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

    public void changeCipher(Cipher cipher) {
        this.cipher = cipher;
    }
}
