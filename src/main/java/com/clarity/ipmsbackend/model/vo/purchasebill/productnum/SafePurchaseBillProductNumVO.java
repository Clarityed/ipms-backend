package com.clarity.ipmsbackend.model.vo.purchasebill.productnum;

import com.clarity.ipmsbackend.model.vo.SafeProductVO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 安全采购单据商品及数量响应封装类
 *
 * @author: clarity
 * @date: 2023年03月19日 10:58
 */

@ApiModel("安全采购单据商品及数量响应封装类")
@Data
public class SafePurchaseBillProductNumVO implements Serializable {

    /**
     * 采购单据商品 id
     */
    @ApiModelProperty("采购单据商品 id")
    private Long purchaseBillProductId;

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
    @ApiModelProperty("仓位 id")
    private String warehousePositionName;

    /**
     * 可用库存
     */
    @ApiModelProperty("可用库存")
    private BigDecimal availableInventory;

    /**
     * 需要入库的商品数量
     */
    @ApiModelProperty("商品数量")
    private BigDecimal needWarehousingProductNum;

    /**
     * 剩余需要入库的商品数量
     */
    @ApiModelProperty("在采购订单中表示需要入库的商品数量，在采购入库单中表示可以退货的商品数量，采购退货单没有意义不展示")
    private BigDecimal surplusNeedWarehousingProductNum;

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
