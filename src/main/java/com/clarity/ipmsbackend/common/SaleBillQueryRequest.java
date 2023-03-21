package com.clarity.ipmsbackend.common;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 销售单据选单源查询请求
 *
 * @author: clarity
 * @date: 2023年03月19日 11:50
 */

@ApiModel("销售单据选单源查询请求")
@EqualsAndHashCode(callSuper = true)
@Data
public class SaleBillQueryRequest extends FuzzyQueryRequest {

    /**
     * 销售单据类型
     */
    @ApiModelProperty("销售单据类型，必须传递")
    private String saleBillType;

    /**
     * 销售单据的客户 id
     */
    @ApiModelProperty("销售单据的客户 id，传递的话作为选单源功能，不传递的话就是查询对应单据的列表")
    private Long customerId;
}
