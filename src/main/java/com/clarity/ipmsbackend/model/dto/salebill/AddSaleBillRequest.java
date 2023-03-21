package com.clarity.ipmsbackend.model.dto.salebill;

import com.clarity.ipmsbackend.model.dto.salebill.productnum.AddSaleProductNumRequest;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
 * 增加销售单据请求封装类
 *
 * @author: clarity
 * @date: 2023年03月13日 17:48
 */

@ApiModel("增加销售单据请求封装类")
@Data
public class AddSaleBillRequest implements Serializable {

    /**
     * 销售源单 id
     */
    @ApiModelProperty("销售源单 id，使用选单源功能时必定携带")
    private Long saleSourceBillId;

    /**
     * 销售单据编号
     */
    @ApiModelProperty("销售单据编号，不能为空，且不重复")
    private String saleBillCode;

    /**
     * 销售单据日期
     */
    @ApiModelProperty("销售单据日期，不能为空")
    private String saleBillDate;

    /**
     * 客户 id（应收款余额、客户收货地址）
     */
    @ApiModelProperty("客户 id（应收款余额、客户收货地址）")
    private Long customerId;

    /**
     * 销售结算日期
     */
    @ApiModelProperty("销售结算日期，不能为空")
    private String saleBillSettlementDate;

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
     * 销售单据备注
     */
    @ApiModelProperty("销售单据备注")
    private String saleBillRemark;

    /**
     * 销售币别（默认人民币）
     */
    @ApiModelProperty("销售币别（默认人民币），数据库默认已经设置，如有需要可以修改")
    private String saleBillCurrencyType;

    /**
     * 销售汇率（默认 1）
     */
    @ApiModelProperty("销售汇率（默认 1），数据库默认已经设置，如有需要可以修改")
    private BigDecimal saleBillExchangeRate;

    /**
     * 销售成交金额
     */
    @ApiModelProperty("销售成交金额，不能为空")
    private BigDecimal saleBillTransactionAmount;

    /**
     * 销售退货原因
     */
    @ApiModelProperty("销售退货原因")
    private String saleBillReturnReason;

    /**
     * 销售单据类型（销售订单、销售出库单、销售退货单）
     */
    @ApiModelProperty("销售单据类型（销售订单、销售出库单、销售退货单），不能为空，且必须符合规范输入")
    private String saleBillType;

    /**
     * 增加销售单据的商品及商品数量
     */
    @ApiModelProperty("增加销售单据的商品及商品数量列表")
    private List<AddSaleProductNumRequest> addSaleProductNumRequestList;

    private static final long serialVersionUID = 1L;
}
