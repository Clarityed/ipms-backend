package com.clarity.ipmsbackend.model.vo.bom;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * 安全 BOM 反向查询响应封装类
 *
 * @author: clarity
 * @date: 2023年03月09日 12:17
 */

@ApiModel("安全 BOM 正向查询响应封装类")
@Data
public class SafeReverseQueryBomVO implements Serializable {

    /**
     * 层级
     */
    @ApiModelProperty("层级")
    private Integer level;

    /**
     * 父级 BOM 编号
     */
    @ApiModelProperty("父级 BOM 编号")
    private String bomCode;

    /**
     * 父件物料编号
     */
    @ApiModelProperty("父件物料编号")
    private String productCode;

    /**
     * 父件物料名称
     */
    @ApiModelProperty("物料名称")
    private String productName;

    /**
     * 父件物料规格
     */
    @ApiModelProperty("父件物料规格")
    private String productSpecification;

    /**
     * 父件物料种类
     */
    @ApiModelProperty("父件物料种类")
    private String productType;

    /**
     * 审核状态（默认为 0，0 - 未审核，1 - 已审核）
     */
    @ApiModelProperty("审核状态（默认为 0，0 - 未审核，1 - 已审核）")
    private Integer checkState;

    /**
     * BOM 备注
     */
    @ApiModelProperty("BOM 备注")
    private String bomRemark;

    private static final long serialVersionUID = 1L;
}
