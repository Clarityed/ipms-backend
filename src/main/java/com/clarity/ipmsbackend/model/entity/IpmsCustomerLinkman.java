package com.clarity.ipmsbackend.model.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 客户联系人表
 * @TableName ipms_customer_linkman
 */
@TableName(value ="ipms_customer_linkman")
@Data
public class IpmsCustomerLinkman implements Serializable {
    /**
     * 联系人 id
     */
    @TableId(type = IdType.AUTO)
    private Long linkmanId;

    /**
     * 所属客户 id
     */
    private Long customerId;

    /**
     * 联系人姓名
     */
    private String linkmanName;

    /**
     * 联系人手机
     */
    private String linkmanPhone;

    /**
     * 0 - 表示女，1 - 表示男
     */
    private Integer linkmanGender;

    /**
     * 联系人生日
     */
    private Date linkmanBirth;

    /**
     * 联系人 QQ
     */
    private String linkmanQq;

    /**
     * 联系人微信
     */
    private String linkmanWechat;

    /**
     * 联系人邮箱
     */
    private String linkmanEmail;

    /**
     * 联系人行政区划
     */
    private String linkmanAdminDivision;

    /**
     * 联系人详细地址
     */
    private String linkmanDetailAddress;

    /**
     * 首要联系人（0 - 否，1 - 是）
     */
    private Integer linkmanIsMain;

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