package com.clarity.ipmsbackend.model.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 
 * @TableName ipms_bom
 */
@TableName(value ="ipms_bom")
@Data
public class IpmsBom implements Serializable {
    /**
     * BOM id
     */
    @TableId(type = IdType.AUTO)
    private Long bomId;

    /**
     * BOM 等级
     */
    private Integer bomLevel;

    /**
     * BOM 编号
     */
    private String bomCode;

    /**
     * BOM 备注
     */
    private String bomRemark;

    /**
     * BOM 分类 id
     */
    private Long bomClassId;

    /**
     * 创建者
     */
    private String founder;

    /**
     * 修改者
     */
    private String modifier;

    /**
     * 审核状态（默认为 0，0 - 未审核，1 - 已审核）
     */
    private Integer checkState;

    /**
     * 审核人
     */
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    private String checker;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 修改时间
     */
    private Date updateTime;

    /**
     * 审核时间
     */
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    private Date checkTime;

    /**
     * 逻辑删除
     */
    @TableLogic
    private Integer isDelete;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}