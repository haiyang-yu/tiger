package org.tiger.core.handler;

import org.apache.commons.lang3.StringUtils;
import org.tiger.api.connection.Connection;
import org.tiger.api.connection.SessionContext;
import org.tiger.api.protocol.Packet;
import org.tiger.common.config.ConfigUtil;
import org.tiger.common.config.TigerConfig;
import org.tiger.common.handler.BaseMessageHandler;
import org.tiger.common.log.TigerLog;
import org.tiger.common.message.HandshakeMessage;
import org.tiger.common.message.HandshakeOkMessage;
import org.tiger.common.security.cipher.AesCipher;
import org.tiger.common.security.cipher.CipherBox;

import java.util.Objects;
import java.util.UUID;

/**
 * {@link HandshakeHandler}
 *
 * @author SongQingWei
 * @since 1.0.0
 * 2020-01-17 15:06 周五
 */
public class HandshakeHandler extends BaseMessageHandler<HandshakeMessage> {

    @Override
    public HandshakeMessage decode(Packet packet, Connection connection) {
        return new HandshakeMessage(packet, connection);
    }

    @Override
    public void handle(HandshakeMessage message) {
        if (message.getConnection().getSessionContext().isSecurity()) {
            doSecurity(message);
        } else {
            doInSecurity(message);
        }
    }

    private void doSecurity(HandshakeMessage message) {
        // 1.校验客户端消息字段
        if (!checkField(message)) {
            return;
        }
        // 2.重复握手判断
        if (!checkRepeatHandshake(message)) {
            return;
        }
        byte[] clientKey = message.getClientKey();
        byte[] iv = message.getIv();
        byte[] serverKey = CipherBox.INSTANCE.randomAesKey();
        byte[] sessionKey = CipherBox.INSTANCE.mixKey(clientKey, serverKey);
        // 3.更换会话密钥RSA=>AES(clientKey)
        SessionContext context = message.getConnection().getSessionContext();
        context.cipher = new AesCipher(clientKey, iv);
        // 4.生成可复用session, 用于快速重连
        String sessionId = UUID.randomUUID().toString();
        // 5.计算心跳时间
        int heartbeat = ConfigUtil.getHeartbeat(message.getMinHeartbeat(), message.getMaxHeartbeat());
        // 6.响应握手成功消息
        sendOk(message, serverKey, sessionKey, iv, sessionId, heartbeat);
    }

    private void doInSecurity(HandshakeMessage message) {
        // 1.校验客户端消息字段
        if (checkField(message)) {
            // 2.重复握手判断
            if (checkRepeatHandshake(message)) {
                // 3.生成可复用session, 用于快速重连
                String sessionId = UUID.randomUUID().toString();
                // 4.计算心跳时间
                int heartbeat = ConfigUtil.getHeartbeat(message.getMinHeartbeat(), message.getMaxHeartbeat());
                // 5.响应握手成功消息
                sendOk(message, sessionId, heartbeat);
            }
        }
    }

    private boolean checkField(HandshakeMessage message) {
        if (StringUtils.isBlank(message.getDeviceId())
                || message.getClientKey().length != CipherBox.INSTANCE.getKeyLength()
                || message.getIv().length != CipherBox.INSTANCE.getKeyLength()) {
            TigerLog.CONNECT.warn("handshake failure, message={}, connect={}", message, message.getConnection());
            return false;
        }
        return true;
    }

    private boolean checkRepeatHandshake(HandshakeMessage message) {
        if (message.getDeviceId().equals(message.getConnection().getSessionContext().deviceId)) {
            TigerLog.CONNECT.warn("handshake failure, repeat handshake, message={}, connect={}", message, message.getConnection());
            return false;
        }
        return true;
    }

    private void sendOk(HandshakeMessage message, String sessionId, int heartbeat) {
        sendOk(message, null, null, null, sessionId, heartbeat);
    }

    private void sendOk(HandshakeMessage message, byte[] serverKey, byte[] sessionKey, byte[] iv,
                        String sessionId, int heartbeat) {
        SessionContext context = message.getConnection().getSessionContext();
        HandshakeOkMessage.from(message)
                .setServerKey(serverKey)
                .setHeartbeat(heartbeat)
                .setClientSessionId(sessionId)
                .setExpireTime(System.currentTimeMillis() + TigerConfig.Tiger.Core.SESSION_EXPIRED_TIME * 1000)
                .send(future -> {
                    if (future.isSuccess()) {
                        if (Objects.nonNull(sessionKey) && sessionKey.length > 0
                                && Objects.nonNull(iv) && iv.length > 0) {
                            // 7.更换会话密钥AES(clientKey)=>AES(sessionKey)
                            context.cipher = new AesCipher(sessionKey, iv);
                        }
                        // 8.保存client信息到当前连接
                        context.deviceId = message.getDeviceId();
                        context.osName = message.getOsName();
                        context.osVersion = message.getOsVersion();
                        context.clientVersion = message.getClientVersion();
                        context.heartbeat = heartbeat;
                        // 9.保存可复用session到Redis, 用于快速重连
                        TigerLog.CONNECT.info("handshake success, connect={}", message.getConnection());
                    } else {
                        TigerLog.CONNECT.info("handshake failure, connect={}", message.getConnection(), future.cause());
                    }
                });
    }
}
