package com.clarity.ipmsbackend.model.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
 * 安全供应商响应封装类
 *
 * @author: clarity
 * @date: 2023年02月27日 10:35
 */

@ApiModel("安全供应商响应封装类")
@Data
public class SafeSupplierVO implements Serializable {

    /**
     * 供应商 id
     */
    @ApiModelProperty("供应商 id")
    private Long supplierId;

    /**
     * 供应商编号
     */
    @ApiModelProperty("供应商编号")
    private String supplierCode;

    /**
     * 供应商名称
     */
    @ApiModelProperty("供应商名称")
    private String supplierName;

    /**
     * 供应商类别
     */
    @ApiModelProperty("供应商类别")
    private String supplierType;

    /**
     * 企业应付款余额
     */
    @ApiModelProperty("企业应付款余额")
    private BigDecimal enterprisePayBalance;

    /**
     * 供应商联系人列表
     */
    @ApiModelProperty("供应商联系人列表")
    private List<SafeSupplierLinkmanVO> safeSupplierLinkmanVOList;

    private static final long serialVersionUID = 1L;
}
