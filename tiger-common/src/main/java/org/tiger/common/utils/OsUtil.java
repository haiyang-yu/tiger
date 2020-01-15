package org.tiger.common.utils;

import org.tiger.common.config.TigerConfig;

import java.util.Locale;

/**
 * {@link OsUtil}
 *
 * @author SongQingWei
 * @since 1.0.0
 * 2020-01-14 10:52 周二
 */
public final class OsUtil {

    private static final String OS = TigerConfig.CONFIG.getString("os.name").toLowerCase(Locale.UK).trim();
    private static final String ARCH = TigerConfig.CONFIG.getString("sun.arch.data.model");

    public static boolean isWindows() {
        return OS.contains("win");
    }

    public static boolean isWindowsXp() {
        return OS.contains("win") && OS.contains("xp");
    }

    public static boolean isMac() {
        return OS.contains("mac");
    }

    public static boolean isUnix() {
        return OS.contains("nix") || OS.contains("nux") || OS.contains("aix");
    }

    public static boolean isSolaris() {
        return (OS.contains("sunos"));
    }

    public static boolean is64() {
        return "64".equals(ARCH);
    }

    public static boolean is32() {
        return "32".equals(ARCH);
    }
}
