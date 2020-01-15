package org.tiger.boot.job;

import org.tiger.common.log.TigerLog;

import java.util.function.Supplier;

/**
 * {@link BootChain}
 *
 * @author SongQingWei
 * @since 1.0.0
 * 2020-01-14 18:18 周二
 */
public class BootChain {

    private final BaseBootJob bootJob = new BaseBootJob() {
        @Override
        protected void start() {
            TigerLog.CONSOLE.info("bootstrap chain starting...");
            startNext();
        }

        @Override
        protected void stop() {
            stopNext();
            TigerLog.CONSOLE.info("bootstrap chain stopped.");
            TigerLog.CONSOLE.info("===================================================================");
            TigerLog.CONSOLE.info("====================tiger SERVER STOPPED SUCCESS===================");
            TigerLog.CONSOLE.info("===================================================================");
        }
    };

    private BaseBootJob last = bootJob;

    private BootChain() {}

    public void start() {
        bootJob.start();
    }

    public void stop() {
        bootJob.stop();
    }

    public void end() {
        setNext(new BaseBootJob() {
            @Override
            protected void start() {
                TigerLog.CONSOLE.info("bootstrap chain started.");
                TigerLog.CONSOLE.info("===================================================================");
                TigerLog.CONSOLE.info("====================TIGER SERVER START SUCCESS=====================");
                TigerLog.CONSOLE.info("===================================================================");
            }

            @Override
            protected void stop() {
                TigerLog.CONSOLE.info("bootstrap chain stopping...");
            }

            @Override
            protected String getName() {
                return "LastBoot";
            }
        });
    }

    public BootChain setNext(BaseBootJob bootJob) {
        this.last = last.setNext(bootJob);
        return this;
    }

    /**
     * 添加服务
     * @param supplier {@link Supplier}
     * @param enabled 是否启动服务
     * @return {@link BootChain}
     */
    public BootChain setNext(Supplier<BaseBootJob> supplier, boolean enabled) {
        if (enabled) {
            setNext(supplier.get());
        }
        return this;
    }

    public static BootChain chain() {
        return new BootChain();
    }
}
