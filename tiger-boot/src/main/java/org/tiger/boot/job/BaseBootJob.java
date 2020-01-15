package org.tiger.boot.job;

import org.tiger.common.log.TigerLog;

/**
 * {@link BaseBootJob}
 *
 * @author SongQingWei
 * @since 1.0.0
 * 2020-01-14 17:52 周二
 */
public abstract class BaseBootJob {

    private BaseBootJob next;

    /**
     * 启动服务
     */
    protected abstract void start();

    /**
     * 停止服务
     */
    protected abstract void stop();

    public void startNext() {
        if (next != null) {
            TigerLog.CONSOLE.info("start bootstrap job [{}]", getNextName());
            next.start();
        }
    }

    public void stopNext() {
        if (next != null) {
            next.stop();
            TigerLog.CONSOLE.info("stopped bootstrap job [{}]", getNextName());
        }
    }

    public BaseBootJob setNext(BaseBootJob next) {
        this.next = next;
        return next;
    }

    protected String getNextName() {
        return next.getName();
    }

    protected String getName() {
        return this.getClass().getSimpleName();
    }
}
