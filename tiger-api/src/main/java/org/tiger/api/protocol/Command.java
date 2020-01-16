package org.tiger.api.protocol;

/**
 * {@link Command}
 *
 * @author SongQingWei
 * @since 1.0.0
 * 2020-01-14 15:59 周二
 */
public enum Command {

    // 心跳
    HEARTBEAT(1),
    // 握手
    HANDSHAKE(2),
    // 快速重连
    FAST_CONNECT(3),
    UNKNOWN(-1);

    public final byte cmd;

    Command(int cmd) {
        this.cmd = (byte) cmd;
    }

    public static Command getCommand(byte cmd) {
        Command[] commands = values();
        if (cmd > 0 && cmd < commands.length) {
            return commands[cmd - 1];
        }
        return UNKNOWN;
    }
}
