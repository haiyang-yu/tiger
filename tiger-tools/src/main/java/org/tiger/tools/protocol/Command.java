package org.tiger.tools.protocol;

/**
 * {@link Command}
 *
 * @author SongQingWei
 * @since 1.0.0
 * 2020-01-07 16:41 周二
 */
public enum Command {

    // 心跳
    HEARTBEAT(1),
    // 握手
    HANDSHAKE(2),
    // 快速重连
    FAST_CONNECT(3);

    public final byte cmd;

    Command(int cmd) {
        this.cmd = (byte) cmd;
    }
}
