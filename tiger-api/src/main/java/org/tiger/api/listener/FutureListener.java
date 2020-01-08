package org.tiger.api.listener;

import org.tiger.tools.exception.ServiceException;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * {@link FutureListener}
 *
 * @author SongQingWei
 * @since 1.0.0
 * 2020-01-08 13:29 周三
 */
public class FutureListener extends CompletableFuture<Boolean> implements Listener {

    private final Listener listener;
    private final AtomicBoolean started;

    public FutureListener(AtomicBoolean started) {
        this(null, started);
    }

    public FutureListener(Listener listener, AtomicBoolean started) {
        this.listener = listener;
        this.started = started;
    }

    @Override
    public void onSuccess(Object... args) {
        if (isDone()) {
            return;
        }
        complete(started.get());
        if (listener != null) {
            listener.onSuccess(args);
        }
    }

    @Override
    public void onFailure(Throwable cause) {
        if (isDone()) {
            return;
        }
        completeExceptionally(cause);
        if (listener != null) {
            listener.onFailure(cause);
        }
        throw cause instanceof ServiceException
                ? (ServiceException) cause
                : new ServiceException(cause);
    }

    @Override
    public boolean cancel(boolean mayInterruptIfRunning) {
        throw new UnsupportedOperationException();
    }

    /**
     * 超时监控，防止服务长时间卡在某个地方
     */
    public void monitor() {
        if (isDone()) {
            return;
        }
        runAsync(() -> {
            try {
                this.get(10000, TimeUnit.MILLISECONDS);
            } catch (Exception e) {
                this.onFailure(new ServiceException("service monitor timeout", e));
            }
        });
    }
}
