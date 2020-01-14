package org.tiger.api.service;

import org.tiger.api.listener.Listener;

import java.util.concurrent.CompletableFuture;

/**
 * {@link Service}
 *
 * @author SongQingWei
 * @since 1.0.0
 * 2020-01-14 13:29 周二
 */
public interface Service {

    /**
     * 初始化服务
     */
    void init();

    /**
     * 服务是否已启动
     * @return true：已启动
     */
    boolean isRunning();

    /**
     * 同步启动服务
     */
    void syncStart();

    /**
     * 异步启动任务
     * @return 任务启动结果
     */
    CompletableFuture<Boolean> start();

    /**
     * 异步启动
     * @param listener 任务启动结果
     */
    void start(Listener listener);

    /**
     * 同步停止服务
     */
    void syncStop();

    /**
     * 异步停止任务
     * @return 任务停止结果
     */
    CompletableFuture<Boolean> stop();

    /**
     * 异步停止
     * @param listener 任务停止结果
     */
    void stop(Listener listener);
}
