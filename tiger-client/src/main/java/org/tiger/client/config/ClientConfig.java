package org.tiger.client.config;

import java.io.Serializable;

/**
 * {@link ClientConfig}
 *
 * @author SongQingWei
 * @since 1.0.0
 * 2020-01-16 10:47 周四
 */
public class ClientConfig implements Serializable {

    private static final long serialVersionUID = -6285913378617501790L;
    /**
     * {@link org.tiger.common.security.cipher.CipherBox}.randomAesKey()
     */
    private byte[] key;
    /**
     * {@link org.tiger.common.security.cipher.CipherBox}.randomAesIv()
     */
    private byte[] iv;
    /**
     * 设备名称
     */
    private String osName;

    /**
     * 设备版本
     */
    private String osVersion;

    /**
     * 设备唯一标识符
     */
    private String deviceId;

    /**
     * 软件版本
     */
    private String clientVersion;
    /**
     * 用于快速重连
     */
    private String cipher;

    public byte[] getKey() {
        return key;
    }

    public void setKey(byte[] key) {
        this.key = key;
    }

    public byte[] getIv() {
        return iv;
    }

    public void setIv(byte[] iv) {
        this.iv = iv;
    }

    public String getOsName() {
        return osName;
    }

    public void setOsName(String osName) {
        this.osName = osName;
    }

    public String getOsVersion() {
        return osVersion;
    }

    public void setOsVersion(String osVersion) {
        this.osVersion = osVersion;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public String getClientVersion() {
        return clientVersion;
    }

    public void setClientVersion(String clientVersion) {
        this.clientVersion = clientVersion;
    }

    public String getCipher() {
        return cipher;
    }

    public void setCipher(String cipher) {
        this.cipher = cipher;
    }

    @Override
    public String toString() {
        return "ClientConfig{" +
                "osName='" + osName + '\'' +
                ", osVersion='" + osVersion + '\'' +
                ", deviceId='" + deviceId + '\'' +
                ", clientVersion='" + clientVersion + '\'' +
                '}';
    }
}
