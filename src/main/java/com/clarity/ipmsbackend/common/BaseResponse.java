package com.clarity.ipmsbackend.common;

import lombok.Data;

import java.io.Serializable;

/**
 * 通用返回类
 *
 * @author: clarity
 * @date: 2023年02月20日 10:51
 */

@Data
public class BaseResponse<T> implements Serializable {

    private int code;

    private T date;

    private String message;

    public BaseResponse(int code, T date, String message) {
        this.code = code;
        this.date = date;
        this.message = message;
    }

    public BaseResponse(int code, T date) {
        this(code, date, "");
    }

    public BaseResponse(ErrorCode errorCode) {
        this(errorCode.getCode(), null, errorCode.getMessage());
    }
}
