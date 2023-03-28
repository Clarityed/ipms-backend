package com.clarity.ipmsbackend.model.vo.inventorybill.otherreceiptorder.productnum;

import com.clarity.ipmsbackend.model.vo.SafeProductVO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 安全其他入库单商品响应封装类
 *
 * @author: clarity
 * @date: 2023年03月28日 18:11
 */

@ApiModel("安全其他入库单商品响应封装类")
@Data
public class SafeOtherReceiptOrderProductNumVO implements Serializable {

    /**
     * 库存单据商品 id
     */
    @ApiModelProperty("库存单据商品 id")
    private Long inventoryBillProductId;

    /**
     * 商品
     */
    @ApiModelProperty("商品")
    private SafeProductVO safeProductVO;

    /**
     * 仓库 id
     */
    @ApiModelProperty("仓库 id")
    private Long warehouseId;

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
     * 仓位名称
     */
    @ApiModelProperty("仓位名称")
    private String warehousePositionName;

    /**
     * 需要执行的商品数量
     */
    @ApiModelProperty("商品数量")
    private BigDecimal needExecutionProductNum;

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

    /**
     * 可用库存
     */
    @ApiModelProperty("可用库存")
    private BigDecimal availableInventory;

    private static final long serialVersionUID = 1L;
}
