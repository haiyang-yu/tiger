package org.tiger.tools.common;

/**
 * {@link ThreadName}
 *
 * @author SongQingWei
 * @since 1.0.0
 * 2020-01-08 11:11 周三
 */
public class ThreadName {

    public static final String BASIC = "tiger";

    public static final String T_BOSS = BASIC + "-boss";
    public static final String T_WORKER = BASIC + "-work";
    public static final String T_CONN_BOSS = BASIC + "-conn-boss";
    public static final String T_CONN_WORKER = BASIC + "-conn-work";
    public static final String T_CONN_TIMER = BASIC + "-conn-check-timer";
    public static final String T_TRAFFIC_SHAPING = BASIC + "-traffic-shaping";
}
