package com.clarity.ipmsbackend.model.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 销售单据
 * @TableName ipms_sale_bill
 */
@TableName(value ="ipms_sale_bill")
@Data
public class IpmsSaleBill implements Serializable {
    /**
     * 销售单据 id
     */
    @TableId(type = IdType.AUTO)
    private Long saleBillId;

    /**
     * 销售源单据 id
     */
    private Long saleSourceBillId;

    /**
     * 销售单据编号
     */
    private String saleBillCode;

    /**
     * 销售单据日期
     */
    private String saleBillDate;

    /**
     * 客户 id（应收款余额、客户收货地址）
     */
    private Long customerId;

    /**
     * 销售结算日期
     */
    private String saleBillSettlementDate;

    /**
     * 职员 id（业务员）
     */
    private Long employeeId;

    /**
     * 部门 id
     */
    private Long departmentId;

    /**
     * 销售单据备注
     */
    private String saleBillRemark;

    /**
     * 销售币别（默认人民币）
     */
    private String saleBillCurrencyType;

    /**
     * 销售汇率（默认 1）
     */
    private BigDecimal saleBillExchangeRate;

    /**
     * 销售成交金额
     */
    private BigDecimal saleBillTransactionAmount;

    /**
     * 销售退货原因
     */
    private String saleBillReturnReason;

    /**
     * 销售单据类型（销售订单、销售出库单、销售退货单）
     */
    private String saleBillType;

    /**
     * 创建者
     */
    private String founder;

    /**
     * 修改者
     */
    private String modifier;

    /**
     * 审核人
     */
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    private String checker;

    /**
     * 审核状态（默认为 0，0 - 未审核，1 - 已审核）
     */
    private Integer checkState;

    /**
     * 执行状态（下推到销售出库单，并且销售出库单已经保存，默认为 0，0 - 未执行，1 - 部分执行，2 - 完全执行）
     */
    private Integer executionState;

    /**
     * 出库状态（默认为 0，0 - 未出库，1 - 部分出库，2 - 完全出库）
     */
    private Integer deliveryState;

    /**
     * 关闭状态（默认为 0，0 - 未关闭，1 - 已关闭）（销售出库单保存时关闭）
     */
    private Integer offState;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 修改时间
     */
    private Date updateTime;

    /**
     * 审核时间
     */
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    private Date checkTime;

    /**
     * 逻辑删除
     */
    @TableLogic
    private Integer isDelete;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}