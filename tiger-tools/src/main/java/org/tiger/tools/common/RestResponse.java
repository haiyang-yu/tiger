package org.tiger.tools.common;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;

import java.io.Serializable;

/**
 * {@link RestResponse}
 *
 * @author SongQingWei
 * @since 1.0.0
 * 2020-01-07 17:06 周二
 */
@Getter
public class RestResponse<T> implements Serializable {

    private Integer code;
    private String msg;
    private T data;
    private long timestamp = System.currentTimeMillis();

    private RestResponse(Integer code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    private RestResponse(Integer code, String msg, T data) {
        this.code = code;
        this.msg = msg;
        this.data = data;
    }

    @JsonIgnore
    public boolean isSuccess() {
        return BizEnum.REQUEST_OK.getCode().equals(code);
    }

    public static <T> RestResponse<T> createBySuccess() {
        return new RestResponse<>(BizEnum.REQUEST_OK.getCode(), BizEnum.REQUEST_OK.getMessage());
    }

    public static <T> RestResponse<T> createBySuccessMessage(String msg) {
        return new RestResponse<>(BizEnum.REQUEST_OK.getCode(), msg);
    }

    public static <T> RestResponse<T> createBySuccess(T data) {
        return new RestResponse<>(BizEnum.REQUEST_OK.getCode(), BizEnum.REQUEST_OK.getMessage(), data);
    }

    public static <T> RestResponse<T> createBySuccess(String msg, T data) {
        return new RestResponse<>(BizEnum.REQUEST_OK.getCode(), msg, data);
    }

    public static <T> RestResponse<T> createByError() {
        return new RestResponse<>(BizEnum.REQUEST_ERROR.getCode(), BizEnum.REQUEST_ERROR.getMessage());
    }

    public static <T> RestResponse<T> createByError(String msg) {
        return new RestResponse<>(BizEnum.REQUEST_ERROR.getCode(), msg);
    }

    public static <T> RestResponse<T> createByError(Integer code, String msg) {
        return new RestResponse<>(code, msg);
    }
}
