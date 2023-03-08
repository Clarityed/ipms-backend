package com.clarity.ipmsbackend.model.dto.bom;

import com.clarity.ipmsbackend.model.dto.productbom.AddProductBomRequest;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 增加 BOM 请求封装类
 *
 * @author: clarity
 * @date: 2023年03月05日 15:04
 */

@ApiModel("增加 BOM 请求封装类")
@Data
public class AddBomRequest implements Serializable {

    /**
     * BOM 编号
     */
    @ApiModelProperty("BOM 编号")
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
     * 增加商品物料清单（BOM）关系请求列表
     */
    @ApiModelProperty("增加商品物料清单（BOM）关系请求列表")
    private List<AddProductBomRequest> addProductBomRequestList;

    private static final long serialVersionUID = 1L;
}
