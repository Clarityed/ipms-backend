package com.clarity.ipmsbackend.common;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 生产单据选单源查询请求
 *
 * @author: clarity
 * @date: 2023年03月23日 15:09
 */

@ApiModel("销售单据选单源查询请求")
@EqualsAndHashCode(callSuper = true)
@Data
public class ProductionBillQueryRequest extends FuzzyQueryRequest {

    /**
     * 生产单据类型
     */
    @ApiModelProperty("生产单据类型，必须传递")
    private String productionBillType;
}
