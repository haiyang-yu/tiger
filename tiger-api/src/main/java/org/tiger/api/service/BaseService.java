package org.tiger.api.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tiger.api.listener.FutureListener;
import org.tiger.api.listener.Listener;

import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * {@link BaseService}
 *
 * @author SongQingWei
 * @since 1.0.0
 * 2020-01-14 13:37 周二
 */
public abstract class BaseService implements Service {

    private static final Logger LOGGER = LoggerFactory.getLogger(BaseService.class);

    private final AtomicBoolean state = new AtomicBoolean();

    @Override
    public void init() {

    }

    @Override
    public boolean isRunning() {
        return state.get();
    }

    @Override
    public void syncStart() {
        start().join();
    }

    @Override
    public CompletableFuture<Boolean> start() {
        FutureListener listener = new FutureListener(state);
        start(listener);
        return listener;
    }

    @Override
    public void start(Listener listener) {
        tryStart(listener, this::doStart);
    }

    @Override
    public void syncStop() {
        stop().join();
    }

    @Override
    public CompletableFuture<Boolean> stop() {
        FutureListener listener = new FutureListener(state);
        stop(listener);
        return listener;
    }

    @Override
    public void stop(Listener listener) {
        tryStop(listener, this::doStop);
    }

    private void tryStart(Listener listener, Function function) {
        FutureListener futureListener = wrapper(listener);
        if (state.compareAndSet(false, true)) {
            try {
                init();
                function.apply(futureListener);
                futureListener.monitor(this);
            } catch (Exception e) {
                futureListener.onFailure(e);
                throw new ServiceException(e);
            }
        } else {
            LOGGER.warn("service already started.");
            futureListener.onSuccess();
        }
    }

    private void tryStop(Listener listener, Function function) {
        FutureListener futureListener = wrapper(listener);
        if (state.compareAndSet(true, false)) {
            try {
                function.apply(futureListener);
                futureListener.monitor(this);
            } catch (Exception e) {
                futureListener.onFailure(e);
                throw new ServiceException(e);
            }
        } else {
            LOGGER.warn("service already stopped.");
            futureListener.onSuccess();
        }
    }

    protected void doStart(Listener listener) throws Exception {
        listener.onSuccess();
    }

    protected void doStop(Listener listener) throws Exception {
        listener.onSuccess();
    }

    /**
     * 服务启动停止，超时时间, 默认是10s
     * @return 超时时间
     */
    public int timeoutMillis() {
        return 1000 * 10;
    }

    private FutureListener wrapper(Listener listener) {
        if (Objects.isNull(listener)) {
            return new FutureListener(state);
        }
        if (listener instanceof FutureListener) {
            return (FutureListener) listener;
        }
        return new FutureListener(listener, state);
    }

    @FunctionalInterface
    private interface Function {
        /**
         * 执行
         * @param listener Listener
         * @throws Exception e
         */
        void apply(Listener listener) throws Exception;
    }
}
