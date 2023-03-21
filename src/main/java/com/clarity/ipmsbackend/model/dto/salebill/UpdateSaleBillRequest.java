package com.clarity.ipmsbackend.model.dto.salebill;

import com.clarity.ipmsbackend.model.dto.salebill.productnum.UpdateSaleProductNumRequest;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
 * 修改销售单据请求封装类
 *
 * @author: clarity
 * @date: 2023年03月17日 10:11
 */

@ApiModel("修改销售单据请求封装类")
@Data
public class UpdateSaleBillRequest implements Serializable {

    /**
     * 销售单据 id
     */
    @ApiModelProperty("销售单据 id，作为更新单据的索引")
    private Long saleBillId;

    /**
     * 销售单据编号
     */
    @ApiModelProperty("销售单据编号，可以传递，也可以不传递，传递的话必须与原来的一致，不支持修改")
    private String saleBillCode;

    /**
     * 销售单据日期
     */
    @ApiModelProperty("销售单据日期，可以修改")
    private String saleBillDate;

    /**
     * 供应商 id（应付款余额、供应商发货地址）
     */
    @ApiModelProperty("供应商 id，可以传递，也可以不传递，传递的话必须与原来的一致")
    private Long supplierId;

    /**
     * 销售结算日期
     */
    @ApiModelProperty("销售结算日期，可以修改")
    private String saleBillSettlementDate;

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
     * 销售单据备注
     */
    @ApiModelProperty("销售单据备注，可以修改")
    private String saleBillRemark;

    /**
     * 销售币别（默认人民币）
     */
    @ApiModelProperty("销售币别（默认人民币），可以修改")
    private String saleBillCurrencyType;

    /**
     * 销售汇率（默认 1）
     */
    @ApiModelProperty("销售汇率（默认 1），可以修改")
    private BigDecimal saleBillExchangeRate;

    /**
     * 销售成交金额
     */
    @ApiModelProperty("销售成交金额，前后端重复计算，保证准确性")
    private BigDecimal saleBillTransactionAmount;

    /**
     * 销售退货原因
     */
    @ApiModelProperty("销售退货原因，销售退货单时可以修改")
    private String saleBillReturnReason;

    /**
     * 销售单据类型（销售订单、销售入库单、销售退货单）
     */
    @ApiModelProperty("销售单据类型（销售订单、销售入库单、销售退货单），不能修改")
    private String saleBillType;

    /**
     * 修改销售单据的商品及商品数量
     */
    @ApiModelProperty("修改销售单据的商品及商品数量必传")
    private List<UpdateSaleProductNumRequest> updateSaleProductNumRequestList;

    private static final long serialVersionUID = 1L;
}
