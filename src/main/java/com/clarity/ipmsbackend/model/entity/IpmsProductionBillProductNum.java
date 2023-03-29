package com.clarity.ipmsbackend.model.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 生产单据商品数量表
 * @TableName ipms_production_bill_product_num
 */
@TableName(value ="ipms_production_bill_product_num")
@Data
public class IpmsProductionBillProductNum implements Serializable {
    /**
     * 生产单据商品 id
     */
    @TableId(type = IdType.AUTO)
    private Long productionBillProductId;

    /**
     * 生产单据 id
     */
    private Long productionBillId;

    /**
     * 商品 id
     */
    private Long productId;

    /**
     * 仓库 id
     */
    private Long warehouseId;

    /**
     * 仓位 id
     */
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    private Long warehousePositionId;

    /**
     * 商品材料分子（在生产任务单中表示材料分子，必须填写，如果存在BOM单，在BOM单中获取，其他类型的单据都不用）
     */
    private BigDecimal productMaterialMole;

    /**
     * 需要执行的商品数量（表示商品数量）
     */
    private BigDecimal needExecutionProductNum;

    /**
     * 剩余可被作为源单的商品数量
     */
    private BigDecimal surplusNeedExecutionProductNum;

    /**
     * 商品发料方式（默认直接发料）
     */
    private String productIssuanceMethod;

    /**
     * 商品备注
     */
    private String productRemark;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 修改时间
     */
    private Date updateTime;

    /**
     * 逻辑删除
     */
    @TableLogic
    private Integer isDelete;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}