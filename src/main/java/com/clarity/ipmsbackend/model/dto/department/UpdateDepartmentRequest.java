package com.clarity.ipmsbackend.model.dto.department;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * 更新部门请求封装类
 *
 * @author: clarity
 * @date: 2023年02月22日 17:05
 */

@ApiModel("更新部门请求封装类")
@Data
public class UpdateDepartmentRequest implements Serializable {

    /**
     * 部门 id
     */
    @ApiModelProperty("部门 id，作为更新的索引")
    private Long departmentId;

    /**
     * 部门编号
     */
    @ApiModelProperty("部门编号，不能修改")
    private String departmentCode;

    /**
     * 部门名称
     */
    @ApiModelProperty("部门名称")
    private String departmentName;

    /**
     * 上级部门 id（可以为空）
     */
    @ApiModelProperty("上级部门 id（可以为空）")
    private Long departmentSuper;

    /**
     * 描述（可以为空）
     */
    @ApiModelProperty("描述（可以为空）")
    private String departmentDescription;

    private static final long serialVersionUID = 1L;
}
