package com.clarity.ipmsbackend.model.dto.inventorybill.otherreceiptorder.productnum;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 更新其他入库单商品请求封装类
 *
 * @author: clarity
 * @date: 2023年03月28日 9:19
 */

@ApiModel("更新其他入库单商品请求封装类")
@Data
public class UpdateOtherReceiptOrderProductNumRequest implements Serializable {

    /**
     * 库存单据商品 id
     */
    @ApiModelProperty("库存单据商品 id")
    private Long inventoryBillProductId;

    /**
     * 商品 id
     */
    @ApiModelProperty("商品 id")
    private Long productId;

    /**
     * 仓库 id
     */
    @ApiModelProperty("仓库 id")
    private Long warehouseId;

    /**
     * 仓位 id
     */
    @ApiModelProperty("仓位 id")
    private Long warehousePositionId;

    /**
     * 商品数量
     */
    @ApiModelProperty("商品数量")
    private BigDecimal productNum;

    /**
     * 商品单价
     */
    @ApiModelProperty("商品单价")
    private BigDecimal unitPrice;

    /**
     * 价格合计
     */
    @ApiModelProperty("价格合计")
    private BigDecimal totalPrice;

    private static final long serialVersionUID = 1L;
}
