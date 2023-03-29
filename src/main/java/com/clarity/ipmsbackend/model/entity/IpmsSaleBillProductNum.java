package com.clarity.ipmsbackend.model.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 销售单据商品数量表
 * @TableName ipms_sale_bill_product_num
 */
@TableName(value ="ipms_sale_bill_product_num")
@Data
public class IpmsSaleBillProductNum implements Serializable {
    /**
     * 采购单据商品 id
     */
    @TableId(type = IdType.AUTO)
    private Long saleBillProductId;

    /**
     * 采购单据 id
     */
    private Long saleBillId;

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
    private BigDecimal needDeliveryProductNum;

    /**
     * 剩余需要入库的商品数量（该字段用于判断单据之间的引用关系）
     */
    private BigDecimal surplusNeedDeliveryProductNum;

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