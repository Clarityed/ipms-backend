package com.clarity.ipmsbackend.model.dto.department;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 增加部门请求封装类
 *
 * @author: clarity
 * @date: 2023年02月22日 17:05
 */

@ApiModel("增加部门请求封装类")
@Data
public class AddDepartmentRequest implements Serializable {

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
