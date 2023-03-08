package com.clarity.ipmsbackend.model.dto.bom;

import com.clarity.ipmsbackend.model.dto.productbom.UpdateProductBomRequest;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 修改 BOM 请求封装类
 *
 * @author: clarity
 * @date: 2023年03月05日 15:04
 */

@ApiModel("修改 BOM 请求封装类")
@Data
public class UpdateBomRequest implements Serializable {

    /**
     * BOM id
     */
    @ApiModelProperty("BOM id，作为更新的索引")
    private Long bomId;

    /**
     * BOM 编号
     */
    @ApiModelProperty("BOM 编号，不能修改")
    private String bomCode;

    /**
     * BOM 备注
     */
    @ApiModelProperty("BOM 备注")
    private String bomRemark;

    /**
     * BOM 分类 id
     */
    @ApiModelProperty("BOM 分类 id")
    private Long bomClassId;

    /**
     * 修改商品物料清单（BOM）关系请求列表
     */
    @ApiModelProperty("修改商品物料清单（BOM）关系请求列表")
    private List<UpdateProductBomRequest> updateProductBomRequestList;

    private static final long serialVersionUID = 1L;
}
