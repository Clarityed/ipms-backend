package com.clarity.ipmsbackend.model.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * 安全仓位响应封装类
 *
 * @author: clarity
 * @date: 2023年02月28日 11:40
 */

@ApiModel("安全仓位响应封装类")
@Data
public class SafeWarehousePositionVO implements Serializable {

    /**
     * 仓位 id
     */
    @ApiModelProperty("仓位 id")
    private Long warehousePositionId;

    /**
     * 仓位编号
     */
    @ApiModelProperty("仓位编号")
    private String warehousePositionCode;

    /**
     * 仓位名称
     */
    @ApiModelProperty("仓位名称")
    private String warehousePositionName;

    /**
     * 仓位备注
     */
    @ApiModelProperty("仓位备注")
    private String warehousePositionRemark;

    /**
     * 所属仓库 id
     */
    @ApiModelProperty("所属仓库 id")
    private Long warehouseId;

    /**
     * 所属仓库名称
     */
    @ApiModelProperty("所属仓库名称")
    private String warehouseName;

    private static final long serialVersionUID = 1L;
}
