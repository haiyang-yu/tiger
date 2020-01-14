package org.tiger.api.listener;

import org.tiger.api.service.BaseService;
import org.tiger.api.service.ServiceException;

import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * {@link FutureListener}
 *
 * @author SongQingWei
 * @since 1.0.0
 * 2020-01-14 13:45 周二
 */
public class FutureListener extends CompletableFuture<Boolean> implements Listener {

    private final Listener listener;
    private final AtomicBoolean state;

    public FutureListener(AtomicBoolean state) {
        this(null, state);
    }

    public FutureListener(Listener listener, AtomicBoolean state) {
        this.listener = listener;
        this.state = state;
    }

    @Override
    public void onSuccess(Object... args) {
        if (isDone()) {
            return;
        }
        complete(state.get());
        if (Objects.nonNull(listener)) {
            listener.onSuccess(args);
        }
    }

    @Override
    public void onFailure(Throwable cause) {
        if (isDone()) {
            return;
        }
        completeExceptionally(cause);
        if (Objects.nonNull(listener)) {
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

    public void monitor(BaseService service) {
        if (isDone()) {
            return;
        }
        runAsync(() -> {
            try {
                get(service.timeoutMillis(), TimeUnit.MILLISECONDS);
            } catch (Exception e) {
                if (Objects.nonNull(listener)) {
                    listener.onFailure(new ServiceException(String.format("service %s monitor timeout", service.getClass().getSimpleName())));
                }
            }
        });
    }
}
