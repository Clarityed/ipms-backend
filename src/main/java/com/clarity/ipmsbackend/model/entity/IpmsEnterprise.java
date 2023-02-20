package com.clarity.ipmsbackend.model.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 *
 * 企业实体
 * @TableName ipms_enterprise
 */
@TableName(value ="ipms_enterprise")
@Data
public class IpmsEnterprise implements Serializable {
    /**
     * 企业 id
     */
    @TableId(type = IdType.AUTO)
    private Long enterpriseId;

    /**
     * 企业编号
     */
    private String enterpriseCode;

    /**
     * 企业名称
     */
    private String enterpriseName;

    /**
     * 企业资产
     */
    private BigDecimal enterpriseAsset;

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