package com.clarity.ipmsbackend.model.dto.enterprise;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 修改企业请求体
 *
 * @author: clarity
 * @date: 2023年02月20日 19:58
 */

@ApiModel("修改企业请求体")
@Data
public class UpdateEnterpriseRequest implements Serializable {
    /**
     * 企业 id
     */
    @ApiModelProperty("企业 id，作为更新的索引")
    private Long enterpriseId;

    /**
     * 企业编号
     */
    @ApiModelProperty("企业编号，不能修改")
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

    private static final long serialVersionUID = 1L;
}
