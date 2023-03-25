package com.clarity.ipmsbackend.model.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 生产单据
 * @TableName ipms_production_bill
 */
@TableName(value ="ipms_production_bill")
@Data
public class IpmsProductionBill implements Serializable {
    /**
     * 生产单据 id
     */
    @TableId(type = IdType.AUTO)
    private Long productionBillId;

    /**
     * 生产源单 id
     */
    private Long productionSourceBillId;

    /**
     * 生产单据编号
     */
    private String productionBillCode;

    /**
     * 生产单据日期
     */
    private String productionBillDate;

    /**
     * 生产单据业务类型（只要生产任务单有类型）
     */
    private String productionBillBusinessType;

    /**
     * 职员 id（业务员也称为计划员、在领料环节称为领料人、在退料环节称为退料人、在入库环节称为交货人、在退库环节称为收货人）
     */
    private Long employeeId;

    /**
     * 部门 id（部门也称为生产车间、在领料环节称为领料部门、在退料环节称为退料部门、在入库环节称为交货部门、在退库环节收货部门）
     */
    private Long departmentId;

    /**
     * 仓管员 id（领料单和入库单才有）
     */
    private Long storekeeperId;

    /**
     * 生产单据退还原因（退料原因或者是退库原因）
     */
    private String productionBillReturnReason;

    /**
     * 生产单据备注
     */
    private String productionBillRemark;

    /**
     * 商品 id （生产任务单必须输入，其他类型的单据都不用）
     */
    private Long productId;

    /**
     * 仓库 id （生产任务单必须输入，其他类型的单据都不用）
     */
    private Long warehouseId;

    /**
     * 仓位 id（如果仓库有仓位，不能为空，满足条件的生产任务单填写，其他类型的单据都不用）
     */
    private Long warehousePositionId;

    /**
     * 需要入库的商品数量（表示商品数量，生产任务单必须输入，其他类型的单据都不用）
     */
    private BigDecimal needWarehousingProductNum;

    /**
     * 剩余可被作为源单的商品数量（表示剩余的入库商品数量，生产任务单必须输入，其他类型的单据都不用）
     */
    private BigDecimal surplusNeedWarehousingProductNum;

    /**
     * 计划开工日期（生产任务单必须输入，其他类型的单据都不用）
     */
    private String planCommencementDate;

    /**
     * 计划完工日期（生产任务单必须输入，其他类型的单据都不用）
     */
    private String planCompletionDate;

    /**
     * 商品备注
     */
    private String productRemark;

    /**
     * 单据类型（生产任务单、生产领料单、生产退料单、生产入库单、生产退库单）
     */
    private String productionBillType;

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
     * 完工人
     */
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    private String finisher;

    /**
     * 审核状态（默认为 0，0 - 未审核，1 - 已审核）
     */
    private Integer checkState;

    /**
     * 领料执行状态（默认为 0，0 - 未执行，1 - 部分执行，2 - 完全执行）
     */
    private Integer pickingExecutionState;

    /**
     * 领料状态（默认为 0，0 - 未领料，1 - 部分领料，2 - 完全领料）
     */
    private Integer pickingState;

    /**
     * 入库执行状态（默认为 0，0 - 未执行，1 - 部分执行，2 - 完全执行）
     */
    private Integer warehousingExecutionState;

    /**
     * 入库状态（默认为 0，0 - 未入库，1 - 部分入库，2 - 完全入库）
     */
    private Integer warehousingState;

    /**
     * 完工状态（默认为 0，0 - 未完工，1 - 已完工）
     */
    private Integer finishState;

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
     * 完工时间
     */
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    private Date finishTime;

    /**
     * 逻辑删除
     */
    @TableLogic
    private Integer isDelete;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}