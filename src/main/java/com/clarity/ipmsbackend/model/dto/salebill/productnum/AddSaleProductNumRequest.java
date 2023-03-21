package com.clarity.ipmsbackend.model.dto.salebill.productnum;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 增加销售单据商品及数量请求封装类
 *
 * @author: clarity
 * @date: 2023年03月13日 18:14
 */

@ApiModel("增加销售单据商品及数量请求封装类")
@Data
public class AddSaleProductNumRequest implements Serializable {

    /**
     * 商品 id
     */
    @ApiModelProperty("商品 id，不能为空，必须存在")
    private Long productId;

    /**
     * 仓库 id
     */
    @ApiModelProperty("仓库 id，不能为空，必须存在")
    private Long warehouseId;

    /**
     * 仓位 id
     */
    @ApiModelProperty("仓位 id，如果选择的仓库没有仓位，可以为空")
    private Long warehousePositionId;

    /**
     * 需要入库的商品数量
     */
    @ApiModelProperty("商品数量，不能为空，且必须大于 0")
    private BigDecimal productNum;

    /**
     * 商品单价
     */
    @ApiModelProperty("商品单价，不能为空，且必须大于 0")
    private BigDecimal unitPrice;

    /**
     * 价格合计
     */
    @ApiModelProperty("价格合计，不能为空，且必须大于 0")
    private BigDecimal totalPrice;

    private static final long serialVersionUID = 1L;
}
