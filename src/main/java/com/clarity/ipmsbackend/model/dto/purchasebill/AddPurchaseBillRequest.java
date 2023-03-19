package com.clarity.ipmsbackend.model.dto.purchasebill;

import com.clarity.ipmsbackend.model.dto.purchasebill.productnum.AddProductNumRequest;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
 * 增加采购单据请求封装类
 *
 * @author: clarity
 * @date: 2023年03月13日 17:48
 */

@ApiModel("增加采购单据请求封装类")
@Data
public class AddPurchaseBillRequest implements Serializable {

    /**
     * 采购源单 id
     */
    @ApiModelProperty("采购源单 id，使用选单源功能时必定携带")
    private Long purchaseSourceBillId;

    /**
     * 采购单据编号
     */
    @ApiModelProperty("采购单据编号，不能为空，且不重复")
    private String purchaseBillCode;

    /**
     * 采购单据日期
     */
    @ApiModelProperty("采购单据日期，不能为空")
    private String purchaseBillDate;

    /**
     * 供应商 id（应付款余额、供应商发货地址）
     */
    @ApiModelProperty("供应商 id（应付款余额、供应商发货地址），不能为空")
    private Long supplierId;

    /**
     * 采购结算日期
     */
    @ApiModelProperty("采购结算日期，不能为空")
    private String purchaseBillSettlementDate;

    /**
     * 职员 id（业务员）
     */
    @ApiModelProperty("职员 id（业务员）")
    private Long employeeId;

    /**
     * 部门 id
     */
    @ApiModelProperty("部门 id")
    private Long departmentId;

    /**
     * 采购单据备注
     */
    @ApiModelProperty("采购单据备注")
    private String purchaseBillRemark;

    /**
     * 采购币别（默认人民币）
     */
    @ApiModelProperty("采购币别（默认人民币），数据库默认已经设置，如有需要可以修改")
    private String purchaseBillCurrencyType;

    /**
     * 采购汇率（默认 1）
     */
    @ApiModelProperty("采购汇率（默认 1），数据库默认已经设置，如有需要可以修改")
    private BigDecimal purchaseBillExchangeRate;

    /**
     * 采购成交金额
     */
    @ApiModelProperty("采购成交金额，不能为空")
    private BigDecimal purchaseBillTransactionAmount;

    /**
     * 采购退货原因
     */
    @ApiModelProperty("采购退货原因")
    private String purchaseBillReturnReason;

    /**
     * 采购单据类型（采购订单、采购入库单、采购退货单）
     */
    @ApiModelProperty("采购单据类型（采购订单、采购入库单、采购退货单），不能为空，且必须符合规范输入")
    private String purchaseBillType;

    /**
     * 增加采购单据的商品及商品数量
     */
    @ApiModelProperty("增加采购单据的商品及商品数量列表")
    private List<AddProductNumRequest> addProductNumRequestList;

    private static final long serialVersionUID = 1L;
}
