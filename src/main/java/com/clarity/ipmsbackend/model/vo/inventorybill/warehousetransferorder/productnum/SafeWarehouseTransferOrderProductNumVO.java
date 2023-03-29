package com.clarity.ipmsbackend.model.vo.inventorybill.warehousetransferorder.productnum;

import com.clarity.ipmsbackend.model.vo.SafeProductVO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 安全移仓单商品响应封装类
 *
 * @author: clarity
 * @date: 2023年03月28日 18:11
 */

@ApiModel("安全其他入库单商品响应封装类")
@Data
public class SafeWarehouseTransferOrderProductNumVO implements Serializable {

    /**
     * 库存单据商品 id
     */
    @ApiModelProperty("库存单据商品 id")
    private Long inventoryBillProductId;

    /**
     * 仓库 id（在移仓单、调拨入库单和调拨出库单中称为商品调出仓库 id）
     */
    @ApiModelProperty("仓库 id（在移仓单中称为商品调出仓库 id）")
    private Long warehouseId;

    /**
     * 仓库名称（在移仓单、调拨入库单和调拨出库单中称为商品调出仓库 id）
     */
    @ApiModelProperty("仓库名称（在移仓单中称为商品调出仓库名称）")
    private String warehouseName;

    /**
     * 仓位 id（在移仓单、调拨入库单和调拨出库单中称为商品调出仓位 id）
     */
    @ApiModelProperty("仓位 id（在移仓单中称为商品调出仓位 id）")
    private Long warehousePositionId;

    /**
     * 仓位名称（在移仓单、调拨入库单和调拨出库单中称为商品调出仓位 id）
     */
    @ApiModelProperty("仓位名称（在移仓单中称为商品调出仓位名称）")
    private String warehousePositionName;

    /**
     * 调入仓库 id（只在移仓单、调拨入库单和调拨出库单中有该字段）
     */
    @ApiModelProperty("调入仓库 id（仓库 id）")
    private Long transferWarehouseId;

    /**
     * 调入仓库名称（只在移仓单、调拨入库单和调拨出库单中有该字段）
     */
    @ApiModelProperty("调入仓库名称（仓库名称）")
    private String transferWarehouseName;

    /**
     * 调入仓位 id（只在移仓单、调拨入库单和调拨出库单中有该字段）
     */
    @ApiModelProperty("调入仓位 id（仓位 id）")
    private Long transferWarehousePositionId;

    /**
     * 调入仓位名称（只在移仓单、调拨入库单和调拨出库单中有该字段）
     */
    @ApiModelProperty("调入仓位名称（仓位名称）")
    private String transferWarehousePositionName;

    /**
     * 商品
     */
    @ApiModelProperty("商品")
    private SafeProductVO safeProductVO;

    /**
     * 需要执行的商品数量
     */
    @ApiModelProperty("商品数量")
    private BigDecimal needExecutionProductNum;

    /**
     * 调出仓可用库存 Transfer out of warehouse
     */
    @ApiModelProperty("调出仓可用库存")
    private BigDecimal transferOutAvailableInventory;

    /**
     * 调入仓可用库存 Transferred into warehouse
     */
    @ApiModelProperty("调入仓可用库存")
    private BigDecimal transferIntoAvailableInventory;

    private static final long serialVersionUID = 1L;
}
