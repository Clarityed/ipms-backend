package com.clarity.ipmsbackend.model.dto.product;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 修改商品请求封装类
 *
 * @author: clarity
 * @date: 2023年03月04日 14:53
 */

@ApiModel("修改商品请求封装类")
@Data
public class UpdateProductRequest implements Serializable {

    /**
     * 商品 id
     */
    @ApiModelProperty("商品 id，作为更新的索引")
    private Long productId;

    /**
     * 商品编码
     */
    @ApiModelProperty("商品编码，不能修改")
    private String productCode;

    /**
     * 商品名称
     */
    @ApiModelProperty("商品名称")
    private String productName;

    /**
     * 商品类别
     */
    @ApiModelProperty("商品类别")
    private String productType;

    /**
     * 商品规格型号
     */
    @ApiModelProperty("商品规格型号")
    private String productSpecification;

    /**
     * 计量单位 id
     */
    @ApiModelProperty("计量单位 id")
    private Long unitId;

    /**
     * 商品采购价
     */
    @ApiModelProperty("商品采购价")
    private BigDecimal productPurchasePrice;

    /**
     * 商品参考成本
     */
    @ApiModelProperty("商品参考成本")
    private BigDecimal productReferenceCost;

    /**
     * 是否可销售（0 - 表示不可销售，1 - 表示可销售）
     */
    @ApiModelProperty("是否可销售（0 - 表示不可销售，1 - 表示可销售）")
    private Integer isSale;

    /**
     * 是否可采购（0 - 表示不可采购，1 - 表示可采购）
     */
    @ApiModelProperty("是否可采购（0 - 表示不可采购，1 - 表示可采购）")
    private Integer isPurchase;

    /**
     * 是否可为子件（在 BOM 中作为原材料；0 - 表示不可为子件，1 - 表示可为子件）
     */
    @ApiModelProperty("是否可为子件（在 BOM 中作为原材料；0 - 表示不可为子件，1 - 表示可为子件）")
    private Integer isSubcomponent;

    /**
     * 是否可为组件（在 BOM 中可作为产品成品；0 - 表示不可为组件，1 - 表示可为组件）
     */
    @ApiModelProperty("是否可为组件（在 BOM 中可作为产品成品；0 - 表示不可为组件，1 - 表示可为组件）")
    private Integer isComponent;

    /**
     * 是否开启保质期管理（0 - 表示不开启保质期管理，1 - 表示开启保质期管理）
     */
    @ApiModelProperty("是否开启保质期管理（0 - 表示不开启保质期管理，1 - 表示开启保质期管理）")
    private Integer isShelfLifeManagement;

    /**
     * 商品保质期单位
     */
    @ApiModelProperty("商品保质期单位")
    private String productShelfLifeUnit;

    /**
     * 商品保质期
     */
    @ApiModelProperty("商品保质期")
    private Integer productShelfLife;

    private static final long serialVersionUID = 1L;
}
