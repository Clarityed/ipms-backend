package com.clarity.ipmsbackend.model.vo.salebill;

import com.clarity.ipmsbackend.model.vo.SafeCustomerVO;
import com.clarity.ipmsbackend.model.vo.salebill.productnum.SafeSaleBillProductNumVO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
 * 安全销售单据响应封装类
 *
 * @author: clarity
 * @date: 2023年03月19日 10:20
 */

@ApiModel("安全销售单据响应封装类")
@Data
public class SafeSaleBillVO implements Serializable {

    /**
     * 销售单据 id
     */
    @ApiModelProperty("销售单据 id，使用选单源功能时这个是作为单源的 id")
    private Long saleBillId;

    /**
     * 销售源单 id
     */
    @ApiModelProperty("销售源单 id，这个单源，指的是该条单据记录的源单是谁？")
    private Long saleSourceBillId;

    /**
     * 销售源单类型
     */
    @ApiModelProperty("销售源单类型")
    private String saleSourceBillType;

    /**
     * 销售源单编号
     */
    @ApiModelProperty("销售源单类型")
    private String saleSourceBillCode;

    /**
     * 销售单据编号
     */
    @ApiModelProperty("销售单据编号")
    private String saleBillCode;

    /**
     * 销售单据日期
     */
    @ApiModelProperty("销售单据日期")
    private String saleBillDate;

    /**
     * 销售结算日期
     */
    @ApiModelProperty("销售结算日期")
    private String saleBillSettlementDate;

    /**
     * 客户
     */
    @ApiModelProperty("客户")
    private SafeCustomerVO safeCustomerVO;

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
     * 销售单据备注
     */
    @ApiModelProperty("销售单据备注")
    private String saleBillRemark;

    /**
     * 销售币别（默认人民币）
     */
    @ApiModelProperty("销售币别（默认人民币）")
    private String saleBillCurrencyType;

    /**
     * 销售汇率（默认 1）
     */
    @ApiModelProperty("销售汇率（默认 1）")
    private BigDecimal saleBillExchangeRate;

    /**
     * 销售成交金额
     */
    @ApiModelProperty("销售成交金额")
    private BigDecimal saleBillTransactionAmount;

    /**
     * 销售退货原因
     */
    @ApiModelProperty("销售退货原因，销售退货单展示该字段")
    private String saleBillReturnReason;

    /**
     * 销售单据类型（销售订单、销售出库单、销售退货单）
     */
    @ApiModelProperty("销售单据类型（销售订单、销售出库单、销售退货单），按需展示")
    private String saleBillType;

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
     * 出库状态（默认为 0，0 - 未出库，1 - 部分出库，2 - 完全出库）
     */
    @ApiModelProperty("出库状态（默认为 0，0 - 未出库，1 - 部分出库，2 - 完全出库）")
    private Integer deliveryState;

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
     * 销售单据的商品
     */
    @ApiModelProperty("销售单据的商品")
    private List<SafeSaleBillProductNumVO> safeSaleBillProductNumVOList;

    private static final long serialVersionUID = 1L;
}
