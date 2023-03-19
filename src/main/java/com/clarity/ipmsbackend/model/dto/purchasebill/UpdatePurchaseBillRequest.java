package com.clarity.ipmsbackend.model.dto.purchasebill;

import com.clarity.ipmsbackend.model.dto.purchasebill.productnum.UpdateProductNumRequest;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
 * 修改采购单据请求封装类
 *
 * @author: clarity
 * @date: 2023年03月17日 10:11
 */

@ApiModel("修改采购单据请求封装类")
@Data
public class UpdatePurchaseBillRequest implements Serializable {

    /**
     * 采购单据 id
     */
    @ApiModelProperty("采购单据 id，作为更新单据的索引")
    private Long purchaseBillId;

    /**
     * 采购单据编号
     */
    @ApiModelProperty("采购单据编号，可以传递，也可以不传递，传递的话必须与原来的一致，不支持修改")
    private String purchaseBillCode;

    /**
     * 采购单据日期
     */
    @ApiModelProperty("采购单据日期，可以修改")
    private String purchaseBillDate;

    /**
     * 供应商 id（应付款余额、供应商发货地址）
     */
    @ApiModelProperty("供应商 id，可以传递，也可以不传递，传递的话必须与原来的一致")
    private Long supplierId;

    /**
     * 采购结算日期
     */
    @ApiModelProperty("采购结算日期，可以修改")
    private String purchaseBillSettlementDate;

    /**
     * 职员 id（业务员）
     */
    @ApiModelProperty("职员 id（业务员），可以修改")
    private Long employeeId;

    /**
     * 部门 id
     */
    @ApiModelProperty("职员 id（业务员），可以修改")
    private Long departmentId;

    /**
     * 采购单据备注
     */
    @ApiModelProperty("采购单据备注，可以修改")
    private String purchaseBillRemark;

    /**
     * 采购币别（默认人民币）
     */
    @ApiModelProperty("采购币别（默认人民币），可以修改")
    private String purchaseBillCurrencyType;

    /**
     * 采购汇率（默认 1）
     */
    @ApiModelProperty("采购汇率（默认 1），可以修改")
    private BigDecimal purchaseBillExchangeRate;

    /**
     * 采购成交金额
     */
    @ApiModelProperty("采购成交金额，前后端重复计算，保证准确性")
    private BigDecimal purchaseBillTransactionAmount;

    /**
     * 采购退货原因
     */
    @ApiModelProperty("采购退货原因，采购退货单时可以修改")
    private String purchaseBillReturnReason;

    /**
     * 采购单据类型（采购订单、采购入库单、采购退货单）
     */
    @ApiModelProperty("采购单据类型（采购订单、采购入库单、采购退货单），不能修改")
    private String purchaseBillType;

    /**
     * 修改采购单据的商品及商品数量
     */
    @ApiModelProperty("修改采购单据的商品及商品数量必传")
    private List<UpdateProductNumRequest> updateProductNumRequestList;

    private static final long serialVersionUID = 1L;
}
