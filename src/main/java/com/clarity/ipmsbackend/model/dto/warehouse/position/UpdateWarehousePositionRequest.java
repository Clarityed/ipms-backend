package com.clarity.ipmsbackend.model.dto.warehouse.position;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * 更新仓位请求封装类
 *
 * @author: clarity
 * @date: 2023年02月28日 11:29
 */

@ApiModel("更新仓位请求封装类")
@Data
public class UpdateWarehousePositionRequest implements Serializable {

    /**
     * 仓位 id
     */
    @ApiModelProperty("仓位 id，作为更新的索引")
    private Long warehousePositionId;

    /**
     * 仓位编号
     */
    @ApiModelProperty("仓位编号，不能修改")
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
