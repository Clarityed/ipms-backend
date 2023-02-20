package com.clarity.ipmsbackend.model.dto.user;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * 用户登录请求信息
 *
 * @author: clarity
 * @date: 2023年02月20日 14:39
 */

@ApiModel("用户登录请求信息")
@Data
public class UserLoginRequest implements Serializable {

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

    private static final long serialVersionUID = 1L;
}
