package com.clarity.ipmsbackend.model.dto.salebill.productnum;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 修改销售单据商品及数量请求封装类
 *
 * @author: clarity
 * @date: 2023年03月17日 10:11
 */

@ApiModel("修改销售单据商品及数量请求封装类")
@Data
public class UpdateSaleProductNumRequest implements Serializable {

    /**
     * 销售单据商品 id
     */
    @ApiModelProperty("销售单据商品 id，作为更新单据的索引")
    private Long saleBillProductId;

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
