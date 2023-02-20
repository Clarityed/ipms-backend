package com.clarity.ipmsbackend.common;

/**
 * 返回工具类
 *
 * @author: clarity
 * @date: 2023年02月20日 11:06
 */

public class ResultUtils {


    /**
     * 成功返回方法
     *
     * @param data
     * @param <T>
     * @return
     */
    public static <T> BaseResponse<T> success(T data) {
        return new BaseResponse<>(0, data, "ok");
    }

    /**
     * 成功返回方法（无 message）
     *
     * @param data
     * @param <T>
     * @return
     */
    public static <T> BaseResponse<T> successWithoutMessage(T data) {
        return new BaseResponse<>(0, data);
    }

    /**
     * 失败返回方法
     *
     * @param errorCode
     * @return
     */
    public static BaseResponse error(ErrorCode errorCode) {
        return new BaseResponse(errorCode);
    }

    /**
     * 失败返回方法（与枚举类无关，错误位置当场自定义错误码和信息）
     *
     * @param code
     * @param message
     * @return
     */
    public static BaseResponse error(int code, String message) {
        return new BaseResponse(code, null, message);
    }

    /**
     * 失败返回方法
     *
     * @param errorCode
     * @param message
     * @return
     */
    public static BaseResponse error(ErrorCode errorCode, String message) {
        return new BaseResponse(errorCode.getCode(), null, message);
    }
}
