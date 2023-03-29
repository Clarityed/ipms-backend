package com.clarity.ipmsbackend.model.dto.inventorybill.warehousetransferorder.productnum;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 增加移仓单商品请求封装类
 *
 * @author: clarity
 * @date: 2023年03月28日 9:19
 */

@ApiModel("增加移仓单商品请求封装类")
@Data
public class AddWarehouseTransferOrderProductNumRequest implements Serializable {

    /**
     * 仓库 id（在移仓单、调拨入库单和调拨出库单中称为商品调出仓库 id）
     */
    @ApiModelProperty("仓库 id（在移仓单中称为商品调出仓库 id）")
    private Long warehouseId;

    /**
     * 仓位 id（在移仓单、调拨入库单和调拨出库单中称为商品调出仓位 id）
     */
    @ApiModelProperty("仓位 id（在移仓单中称为商品调出仓位 id）")
    private Long warehousePositionId;

    /**
     * 调入仓库 id（只在移仓单、调拨入库单和调拨出库单中有该字段）
     */
    @ApiModelProperty("调入仓库 id")
    private Long transferWarehouseId;

    /**
     * 调入仓位 id（只在移仓单、调拨入库单和调拨出库单中有该字段）
     */
    @ApiModelProperty("调入仓位 id")
    private Long transferWarehousePositionId;

    /**
     * 商品 id
     */
    @ApiModelProperty("商品 id")
    private Long productId;

    /**
     * 商品数量
     */
    @ApiModelProperty("商品数量")
    private BigDecimal productNum;

    private static final long serialVersionUID = 1L;
}
