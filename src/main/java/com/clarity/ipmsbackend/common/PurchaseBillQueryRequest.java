package com.clarity.ipmsbackend.common;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 采购单据选单源查询请求
 *
 * @author: clarity
 * @date: 2023年03月19日 11:50
 */

@ApiModel("采购单据选单源查询请求")
@EqualsAndHashCode(callSuper = true)
@Data
public class PurchaseBillQueryRequest extends FuzzyQueryRequest {

    /**
     * 采购单据类型
     */
    @ApiModelProperty("采购单据类型，必须传递")
    private String purchaseBillType;

    /**
     * 采购单据的供应商 id
     */
    @ApiModelProperty("采购单据的供应商 id，传递的话作为选单源功能，不传递的话就是查询对应单据的列表")
    private Long supplierId;
}
