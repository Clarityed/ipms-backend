package com.clarity.ipmsbackend.model.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 仓库表
 * @TableName ipms_warehouse
 */
@TableName(value ="ipms_warehouse")
@Data
public class IpmsWarehouse implements Serializable {
    /**
     * 仓库 id
     */
    @TableId(type = IdType.AUTO)
    private Long warehouseId;

    /**
     * 仓库编码
     */
    private String warehouseCode;

    /**
     * 仓库名称
     */
    private String warehouseName;

    /**
     * 仓库类别
     */
    private String warehouseType;

    /**
     * 仓库行政区划
     */
    private String warehouseAdminDivision;

    /**
     * 仓库详细地址
     */
    private String warehouseDetailAddress;

    /**
     * 仓库备注
     */
    private String warehouseRemark;

    /**
     * 是否允许负库存（0 - 不允许负库存，1 - 允许负库存）
     */
    private Integer isNegativeInventory;

    /**
     * 是否启用仓位管理（0 - 不启用仓位管理，1 - 启用仓位管理）
     */
    private Integer isWarehousePositionManagement;

    /**
     * 仓管 id
     */
    private Long warehouseKeeperId;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 修改时间
     */
    private Date updateTime;

    /**
     * 逻辑删除
     */
    @TableLogic
    private Integer isDelete;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}