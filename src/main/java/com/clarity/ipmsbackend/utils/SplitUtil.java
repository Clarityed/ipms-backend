package com.clarity.ipmsbackend.utils;

import com.clarity.ipmsbackend.common.ErrorCode;
import com.clarity.ipmsbackend.exception.BusinessException;

/**
 * 分割工具类
 *
 * @author: clarity
 * @date: 2023年03月13日 16:56
 */

public class SplitUtil {

    /**
     * 通过减号分割单据编号
     *
     * @param billCodeStr 单据编号
     * @return 组成单据编号的各部分数组
     */
    public static String[] codeSplitByMinusSign(String billCodeStr) {
        String[] codeParts = null;
        if (billCodeStr != null) {
            codeParts = billCodeStr.split("-");
        }
        if (codeParts == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "没有按照规则设置编号或者数据库编号不存在");
        }
        return codeParts;
    }
}
