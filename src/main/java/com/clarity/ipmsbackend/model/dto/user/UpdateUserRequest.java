package com.clarity.ipmsbackend.model.dto.user;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * 修改用户请求封装类
 *
 * @author: clarity
 * @date: 2023年02月20日 19:58
 */

@ApiModel("修改用户请求封装类")
@Data
public class UpdateUserRequest implements Serializable {

    /**
     * 用户 id
     */
    @ApiModelProperty("用户 id")
    private Long userId;

    /**
     * 用户编号
     */
    @ApiModelProperty("用户编号")
    private String userCode;

    /**
     * 用户姓名
     */
    @ApiModelProperty("用户姓名")
    private String userName;

    /**
     * 用户性别（0 - 表示女，1 - 表示男）
     */
    @ApiModelProperty("用户性别（0 - 表示女，1 - 表示男）")
    private Integer userGender;

    /**
     * 用户账号
     */
    @ApiModelProperty("用户账号")
    private String userAccount;

    /**
     * 用户密码
     */
    @ApiModelProperty("用户密码")
    private String userPassword;

    /**
     * 用户角色
     */
    @ApiModelProperty("用户角色（开发员, 开发主管, 销售员, 销售主管, 生产员, 生产主管, 采购员, 采购主管, 仓管员, 仓管主管）")
    private String userRole;

    private static final long serialVersionUID = 1L;
}
