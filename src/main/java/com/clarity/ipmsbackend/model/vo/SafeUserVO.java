package com.clarity.ipmsbackend.model.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

/**
 * 安全用户响应体（脱敏后的用户信息）
 *
 * @author: clarity
 * @date: 2023年02月20日 15:09
 */

@ApiModel("安全用户响应体（脱敏后的用户信息）")
@Data
public class SafeUserVO {
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

    /**
     * 所属企业 id
     */
    @ApiModelProperty("所属企业 id")
    private Long enterpriseId;

    /**
     * 创建时间
     */
    @ApiModelProperty("创建时间")
    private Date createTime;

    /**
     * 修改时间
     */
    @ApiModelProperty("修改时间")
    private Date updateTime;

    private static final long serialVersionUID = 1L;
}
