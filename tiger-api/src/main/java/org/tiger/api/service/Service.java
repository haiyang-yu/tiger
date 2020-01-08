package org.tiger.api.service;

import org.tiger.api.listener.Listener;

import java.util.concurrent.CompletableFuture;

/**
 * {@link Service}
 *
 * @author SongQingWei
 * @since 1.0.0
 * 2020-01-08 13:40 周三
 */
public interface Service {

    /**
     * 初始化服务
     */
    void init();

    /**
     * 服务是否运行中
     * @return true：运行中
     */
    boolean isRunning();

    /**
     * 启动服务
     * @param listener 结果回调
     */
    void start(Listener listener);

    /**
     * 停止服务
     * @param listener 结果回调
     */
    void stop(Listener listener);

    /**
     * 启动服务
     * @return 结果回调
     */
    CompletableFuture<Boolean> start();

    /**
     * 停止服务
     * @return 结果回调
     */
    CompletableFuture<Boolean> stop();

    /**
     * 同步启动服务
     * @return true：启动成功
     */
    boolean syncStart();

    /**
     * 同步停止服务
     * @return true：停止成功
     */
    boolean syncStop();
}
