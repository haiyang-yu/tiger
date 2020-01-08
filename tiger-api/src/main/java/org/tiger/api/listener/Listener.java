package org.tiger.api.listener;

/**
 * {@link Listener}
 *
 * @author SongQingWei
 * @since 1.0.0
 * 2020-01-08 13:28 周三
 */
public interface Listener {

    /**
     * 成功时回调
     * @param args 额外参数
     */
    void onSuccess(Object... args);

    /**
     * 失败时回调
     * @param cause 异常
     */
    void onFailure(Throwable cause);
}
