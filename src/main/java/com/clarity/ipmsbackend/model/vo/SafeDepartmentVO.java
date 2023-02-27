package com.clarity.ipmsbackend.model.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * 安全部门响应封装类
 *
 * @author: clarity
 * @date: 2023年02月20日 15:09
 */

@ApiModel("安全部门响应封装类")
@Data
public class SafeDepartmentVO implements Serializable {

    /**
     * 部门 id
     */
    @ApiModelProperty("部门 id")
    private Long departmentId;

    /**
     * 部门编号
     */
    @ApiModelProperty("部门编号")
    private String departmentCode;

    /**
     * 部门名称
     */
    @ApiModelProperty("部门名称")
    private String departmentName;

    /**
     * 描述
     */
    @ApiModelProperty("描述")
    private String departmentDescription;

    /**
     * 上级部门名称
     */
    @ApiModelProperty("上级部门名称")
    private String departmentSuperName;

    private static final long serialVersionUID = 1L;
}
