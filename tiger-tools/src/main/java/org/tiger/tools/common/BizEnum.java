package org.tiger.tools.common;

import lombok.Getter;

/**
 * {@link BizEnum}
 *
 * @author SongQingWei
 * @since 1.0.0
 * 2020-01-07 16:53 周二
 */
@Getter
public enum BizEnum {

    /**
     * 请求成功
     */
    REQUEST_OK(200, "请求成功"),

    /**
     * 请求异常
     */
    REQUEST_ERROR(500, "请求失败"),
    SERVER_ERROR(50001, "服务器异常"),
    SYSTEM_NO_AUTH(50002, "非法请求"),

    /**
     * 签名异常
     */
    SIGN_EXPIRED(60001, "请求超时"),
    SIGN_ERROR(60002, "签名验证失败"),

    /**
     * 通用异常
     */
    INVALID_KEY(70001, "秘钥无效"),
    INVALID_USER(70002, "用户无效"),
    INVALID_PARAMETER(70003,"参数无效"),
    LOAD_PRIVATE_KEY_ERROR(70004, "加载私钥失败"),
    LOAD_PUBLIC_KEY_ERROR(70005, "加载公钥失败"),
    ;

    private Integer code;
    private String message;

    BizEnum(Integer code, String message) {
        this.code = code;
        this.message = message;
    }
}
