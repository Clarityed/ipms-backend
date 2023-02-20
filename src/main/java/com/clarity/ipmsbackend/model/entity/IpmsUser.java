package com.clarity.ipmsbackend.model.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 
 * @TableName ipms_user
 */
@TableName(value ="ipms_user")
@Data
public class IpmsUser implements Serializable {
    /**
     * 用户 id
     */
    @TableId(type = IdType.AUTO)
    private Long userId;

    /**
     * 用户编号
     */
    private String userCode;

    /**
     * 用户姓名
     */
    private String userName;

    /**
     * 用户性别（0 - 表示女，1 - 表示男）
     */
    private Integer userGender;

    /**
     * 用户账号
     */
    private String userAccount;

    /**
     * 用户密码
     */
    private String userPassword;

    /**
     * 用户角色
     */
    private String userRole;

    /**
     * 所属企业 id
     */
    private Long enterpriseId;

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