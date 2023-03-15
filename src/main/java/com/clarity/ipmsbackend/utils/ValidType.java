package com.clarity.ipmsbackend.utils;

import java.util.List;

/**
 * 校验类型工具类
 *
 * @author: clarity
 * @date: 2023年03月14日 10:35
 */

public class ValidType {

    /**
     * 单据类型校验
     *
     * @param billTypeList 单据类型字符串数组
     * @return 0 - 错误，1 - 正确
     */
    public static int valid(List<String> billTypeList, String billType) {
        int result = 0;
        for (String type : billTypeList) {
            if (type.equals(billType)) {
                result++;
                break;
            }
        }
        return result;
    }
}
