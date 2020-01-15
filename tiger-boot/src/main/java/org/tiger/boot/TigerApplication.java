package org.tiger.boot;

import org.tiger.common.log.TigerLog;

/**
 * {@link TigerApplication}
 *
 * @author SongQingWei
 * @since 1.0.0
 * 2020-01-15 11:06 周三
 */
public class TigerApplication {

    public static void main(String[] args) {
        TigerLog.init();
        ServerLauncher launcher = new ServerLauncher();
        launcher.init();
        launcher.start();
        addHook(launcher);
    }

    private static void addHook(ServerLauncher launcher) {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try {
                launcher.stop();
            } catch (Exception e) {
                TigerLog.CONSOLE.error("tiger stop occur exception", e);
            }
            TigerLog.CONSOLE.info("jvm exit, all service stopped.");
        }, "tiger-shutdown-hook-thread"));
    }
}
