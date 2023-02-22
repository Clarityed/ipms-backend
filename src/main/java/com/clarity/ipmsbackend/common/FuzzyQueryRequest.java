package com.clarity.ipmsbackend.common;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 通用模糊查询请求封装类
 *
 * @author: clarity
 * @date: 2023年02月22日 12:16
 */

@ApiModel("通用模糊查询请求封装类")
@EqualsAndHashCode(callSuper = true)
@Data
public class FuzzyQueryRequest extends PageRequest {

    /**
     * 模糊文本，不传递该参数，就等于查全部数据
     */
    @ApiModelProperty("模糊文本，不传递该参数，就等于查全部数据")
    private String fuzzyText;
}
