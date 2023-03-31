package com.clarity.ipmsbackend.model.vo.inventory;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 安全商品库存查询响应封装类
 *
 * @author: clarity
 * @date: 2023年03月31日 9:15
 */

@ApiModel("安全商品库存查询响应封装类")
@Data
public class ProductInventoryQueryVO implements Serializable {

    /**
     * 商品编码
     */
    @ApiModelProperty("商品编码")
    private String productCode;

    /**
     * 商品名称
     */
    @ApiModelProperty("商品编码")
    private String productName;

    /**
     * 商品规格型号
     */
    @ApiModelProperty("商品规格型号")
    private String productSpecification;

    /**
     * 仓库名称
     */
    @ApiModelProperty("仓库名称")
    private String warehouseName;

    /**
     * 仓位 id
     */
    @ApiModelProperty("仓位 id")
    private Long warehousePositionId;

    /**
     * 计量单位 id
     */
    @ApiModelProperty("计量单位 id")
    private Long unitId;

    /**
     * 商品库存剩余数量
     */
    @ApiModelProperty("商品库存剩余数量")
    private BigDecimal productInventorySurplusNum;

    /**
     * 库存商品单位成本
     */
    @ApiModelProperty("库存商品单位成本 = 商品成本 / 商品库存剩余数量")
    private BigDecimal productInventoryUnitCost;

    /**
     * 商品成本
     */
    @ApiModelProperty("商品成本 = 入库成本合计 - 出库成本合计")
    private BigDecimal productInventoryCost;

    private static final long serialVersionUID = 1L;
}
