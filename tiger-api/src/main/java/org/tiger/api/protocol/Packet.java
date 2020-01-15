package org.tiger.api.protocol;

/**
 * {@link Packet}
 * 自定义协议
 * +——----——+——----——+——----——+——----——+——----——+
 * |  长度  | 命令码  |  特性  | 会话ID  |  数据  |
 * +——----——+——----——+——----——+——----——+——----——+
 * 1. 要传输数据的长度 bodyLength，int类型，占据4个字节
 * 2. 通讯命令码，byte类型，占据1个字节
 * 3. 特性（是否压缩、加密），byte类型，占据1个字节
 * 4. 会话ID，客户端生成，int类型，占据4个字节
 * 5. 要传输的数据，长度不得超过配置的长度，防止socket流攻击
 * @author SongQingWei
 * @since 1.0.0
 * 2020-01-14 15:59 周二
 */
public class Packet {

    /**
     * 头信息长度
     * bodyLength(4) + cmd(1) + flags(1) + sessionId(4)
     */
    public static final int HEADER_LEN = 10;

    /**
     * 加密标记
     */
    public static final byte FLAG_CRYPTO = 1;

    /**
     * 压缩标记
     */
    public static final byte FLAG_COMPRESS = 2;

    /**
     * 心跳
     */
    public static final byte HEARTBEAT_PACKET_BYTE = -33;

    /**
     * 心跳包
     */
    public static final Packet HEARTBEAT_PACKET = new Packet(Command.HEARTBEAT);

    public byte cmd;
    public byte flags;
    public int sessionId;
    public transient byte[] body;

    public Packet(byte cmd) {
        this.cmd = cmd;
    }

    public Packet(byte cmd, int sessionId) {
        this.cmd = cmd;
        this.sessionId = sessionId;
    }

    public Packet(Command command) {
        this.cmd = command.cmd;
    }

    public Packet(Command command, int sessionId) {
        this.cmd = command.cmd;
        this.sessionId = sessionId;
    }

    public int getBodyLength() {
        return body == null ? 0 : body.length;
    }

    public void addFlag(byte flag) {
        this.flags |= flag;
    }

    public boolean hasFlag(byte flag) {
        return (flags & flag) != 0;
    }
}
