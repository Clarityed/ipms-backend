package com.clarity.ipmsbackend.model.dto.productionbill;

import com.clarity.ipmsbackend.model.dto.productionbill.productnum.UpdateProductionProductNumRequest;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
 * 修改生产单据请求封装类
 *
 * @author: clarity
 * @date: 2023年03月23日 15:03
 */

@ApiModel("修改生产单据请求封装类")
@Data
public class UpdateProductionBillRequest implements Serializable {

    /**
     * 生产单据 id
     */
    @ApiModelProperty("生产单据 id，更新索引 id")
    private Long productionBillId;

    /**
     * 生产源单 id
     */
    @ApiModelProperty("生产源单 id")
    private Long productionSourceBillId;

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
     * 生产单据业务类型（只要生产任务单有类型）
     */
    @ApiModelProperty("生产单据业务类型（只要生产任务单有类型）")
    private String productionBillBusinessType;

    /**
     * 职员 id（业务员也称为计划员、在领料环节称为领料人、在退料环节称为退料人、在入库环节称为交货人、在退库环节称为收货人）
     */
    private Long employeeId;

    /**
     * 部门 id（部门也称为生产车间、在领料环节称为领料部门、在退料环节称为退料部门、在入库环节称为交货部门、在退库环节收货部门）
     */
    @ApiModelProperty("职员 id（业务员在生产任务单中称为计划员、在领料环节称为领料人、在退料环节称为退料人、在入库环节称为交货人、在退库环节称为收货人）")
    private Long departmentId;

    /**
     * 仓管员 id（领料单和入库单才有）
     */
    @ApiModelProperty("仓管员 id（领料单和入库单才有）")
    private Long storekeeperId;

    /**
     * 生产单据退还原因（退料原因或者是退库原因）
     */
    @ApiModelProperty("生产单据退还原因（退料原因或者是退库原因，在领料环节和退库环节中可以输入）")
    private String productionBillReturnReason;

    /**
     * 生产单据备注
     */
    @ApiModelProperty("生产单据备注")
    private String productionBillRemark;

    /**
     * 商品 id （生产任务单必须输入，其他类型的单据都不用）
     */
    @ApiModelProperty("商品 id （生产任务单必须输入，其他类型的单据都不用）")
    private Long productId;

    /**
     * 仓库 id （生产任务单必须输入，其他类型的单据都不用）
     */
    @ApiModelProperty("仓库 id （生产任务单必须输入，其他类型的单据都不用）")
    private Long warehouseId;

    /**
     * 仓位 id（如果仓库有仓位，不能为空，满足条件的生产任务单填写，其他类型的单据都不用）
     */
    @ApiModelProperty("仓位 id（如果仓库有仓位，不能为空，满足条件的生产任务单填写，其他类型的单据都不用）")
    private Long warehousePositionId;

    /**
     * 需要入库的商品数量（表示商品数量，生产任务单必须输入，其他类型的单据都不用）
     */
    @ApiModelProperty("需要入库的商品数量（表示商品数量，生产任务单必须输入，其他类型的单据都不用）")
    private BigDecimal productNum;

    /**
     * 计划开工日期（生产任务单必须输入，其他类型的单据都不用）
     */
    @ApiModelProperty("计划开工日期（生产任务单必须输入，其他类型的单据都不用）")
    private String planCommencementDate;

    /**
     * 计划完工日期（生产任务单必须输入，其他类型的单据都不用）
     */
    @ApiModelProperty("计划完工日期（生产任务单必须输入，其他类型的单据都不用）")
    private String planCompletionDate;

    /**
     * 商品备注
     */
    @ApiModelProperty("商品备注")
    private String productRemark;

    /**
     * 单据类型（生产任务单、生产领料单、生产退料单、生产入库单、生产退库单）
     */
    @ApiModelProperty("单据类型（生产任务单、生产领料单、生产退料单、生产入库单、生产退库单）")
    private String productionBillType;

    /**
     * 修改生产单据商品及数量请求列表
     */
    @ApiModelProperty("修改生产单据商品及数量请求列表")
    private List<UpdateProductionProductNumRequest> updateProductionProductNumRequestList;

    private static final long serialVersionUID = 1L;
}
