package com.clarity.ipmsbackend.model.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 商品库存表
 * @TableName ipms_product_inventory
 */
@TableName(value ="ipms_product_inventory")
@Data
public class IpmsProductInventory implements Serializable {
    /**
     * 商品库存 id
     */
    @TableId(type = IdType.AUTO)
    private Long productInventoryId;

    /**
     * 仓库 id
     */
    private Long warehouseId;

    /**
     * 仓位 id
     */
    private Long warehousePositionId;

    /**
     * 商品 id
     */
    private Long productId;

    /**
     * 商品库存剩余数量
     */
    private BigDecimal productInventorySurplusNum;

    /**
     * 库存商品单位成本
     */
    private BigDecimal productInventoryUnitCost;


    /**
     * 商品单位成本（不会为 0）
     */
    private BigDecimal productUnitCost;

    /**
     * 商品成本
     */
    private BigDecimal productInventoryCost;

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