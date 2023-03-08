package com.clarity.ipmsbackend.model.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 商品物料清单（BOM） 关系表
 * @TableName ipms_product_bom
 */
@TableName(value ="ipms_product_bom")
@Data
public class IpmsProductBom implements Serializable {
    /**
     * 商品 BOM 关系 id
     */
    @TableId(type = IdType.AUTO)
    private Long productBomId;

    /**
     * 商品 id
     */
    private Long productId;

    /**
     * BOM id
     */
    private Long bomId;

    /**
     * 子件 BOM id
     */
    private Long subcomponentBomId;

    /**
     * 子件商品 id
     */
    private Long subcomponentProductId;

    /**
     * 子件材料用量
     */
    private Integer subcomponentMaterialNum;

    /**
     * 子件损耗率（%）（1-100）
     */
    private Integer subcomponentLossRate;

    /**
     * 子件领料方式
     */
    private String subcomponentPickMethod;

    /**
     * 子件发料仓库 id
     */
    private Long subcomponentIssuingWarehouseId;

    /**
     * 子件物料备注
     */
    private String subcomponentMaterialRemark;

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