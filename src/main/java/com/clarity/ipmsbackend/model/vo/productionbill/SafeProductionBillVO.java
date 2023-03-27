package com.clarity.ipmsbackend.model.vo.productionbill;

import com.clarity.ipmsbackend.model.vo.SafeProductVO;
import com.clarity.ipmsbackend.model.vo.productionbill.productnum.SafeProductionBillProductNumVO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
 * 安全生产单据响应封装类
 *
 * @author: clarity
 * @date: 2023年03月23日 15:07
 */

@ApiModel("安全生产单据响应封装类")
@Data
public class SafeProductionBillVO implements Serializable {

    /**
     * 生产单据 id
     */
    @ApiModelProperty("生产单据 id，使用选单源功能时这个是作为单源的 id")
    private Long productionBillId;

    /**
     * 生产源单 id
     */
    @ApiModelProperty("生产源单 id，这个单源，指的是该条单据记录的源单是谁？")
    private Long productionSourceBillId;

    /**
     * 生产源单类型
     */
    @ApiModelProperty("生产源单类型")
    private String productionSourceBillType;

    /**
     * 生产源单编号
     */
    @ApiModelProperty("生产源单类型")
    private String productionSourceBillCode;

    /**
     * 生产单据编号
     */
    @ApiModelProperty("生产单据编号")
    private String productionBillCode;

    /**
     * 生产单据日期
     */
    @ApiModelProperty("生产单据日期")
    private String productionBillDate;

    /**
     * 职员 id（业务员也称为计划员、在领料环节称为领料人、在退料环节称为退料人、在入库环节称为交货人、在退库环节称为收货人）
     */
    @ApiModelProperty("职员 id（业务员也称为计划员、在领料环节称为领料人、在退料环节称为退料人、在入库环节称为交货人、在退库环节称为收货人）")
    private Long employeeId;

    /**
     * 职员姓名
     */
    @ApiModelProperty("职员姓名")
    private String employeeName;

    /**
     * 部门 id（部门也称为生产车间、在领料环节称为领料部门、在退料环节称为退料部门、在入库环节称为交货部门、在退库环节收货部门）
     */
    @ApiModelProperty("部门 id（部门也称为生产车间、在领料环节称为领料部门、在退料环节称为退料部门、在入库环节称为交货部门、在退库环节收货部门）")
    private Long departmentId;

    /**
     * 部门名称
     */
    @ApiModelProperty("部门名称")
    private String departmentName;

    /**
     * 仓管员 id（领料单和入库单才有）
     */
    @ApiModelProperty("仓管员 id（领料单和入库单才有）")
    private Long storekeeperId;

    /**
     * 仓管员姓名
     */
    @ApiModelProperty("仓管员姓名")
    private String storekeeperName;

    /**
     * 生产单据业务类型（只有生产任务单有这个属性）
     */
    @ApiModelProperty("生产单据业务类型（只有生产任务单有这个属性）")
    private String productionBillBusinessType;

    /**
     * 生产单据退还原因（退料原因或者是退库原因）
     */
    @ApiModelProperty("生产单据退还原因（退料原因或者是退库原因）")
    private String productionBillReturnReason;

    /**
     * 生产单据备注
     */
    @ApiModelProperty("生产单据备注")
    private String productionBillRemark;

    /**
     * 商品
     */
    @ApiModelProperty("生产任务单要生产的商品")
    private SafeProductVO safeProductVO;

    /**
     * 仓库 id （生产任务单必须输入，其他类型的单据都不用）
     */
    @ApiModelProperty("仓库 id")
    private Long warehouseId;

    /**
     * 仓库名称 （生产任务单必须输入，其他类型的单据都不用）
     */
    @ApiModelProperty("仓库名称")
    private String warehouseName;

    /**
     * 仓位 id（如果仓库有仓位，不能为空，满足条件的生产任务单填写，其他类型的单据都不用）
     */
    @ApiModelProperty("仓位 id")
    private Long warehousePositionId;

    /**
     * 仓位名称（如果仓库有仓位，不能为空，满足条件的生产任务单填写，其他类型的单据都不用）
     */
    @ApiModelProperty("仓位名称")
    private String warehousePositionName;

    /**
     * 需要入库的商品数量（表示商品数量，生产任务单必须输入，其他类型的单据都不用）
     */
    @ApiModelProperty("需要入库的商品数量（表示商品数量，生产任务单必须输入，其他类型的单据都不用）")
    private BigDecimal needWarehousingProductNum;

    /**
     * 剩余可被作为源单的商品数量（表示剩余的入库商品数量，生产任务单必须输入，其他类型的单据都不用）
     */
    @ApiModelProperty("剩余可被作为源单的商品数量（表示剩余的入库商品数量，生产任务单必须输入，其他类型的单据都不用）")
    private BigDecimal surplusNeedWarehousingProductNum;

    /**
     * 计划开工日期（生产任务单必须输入，其他类型的单据都不用）
     */
    @ApiModelProperty("计划开工日期")
    private String planCommencementDate;

    /**
     * 计划完工日期（生产任务单必须输入，其他类型的单据都不用）
     */
    @ApiModelProperty("计划完工日期")
    private String planCompletionDate;

    /**
     * 商品备注
     */
    @ApiModelProperty("商品备注")
    private String productRemark;

    /**
     * 单据类型（生产任务单、生产领料单、生产退料单、生产入库单、生产退库单）
     */
    @ApiModelProperty("单据类型")
    private String productionBillType;

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
     * 完工人
     */
    @ApiModelProperty("完工人")
    private String finisher;

    /**
     * 审核状态（默认为 0，0 - 未审核，1 - 已审核）
     */
    @ApiModelProperty("审核状态（默认为 0，0 - 未审核，1 - 已审核）")
    private Integer checkState;

    /**
     * 领料执行状态（默认为 0，0 - 未执行，1 - 部分执行，2 - 完全执行）
     */
    @ApiModelProperty("领料执行状态（默认为 0，0 - 未执行，1 - 部分执行，2 - 完全执行）")
    private Integer pickingExecutionState;

    /**
     * 领料状态（默认为 0，0 - 未领料，1 - 部分领料，2 - 完全领料）
     */
    @ApiModelProperty("领料状态（默认为 0，0 - 未领料，1 - 部分领料，2 - 完全领料）")
    private Integer pickingState;

    /**
     * 入库执行状态（默认为 0，0 - 未执行，1 - 部分执行，2 - 完全执行）
     */
    @ApiModelProperty("入库执行状态（默认为 0，0 - 未执行，1 - 部分执行，2 - 完全执行）")
    private Integer warehousingExecutionState;

    /**
     * 入库状态（默认为 0，0 - 未入库，1 - 部分入库，2 - 完全入库）
     */
    @ApiModelProperty("入库状态（默认为 0，0 - 未入库，1 - 部分入库，2 - 完全入库）")
    private Integer warehousingState;

    /**
     * 完工状态（默认为 0，0 - 未完工，1 - 已完工）
     */
    @ApiModelProperty("完工状态（默认为 0，0 - 未完工，1 - 已完工）")
    private Integer finishState;

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
     * 完工时间
     */
    @ApiModelProperty("完工时间")
    private String finishTime;

    /**
     * 生产单据的商品
     */
    @ApiModelProperty("生产单据的商品")
    List<SafeProductionBillProductNumVO> safeProductionBillProductNumVOList;

    private static final long serialVersionUID = 1L;
}