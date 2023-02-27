package com.clarity.ipmsbackend.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 供应商表
 * @TableName ipms_supplier
 */
@TableName(value ="ipms_supplier")
@Data
public class IpmsSupplier implements Serializable {
    /**
     * 供应商 id
     */
    @TableId(type = IdType.AUTO)
    private Long supplierId;

    /**
     * 供应商编号
     */
    private String supplierCode;

    /**
     * 供应商名称
     */
    private String supplierName;

    /**
     * 供应商类别
     */
    private String supplierType;

    /**
     * 企业应付款余额
     */
    private BigDecimal enterprisePayBalance;

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