package com.clarity.ipmsbackend.model.dto.customer;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * 更新客户请求封装类
 *
 * @author: clarity
 * @date: 2023年02月25日 20:24
 */

@ApiModel("更新客户请求封装类")
@Data
public class UpdateCustomerRequest {

    /**
     * 客户 id
     */
    @ApiModelProperty("客户 id，作为更新的索引")
    private Long customerId;

    /**
     * 客户编号
     */
    @ApiModelProperty("客户编号，不能修改")
    private String customerCode;

    /**
     * 客户名称
     */
    @ApiModelProperty("客户名称")
    private String customerName;

    /**
     * 客户类别
     */
    @ApiModelProperty("客户类别")
    private String customerType;

    /**
     * 客户联系人列表
     */
    @ApiModelProperty("客户联系人列表")
    private List<UpdateCustomerLinkmanRequest> updateCustomerLinkmanRequestList;

    private static final long serialVersionUID = 1L;
}
