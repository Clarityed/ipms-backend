package com.clarity.ipmsbackend.model.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 
 * @TableName ipms_department
 */
@TableName(value ="ipms_department")
@Data
public class IpmsDepartment implements Serializable {
    /**
     * 部门 id
     */
    @TableId(type = IdType.AUTO)
    private Long departmentId;

    /**
     * 部门编号
     */
    private String departmentCode;

    /**
     * 部门名称
     */
    private String departmentName;

    /**
     * 上级部门 id
     */
    private Long departmentSuper;

    /**
     * 所属企业 id
     */
    private Long enterpriseId;

    /**
     * 描述
     */
    private String departmentDescription;

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