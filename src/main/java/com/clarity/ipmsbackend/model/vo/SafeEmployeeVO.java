package com.clarity.ipmsbackend.model.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 安全职员响应封装类
 *
 * @author: clarity
 * @date: 2023年02月20日 15:09
 */

@ApiModel("安全职员响应封装类")
@Data
public class SafeEmployeeVO implements Serializable {

    /**
     * 职员 id
     */
    @ApiModelProperty("职员 id")
    private Long employeeId;

    /**
     * 职员编号
     */
    @ApiModelProperty("职员编号")
    private String employeeCode;

    /**
     * 职员名称
     */
    @ApiModelProperty("职员名称")
    private String employeeName;

    /**
     * 职员手机号
     */
    @ApiModelProperty("职员手机号")
    private String employeePhoneNum;

    /**
     * 职员生日
     */
    @ApiModelProperty("职员生日")
    private Date employeeBirthday;

    /**
     * 职员性别（0 - 表示女，1 - 表示男）
     */
    @ApiModelProperty("职员性别（0 - 表示女，1 - 表示男）")
    private Integer employeeGender;

    /**
     * 职员邮箱
     */
    @ApiModelProperty("职员邮箱")
    private String employeeEmail;

    /**
     * 职员微信号
     */
    @ApiModelProperty("职员微信号")
    private String employeeWechatNum;

    /**
     * 职员开户行
     */
    @ApiModelProperty("职员开户行")
    private String employeeOpeningBank;

    /**
     * 职员银行账号
     */
    @ApiModelProperty("职员银行账号")
    private String employeeBankAccount;

    /**
     * 职员证件类型
     */
    @ApiModelProperty("职员证件类型")
    private String employeeIdCardType;

    /**
     * 职员证件号码
     */
    @ApiModelProperty("职员证件号码")
    private String employeeIdCardNum;

    /**
     * 部门负责人（1 - 表示是，0 - 表示否）
     */
    @ApiModelProperty("部门负责人（1 - 表示是，0 - 表示否）")
    private Integer employeeIsMaster;

    /**
     * 职员薪水
     */
    @ApiModelProperty("职员薪水")
    private BigDecimal employeeSalary;

    /**
     * 职员入职日期
     */
    @ApiModelProperty("职员入职日期")
    private Date employeeEntryTime;

    /**
     * 职员离职日期
     */
    @ApiModelProperty("职员离职日期")
    private Date employeeDepartureTime;

    /**
     * 所属部门名称
     */
    @ApiModelProperty("所属部门名称")
    private String departmentName;

    private static final long serialVersionUID = 1L;
}