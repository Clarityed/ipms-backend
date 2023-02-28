package com.clarity.ipmsbackend.model.dto.warehouse;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * 更新仓库请求封装类
 *
 * @author: clarity
 * @date: 2023年02月27日 19:56
 */

@ApiModel("更新仓库请求封装类")
@Data
public class UpdateWarehouseRequest implements Serializable {

    /**
     * 仓库 id
     */
    @ApiModelProperty("仓库 id，作为更新的索引")
    private Long warehouseId;

    /**
     * 仓库编码
     */
    @ApiModelProperty("仓库编码，不能修改")
    private String warehouseCode;

    /**
     * 仓库名称
     */
    @ApiModelProperty("仓库名称")
    private String warehouseName;

    /**
     * 仓库类别
     */
    @ApiModelProperty("仓库类别")
    private String warehouseType;

    /**
     * 仓库行政区划
     */
    @ApiModelProperty("仓库行政区划")
    private String warehouseAdminDivision;

    /**
     * 仓库详细地址
     */
    @ApiModelProperty("仓库详细地址")
    private String warehouseDetailAddress;

    /**
     * 仓库备注
     */
    @ApiModelProperty("仓库备注")
    private String warehouseRemark;

    /**
     * 是否允许负库存（0 - 不允许负库存，1 - 允许负库存）
     */
    @ApiModelProperty("是否允许负库存（0 - 不允许负库存，1 - 允许负库存）")
    private Integer isNegativeInventory;

    /**
     * 是否启用仓位管理（0 - 不启用仓位管理，1 - 启用仓位管理）
     */
    @ApiModelProperty("是否启用仓位管理（0 - 不启用仓位管理，1 - 启用仓位管理）")
    private Integer isWarehousePositionManagement;

    /**
     * 仓管 id
     */
    @ApiModelProperty("仓管 id")
    private Long warehouseKeeperId;

    private static final long serialVersionUID = 1L;
}
