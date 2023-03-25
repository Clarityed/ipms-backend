package com.clarity.ipmsbackend.model.dto.productionbill.productnum;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 修改生产单据商品及数量请求封装类
 *
 * @author: clarity
 * @date: 2023年03月23日 15:05
 */

@ApiModel("修改生产单据商品及数量请求封装类")
@Data
public class UpdateProductionProductNumRequest implements Serializable {

    /**
     * 生产单据商品 id
     */
    @ApiModelProperty("生产单据商品 id")
    private Long productionBillProductId;

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
     * 商品材料分子（在生产任务单中表示材料分子，必须填写，如果存在BOM单，在BOM单中获取，其他类型的单据都不用）
     */
    @ApiModelProperty("商品材料分子（在生产任务单中表示材料分子，必须填写，如果存在BOM单，在BOM单中获取，其他类型的单据都不用）")
    private BigDecimal productMaterialMole;

    /**
     * 需要执行的商品数量（表示商品数量）
     */
    @ApiModelProperty("需要执行的商品数量，在生产任务单中表示标准用量（父级商品数量 * 商品材料分子），在其他类型的生产单据中表示商品数量")
    private BigDecimal productNum;

    /**
     * 商品发料方式（默认直接发料）
     */
    @ApiModelProperty("商品发料方式（默认直接发料）")
    private String productIssuanceMethod;

    /**
     * 商品备注
     */
    @ApiModelProperty("商品备注")
    private String productRemark;

    private static final long serialVersionUID = 1L;
}
