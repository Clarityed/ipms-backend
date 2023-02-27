package com.clarity.ipmsbackend.model.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
 * 安全客户响应封装类
 *
 * @author: clarity
 * @date: 2023年02月26日 17:22
 */

@ApiModel("安全客户响应封装类")
@Data
public class SafeCustomerVO implements Serializable {

    /**
     * 客户 id
     */
    @ApiModelProperty("客户 id")
    private Long customerId;

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
     * 企业应收款余额
     */
    @ApiModelProperty("企业应收款余额")
    private BigDecimal enterpriseReceiveBalance;

    /**
     * 客户联系人列表
     */
    @ApiModelProperty("客户联系人列表")
    private List<SafeCustomerLinkmanVO> safeCustomerLinkmanVOList;

    private static final long serialVersionUID = 1L;
}
