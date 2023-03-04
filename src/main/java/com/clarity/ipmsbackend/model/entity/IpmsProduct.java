package com.clarity.ipmsbackend.model.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 商品表
 * @TableName ipms_product
 */
@TableName(value ="ipms_product")
@Data
public class IpmsProduct implements Serializable {
    /**
     * 商品 id
     */
    @TableId(type = IdType.AUTO)
    private Long productId;

    /**
     * 商品编码
     */
    private String productCode;

    /**
     * 商品名称
     */
    private String productName;

    /**
     * 商品类别
     */
    private String productType;

    /**
     * 商品规格型号
     */
    private String productSpecification;

    /**
     * 计量单位 id
     */
    private Long unitId;

    /**
     * 商品采购价
     */
    private BigDecimal productPurchasePrice;

    /**
     * 商品参考成本
     */
    private BigDecimal productReferenceCost;

    /**
     * 是否可销售（0 - 表示不可销售，1 - 表示可销售）
     */
    private Integer isSale;

    /**
     * 是否可采购（0 - 表示不可采购，1 - 表示可采购）
     */
    private Integer isPurchase;

    /**
     * 是否可为子件（在 BOM 中作为原材料；0 - 表示不可为子件，1 - 表示可为子件）
     */
    private Integer isSubcomponent;

    /**
     * 是否可为组件（在 BOM 中可作为产品成品；0 - 表示不可为组件，1 - 表示可为组件）
     */
    private Integer isComponent;

    /**
     * 是否开启保质期管理（0 - 表示不开启保质期管理，1 - 表示开启保质期管理）
     */
    private Integer isShelfLifeManagement;

    /**
     * 商品保质期单位
     */
    private String productShelfLifeUnit;

    /**
     * 商品保质期
     */
    private Integer productShelfLife;

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