package com.clarity.ipmsbackend.model.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 仓位表
 * @TableName ipms_warehouse_position
 */
@TableName(value ="ipms_warehouse_position")
@Data
public class IpmsWarehousePosition implements Serializable {
    /**
     * 仓位 id
     */
    @TableId(type = IdType.AUTO)
    private Long warehousePositionId;

    /**
     * 仓位编号
     */
    private String warehousePositionCode;

    /**
     * 仓位名称
     */
    private String warehousePositionName;

    /**
     * 仓位备注
     */
    private String warehousePositionRemark;

    /**
     * 所属仓库 id
     */
    private Long warehouseId;

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