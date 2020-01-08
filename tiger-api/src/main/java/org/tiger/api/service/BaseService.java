package org.tiger.api.service;

import org.tiger.api.listener.FutureListener;
import org.tiger.api.listener.Listener;
import org.tiger.tools.exception.ServiceException;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;

/**
 * {@link BaseService}
 *
 * @author SongQingWei
 * @since 1.0.0
 * 2020-01-08 13:44 周三
 */
public abstract class BaseService implements Service {

    protected final AtomicBoolean started = new AtomicBoolean();

    @Override
    public void init() {

    }

    @Override
    public boolean isRunning() {
        return started.get();
    }

    @Override
    public void start(Listener listener) {
        tryStart(listener, this::doStart);
    }

    @Override
    public void stop(Listener listener) {
        tryStop(listener, this::doStop);
    }

    @Override
    public CompletableFuture<Boolean> start() {
        FutureListener listener = new FutureListener(started);
        start(listener);
        return listener;
    }

    @Override
    public CompletableFuture<Boolean> stop() {
        FutureListener listener = new FutureListener(started);
        stop(listener);
        return listener;
    }

    @Override
    public boolean syncStart() {
        return start().join();
    }

    @Override
    public boolean syncStop() {
        return stop().join();
    }

    protected void doStart(Listener listener) {
        listener.onSuccess();
    }

    protected void doStop(Listener listener) {
        listener.onSuccess();
    }

    /**
     * 服务启动停止，超时时间, 默认是10s
     * @return 超时时间
     */
    public int timeoutMillis() {
        return 1000 * 10;
    }

    private void tryStart(Listener listener, Consumer<Listener> consumer) {
        FutureListener futureListener = wrapper(listener);
        if (started.compareAndSet(false, true)) {
            try {
                init();
                consumer.accept(futureListener);
                futureListener.monitor(this);
            } catch (Exception e) {
                futureListener.onFailure(e);
                throw new ServiceException(e);
            }
        } else {
            futureListener.onSuccess();
        }
    }

    private void tryStop(Listener listener, Consumer<Listener> consumer) {
        FutureListener futureListener = wrapper(listener);
        if (started.compareAndSet(true, false)) {
            try {
                consumer.accept(futureListener);
                futureListener.monitor(this);
            } catch (Exception e) {
                futureListener.onFailure(e);
                throw new ServiceException(e);
            }
        } else {
            futureListener.onSuccess();
        }
    }

    private FutureListener wrapper(Listener listener) {
        if (listener == null) {
            return new FutureListener(started);
        }
        if (listener instanceof FutureListener) {
            return (FutureListener) listener;
        }
        return new FutureListener(listener, started);
    }
}
