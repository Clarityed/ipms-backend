package com.clarity.ipmsbackend.model.dto.customer;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 增加客户请求封装类
 *
 * @author: clarity
 * @date: 2023年02月25日 20:24
 */

@ApiModel("增加客户请求封装类")
@Data
public class AddCustomerRequest implements Serializable {

    /**
     * 客户编号
     */
    @ApiModelProperty("客户编号")
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
     * 增加客户联系人请求列表
     */
    @ApiModelProperty("增加客户联系人请求列表")
    private List<AddCustomerLinkmanRequest> AddCustomerLinkmanRequestList;

    private static final long serialVersionUID = 1L;
}
