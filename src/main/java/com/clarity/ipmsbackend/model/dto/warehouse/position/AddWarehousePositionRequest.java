package com.clarity.ipmsbackend.model.dto.warehouse.position;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * 增加仓位请求封装类
 *
 * @author: clarity
 * @date: 2023年02月28日 11:29
 */

@ApiModel("增加仓位请求封装类")
@Data
public class AddWarehousePositionRequest implements Serializable {

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

    private static final long serialVersionUID = 1L;
}
