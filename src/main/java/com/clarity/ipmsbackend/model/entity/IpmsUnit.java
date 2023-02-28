package com.clarity.ipmsbackend.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 计量单位表
 * @TableName ipms_unit
 */
@TableName(value ="ipms_unit")
@Data
public class IpmsUnit implements Serializable {
    /**
     * 计量单位 id
     */
    @TableId(type = IdType.AUTO)
    private Long unitId;

    /**
     * 计量单位编号
     */
    private String unitCode;

    /**
     * 计量单位名称
     */
    private String unitName;

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
    private Integer isDelete;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}