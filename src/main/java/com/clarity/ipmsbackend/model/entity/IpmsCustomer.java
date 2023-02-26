package com.clarity.ipmsbackend.model.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 客户表
 * @TableName ipms_customer
 */
@TableName(value ="ipms_customer")
@Data
public class IpmsCustomer implements Serializable {
    /**
     * 客户 id
     */
    @TableId(type = IdType.AUTO)
    private Long customerId;

    /**
     * 客户编号
     */
    private String customerCode;

    /**
     * 客户名称
     */
    private String customerName;

    /**
     * 客户类别
     */
    private String customerType;

    /**
     * 企业应收款余额
     */
    private BigDecimal enterpriseReceiveBalance;

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