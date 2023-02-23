package com.clarity.ipmsbackend.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 
 * @TableName ipms_employee
 */
@TableName(value ="ipms_employee")
@Data
public class IpmsEmployee implements Serializable {
    /**
     * 职员 id
     */
    @TableId(type = IdType.AUTO)
    private Long employeeId;

    /**
     * 职员编号
     */
    private String employeeCode;

    /**
     * 职员名称
     */
    private String employeeName;

    /**
     * 职员手机号
     */
    private String employeePhoneNum;

    /**
     * 职员生日
     */
    private Date employeeBirthday;

    /**
     * 职员性别（0 - 表示女，1 - 表示男）
     */
    private Integer employeeGender;

    /**
     * 职员邮箱
     */
    private String employeeEmail;

    /**
     * 职员微信号
     */
    private String employeeWechatNum;

    /**
     * 职员开户行
     */
    private String employeeOpeningBank;

    /**
     * 职员银行账号
     */
    private String employeeBankAccount;

    /**
     * 职员证件类型
     */
    private String employeeIdCardType;

    /**
     * 职员证件号码
     */
    private String employeeIdCardNum;

    /**
     * 部门负责人（1 - 表示是，0 - 表示否）
     */
    private Integer employeeIsMaster;

    /**
     * 职员薪水
     */
    private BigDecimal employeeSalary;

    /**
     * 职员入职日期
     */
    private Date employeeEntryTime;

    /**
     * 职员离职日期
     */
    private Date employeeDepartureTime;

    /**
     * 所属部门 id
     */
    private Long departmentId;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 修改时间
     */
    private Date updateTime;

    /**
     * 逻辑删除
     */
    @TableField
    private Integer isDelete;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}