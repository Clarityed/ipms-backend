package com.clarity.ipmsbackend.model.dto.user;

import com.clarity.ipmsbackend.common.PageRequest;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 用户查询封装类
 *
 * @author: clarity
 * @date: 2023年02月22日 9:40
 */

@EqualsAndHashCode(callSuper = true)
@ApiModel("用户查询封装类")
@Data
public class UserQueryRequest extends PageRequest {

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
     * 用户角色
     */
    @ApiModelProperty("用户角色")
    private String userRole;
}
