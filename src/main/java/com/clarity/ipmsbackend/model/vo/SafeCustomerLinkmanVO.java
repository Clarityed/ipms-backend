package com.clarity.ipmsbackend.model.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 安全客户联系人响应封装类
 *
 * @author: clarity
 * @date: 2023年02月26日 17:23
 */

@ApiModel("安全客户联系人响应封装类")
@Data
public class SafeCustomerLinkmanVO implements Serializable {

    /**
     * 联系人 id
     */
    @ApiModelProperty("联系人 id")
    private Long linkmanId;

    /**
     * 联系人姓名
     */
    @ApiModelProperty("联系人姓名")
    private String linkmanName;

    /**
     * 联系人手机
     */
    @ApiModelProperty("联系人手机")
    private String linkmanPhone;

    /**
     * 0 - 表示女，1 - 表示男
     */
    @ApiModelProperty("联系人性别（0 - 表示女，1 - 表示男）")
    private Integer linkmanGender;

    /**
     * 联系人生日
     */
    @ApiModelProperty("联系人生日")
    private String linkmanBirth;

    /**
     * 联系人 QQ
     */
    @ApiModelProperty("联系人 QQ")
    private String linkmanQq;

    /**
     * 联系人微信
     */
    @ApiModelProperty("联系人微信")
    private String linkmanWechat;

    /**
     * 联系人邮箱
     */
    @ApiModelProperty("联系人邮箱")
    private String linkmanEmail;

    /**
     * 联系人行政区划
     */
    @ApiModelProperty("联系人行政区划")
    private String linkmanAdminDivision;

    /**
     * 联系人详细地址
     */
    @ApiModelProperty("联系人详细地址")
    private String linkmanDetailAddress;

    /**
     * 首要联系人（0 - 否，1 - 是）
     */
    @ApiModelProperty("首要联系人（0 - 否，1 - 是）")
    private Integer linkmanIsMain;

    private static final long serialVersionUID = 1L;
}
