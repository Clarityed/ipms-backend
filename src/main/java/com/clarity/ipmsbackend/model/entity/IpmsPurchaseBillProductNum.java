package com.clarity.ipmsbackend.model.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 采购单据商品数量表
 * @TableName ipms_purchase_bill_product_num
 */
@TableName(value ="ipms_purchase_bill_product_num")
@Data
public class IpmsPurchaseBillProductNum implements Serializable {
    /**
     * 采购单据商品 id
     */
    @TableId(type = IdType.AUTO)
    private Long purchaseBillProductId;

    /**
     * 采购单据 id
     */
    private Long purchaseBillId;

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
     * 需要入库的商品数量
     */
    private BigDecimal needWarehousingProductNum;

    /**
     * 剩余需要入库的商品数量
     */
    private BigDecimal surplusNeedWarehousingProductNum;

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