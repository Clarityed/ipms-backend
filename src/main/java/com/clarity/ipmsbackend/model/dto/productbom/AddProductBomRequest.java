package com.clarity.ipmsbackend.model.dto.productbom;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * 增加商品 BOM 关系请求封装类
 *
 * @author: clarity
 * @date: 2023年03月05日 15:10
 */

@ApiModel("增加商品 BOM 关系请求封装类")
@Data
public class AddProductBomRequest implements Serializable {

    /**
     * 商品 id
     */
    @ApiModelProperty("商品 id")
    private Long productId;

    /**
     * 子件商品 id
     */
    @ApiModelProperty("子件商品 id")
    private Long subcomponentProductId;

    /**
     * 子件材料用量
     */
    @ApiModelProperty("子件材料用量")
    private Integer subcomponentMaterialNum;

    /**
     * 子件损耗率（%）（1-100）
     */
    @ApiModelProperty("子件损耗率（%）（1-100）")
    private Integer subcomponentLossRate;

    /**
     * 子件领料方式
     */
    @ApiModelProperty("子件领料方式")
    private String subcomponentPickMethod;

    /**
     * 子件发料仓库 id
     */
    @ApiModelProperty("子件发料仓库 id")
    private Long subcomponentIssuingWarehouseId;

    /**
     * 子件物料备注
     */
    @ApiModelProperty("子件物料备注")
    private String subcomponentMaterialRemark;

    private static final long serialVersionUID = 1L;
}
