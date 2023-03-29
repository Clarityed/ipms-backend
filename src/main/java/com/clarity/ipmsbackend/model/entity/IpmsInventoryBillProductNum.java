package com.clarity.ipmsbackend.model.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 库存单据商品数量表
 * @TableName ipms_inventory_bill_product_num
 */
@TableName(value ="ipms_inventory_bill_product_num")
@Data
public class IpmsInventoryBillProductNum implements Serializable {
    /**
     * 库存单据商品 id
     */
    @TableId(type = IdType.AUTO)
    private Long inventoryBillProductId;

    /**
     * 库存单据 id
     */
    private Long inventoryBillId;

    /**
     * 商品 id
     */
    private Long productId;

    /**
     * 仓库 id（在移仓单、调拨入库单和调拨出库单中称为商品调出仓库 id）
     */
    private Long warehouseId;

    /**
     * 仓位 id（在移仓单、调拨入库单和调拨出库单中称为商品调出仓位 id）
     */
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    private Long warehousePositionId;

    /**
     * 调入仓库 id（只在移仓单、调拨入库单和调拨出库单中有该字段）
     */
    private Long transferWarehouseId;

    /**
     * 调入仓位 id（只在移仓单、调拨入库单和调拨出库单中有该字段）
     */
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    private Long transferWarehousePositionId;

    /**
     * 需要执行的商品数量
     */
    private BigDecimal needExecutionProductNum;

    /**
     * 剩余需要执行的商品数量（该字段用于判断单据之间的引用关系，是否还能作为相关单据的源单）
     */
    private BigDecimal surplusNeedExecutionProductNum;

    /**
     * 商品单价
     */
    private BigDecimal unitPrice;

    /**
     * 价格合计
     */
    private BigDecimal totalPrice;

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