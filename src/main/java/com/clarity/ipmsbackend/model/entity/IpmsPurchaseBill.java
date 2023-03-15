package com.clarity.ipmsbackend.model.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 采购单据
 * @TableName ipms_purchase_bill
 */
@TableName(value ="ipms_purchase_bill")
@Data
public class IpmsPurchaseBill implements Serializable {
    /**
     * 采购单据 id
     */
    @TableId(type = IdType.AUTO)
    private Long purchaseBillId;

    /**
     * 采购源单 id
     */
    private Long purchaseSourceBillId;

    /**
     * 采购单据编号
     */
    private String purchaseBillCode;

    /**
     * 采购单据日期
     */
    private String purchaseBillDate;

    /**
     * 供应商 id（应付款余额、供应商发货地址）
     */
    private Long supplierId;

    /**
     * 采购结算日期
     */
    private String purchaseBillSettlementDate;

    /**
     * 职员 id（业务员）
     */
    private Long employeeId;

    /**
     * 部门 id
     */
    private Long departmentId;

    /**
     * 采购单据备注
     */
    private String purchaseBillRemark;

    /**
     * 采购币别（默认人民币）
     */
    private String purchaseBillCurrencyType;

    /**
     * 采购汇率（默认 1）
     */
    private BigDecimal purchaseBillExchangeRate;

    /**
     * 采购成交金额
     */
    private BigDecimal purchaseBillTransactionAmount;

    /**
     * 采购退货原因
     */
    private String purchaseBillReturnReason;

    /**
     * 采购单据类型（采购订单、采购入库单、采购退货单）
     */
    private String purchaseBillType;

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
    private String checker;

    /**
     * 审核状态（默认为 0，0 - 未审核，1 - 已审核）
     */
    private Integer checkState;

    /**
     * 执行状态（默认为 0，0 - 未执行，1 - 部分执行，2 - 完全执行）
     */
    private Integer executionState;

    /**
     * 入库状态（默认为 0，0 - 未入库，1 - 部分入库，2 - 完全入库）
     */
    private Integer warehousingState;

    /**
     * 关闭状态（默认为 0，0 - 未关闭，1 - 已关闭）
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
    private Date checkTime;

    /**
     * 逻辑删除
     */
    @TableLogic
    private Integer isDelete;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}