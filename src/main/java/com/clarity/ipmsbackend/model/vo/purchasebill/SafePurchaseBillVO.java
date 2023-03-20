package com.clarity.ipmsbackend.model.vo.purchasebill;

import com.clarity.ipmsbackend.model.vo.SafeSupplierVO;
import com.clarity.ipmsbackend.model.vo.purchasebill.productnum.SafePurchaseBillProductNumVO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
 * 安全采购单据响应封装类
 *
 * @author: clarity
 * @date: 2023年03月19日 10:20
 */

@ApiModel("安全采购单据响应封装类")
@Data
public class SafePurchaseBillVO implements Serializable {

    /**
     * 采购单据 id
     */
    @ApiModelProperty("采购单据 id，使用选单源功能时这个是作为单源的 id")
    private Long purchaseBillId;

    /**
     * 采购源单 id
     */
    @ApiModelProperty("采购源单 id，这个单源，指的是该条单据记录的源单是谁？")
    private Long purchaseSourceBillId;

    /**
     * 采购源单类型
     */
    @ApiModelProperty("采购源单类型")
    private String purchaseSourceBillType;

    /**
     * 采购源单编号
     */
    @ApiModelProperty("采购源单类型")
    private String purchaseSourceBillCode;

    /**
     * 采购单据编号
     */
    @ApiModelProperty("采购单据编号")
    private String purchaseBillCode;

    /**
     * 采购单据日期
     */
    @ApiModelProperty("采购单据日期")
    private String purchaseBillDate;

    /**
     * 采购结算日期
     */
    @ApiModelProperty("采购结算日期")
    private String purchaseBillSettlementDate;

    /**
     * 供应商
     */
    @ApiModelProperty("供应商")
    private SafeSupplierVO safeSupplierVO;

    /**
     * 职员（业务员） id
     */
    @ApiModelProperty("职员（业务员） id")
    private Long employeeId;

    /**
     * 职员（业务员） 姓名
     */
    @ApiModelProperty("职员（业务员） 姓名")
    private String employeeName;

    /**
     * 部门 id
     */
    @ApiModelProperty("部门 id")
    private Long departmentId;

    /**
     * 部门姓名
     */
    @ApiModelProperty("部门姓名")
    private String departmentName;

    /**
     * 采购单据备注
     */
    @ApiModelProperty("采购单据备注")
    private String purchaseBillRemark;

    /**
     * 采购币别（默认人民币）
     */
    @ApiModelProperty("采购币别（默认人民币）")
    private String purchaseBillCurrencyType;

    /**
     * 采购汇率（默认 1）
     */
    @ApiModelProperty("采购汇率（默认 1）")
    private BigDecimal purchaseBillExchangeRate;

    /**
     * 采购成交金额
     */
    @ApiModelProperty("采购成交金额")
    private BigDecimal purchaseBillTransactionAmount;

    /**
     * 采购退货原因
     */
    @ApiModelProperty("采购退货原因，采购退货单展示该字段")
    private String purchaseBillReturnReason;

    /**
     * 采购单据类型（采购订单、采购入库单、采购退货单）
     */
    @ApiModelProperty("采购单据类型（采购订单、采购入库单、采购退货单），按需展示")
    private String purchaseBillType;

    /**
     * 创建者
     */
    @ApiModelProperty("创建者")
    private String founder;

    /**
     * 修改者
     */
    @ApiModelProperty("修改者")
    private String modifier;

    /**
     * 审核人
     */
    @ApiModelProperty("审核人")
    private String checker;

    /**
     * 审核状态（默认为 0，0 - 未审核，1 - 已审核）
     */
    @ApiModelProperty("审核状态（默认为 0，0 - 未审核，1 - 已审核）")
    private Integer checkState;

    /**
     * 执行状态（默认为 0，0 - 未执行，1 - 部分执行，2 - 完全执行）
     */
    @ApiModelProperty("执行状态（默认为 0，0 - 未执行，1 - 部分执行，2 - 完全执行）")
    private Integer executionState;

    /**
     * 入库状态（默认为 0，0 - 未入库，1 - 部分入库，2 - 完全入库）
     */
    @ApiModelProperty("入库状态（默认为 0，0 - 未入库，1 - 部分入库，2 - 完全入库）")
    private Integer warehousingState;

    /**
     * 关闭状态（默认为 0，0 - 未关闭，1 - 已关闭）
     */
    @ApiModelProperty("关闭状态（默认为 0，0 - 未关闭，1 - 已关闭）")
    private Integer offState;

    /**
     * 创建时间
     */
    @ApiModelProperty("创建时间")
    private String createTime;

    /**
     * 修改时间
     */
    @ApiModelProperty("修改时间")
    private String updateTime;

    /**
     * 审核时间
     */
    @ApiModelProperty("审核时间")
    private String checkTime;

    /**
     * 采购单据的商品
     */
    @ApiModelProperty("采购单据的商品")
    private List<SafePurchaseBillProductNumVO> safePurchaseBillProductNumVOList;

    private static final long serialVersionUID = 1L;
}
