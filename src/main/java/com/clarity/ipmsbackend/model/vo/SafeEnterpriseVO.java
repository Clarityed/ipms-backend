package com.clarity.ipmsbackend.model.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 安全企业响应封装类
 *
 * @author: clarity
 * @date: 2023年02月20日 15:09
 */

@ApiModel("安全企业响应封装类")
@Data
public class SafeEnterpriseVO implements Serializable {
    /**
     * 企业 id
     */
    @ApiModelProperty("企业 id")
    private Long enterpriseId;

    /**
     * 企业编号
     */
    @ApiModelProperty("企业编号")
    private String enterpriseCode;

    /**
     * 企业名称
     */
    @ApiModelProperty("企业名称")
    private String enterpriseName;

    /**
     * 企业资产
     */
    @ApiModelProperty("企业资产")
    private BigDecimal enterpriseAsset;

    /**
     * 创建时间
     */
    @ApiModelProperty("用户编号")
    private Date createTime;

    /**
     * 修改时间
     */
    @ApiModelProperty("修改时间")
    private Date updateTime;

    private static final long serialVersionUID = 1L;
}
