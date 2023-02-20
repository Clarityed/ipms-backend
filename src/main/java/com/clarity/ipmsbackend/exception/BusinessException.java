package com.clarity.ipmsbackend.exception;

import com.clarity.ipmsbackend.common.ErrorCode;

/**
 * 自定义异常类
 *
 * @author: clarity
 * @date: 2023年02月20日 11:31
 */

public class BusinessException extends RuntimeException {

    private final int code;

    public BusinessException(int code, String message) {
        super(message);
        this.code = code;
    }

    public BusinessException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.code = errorCode.getCode();
    }

    public BusinessException(ErrorCode errorCode, String message) {
        super(message);
        this.code = errorCode.getCode();
    }

    public int getCode() {
        return code;
    }

}
