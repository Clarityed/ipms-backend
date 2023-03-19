package com.clarity.ipmsbackend.model.dto.purchasebill.productnum;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 修改采购单据商品及数量请求封装类
 *
 * @author: clarity
 * @date: 2023年03月17日 10:11
 */

@ApiModel("修改采购单据商品及数量请求封装类")
@Data
public class UpdateProductNumRequest implements Serializable {

    /**
     * 采购单据商品 id
     */
    @ApiModelProperty("采购单据商品 id，作为更新单据的索引")
    private Long purchaseBillProductId;

    /**
     * 商品 id
     */
    @ApiModelProperty("商品 id，可以修改")
    private Long productId;

    /**
     * 仓库 id
     */
    @ApiModelProperty("仓库 id，可以修改")
    private Long warehouseId;

    /**
     * 仓位 id
     */
    @ApiModelProperty("如果有仓位，要选择仓位 id，可以修改")
    private Long warehousePositionId;

    /**
     * 需要入库的商品数量
     */
    @ApiModelProperty("商品数量，不能为空，可以修改，且必须大于 0")
    private BigDecimal productNum;

    /**
     * 商品单价
     */
    @ApiModelProperty("商品单价，不能为空，可以修改，且必须大于 0")
    private BigDecimal unitPrice;

    /**
     * 价格合计
     */
    @ApiModelProperty("价格合计，不能为空，可以修改，且必须大于 0")
    private BigDecimal totalPrice;

    private static final long serialVersionUID = 1L;
}
