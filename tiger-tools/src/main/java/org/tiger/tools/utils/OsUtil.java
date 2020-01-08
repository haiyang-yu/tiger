package org.tiger.tools.utils;

import java.util.Locale;

/**
 * {@link OsUtil}
 *
 * @author SongQingWei
 * @since 1.0.0
 * 2020-01-08 14:08 周三
 */
public class OsUtil {

    private static final String OS = System.getProperty("os.name").toLowerCase(Locale.UK).trim();

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

    private static final String ARCH = System.getProperty("sun.arch.data.model");

    public static boolean is64() {
        return "64".equals(ARCH);
    }

    public static boolean is32() {
        return "32".equals(ARCH);
    }
}
