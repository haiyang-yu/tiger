package org.tiger.client.model;

import java.io.Serializable;

/**
 * {@link ClientCache}
 *
 * @author SongQingWei
 * @since 1.0.0
 * 2020-01-16 11:21 周四
 */
public final class ClientCache implements Serializable {

    private static final long serialVersionUID = -2326300626624859881L;

    private String sessionId;
    private long expireTime;
    private String cipher;

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public long getExpireTime() {
        return expireTime;
    }

    public void setExpireTime(long expireTime) {
        this.expireTime = expireTime;
    }

    public String getCipher() {
        return cipher;
    }

    public void setCipher(String cipher) {
        this.cipher = cipher;
    }

    @Override
    public String toString() {
        return "ClientCache{" +
                "sessionId='" + sessionId + '\'' +
                ", expireTime=" + expireTime +
                ", cipher='" + cipher + '\'' +
                '}';
    }
}
