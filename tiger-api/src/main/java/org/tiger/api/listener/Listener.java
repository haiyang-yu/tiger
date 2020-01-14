package org.tiger.api.listener;

/**
 * {@link Listener}
 *
 * @author SongQingWei
 * @since 1.0.0
 * 2020-01-14 13:43 周二
 */
public interface Listener {

    /**
     * 成功回调
     * @param args 额外参数
     */
    void onSuccess(Object... args);

    /**
     * 失败回调
     * @param cause 异常
     */
    void onFailure(Throwable cause);
}
