package com.clarity.ipmsbackend.model.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 库存单据
 * @TableName ipms_inventory_bill
 */
@TableName(value ="ipms_inventory_bill")
@Data
public class IpmsInventoryBill implements Serializable {
    /**
     * 库存单据 id
     */
    @TableId(type = IdType.AUTO)
    private Long inventoryBillId;

    /**
     * 库存源单据 id
     */
    private Long inventorySourceBillId;

    /**
     * 库存单据编号
     */
    private String inventoryBillCode;

    /**
     * 库存单据日期
     */
    private String inventoryBillDate;

    /**
     * 供应商 id
     */
    private Long supplierId;

    /**
     * 供应商联系人 id
     */
    private Long supplierLinkmanId;

    /**
     * 客户 id
     */
    private Long customerId;

    /**
     * 客户联系人 id
     */
    private Long customerLinkmanId;

    /**
     * 职员 id（其他入库单和其他出库单中称为业务员，在移仓单、调拨出库单和调拨入库单中称为经办人）
     */
    private Long employeeId;

    /**
     * 部门 id（在移仓单、调拨出库单和调拨入库单中称为调出部门 id）
     */
    private Long departmentId;

    /**
     * 调入部门 id（只在移仓单、调拨入库单和调拨出库单中有该字段）
     */
    private Long transferDepartmentId;

    /**
     * 库存单据业务类型（在其他入库单中是其他入库，在其他出库单是其他出库，移仓单中没有该字段，调拨出库单和调拨入库单中有同价调拨和异价调拨）
     */
    private String inventoryBillBusinessType;

    /**
     * 库存单据备注
     */
    private String inventoryBillRemark;

    /**
     * 库存单据类型（其他入库单、其他出库单、移仓单、调拨出库单、调拨入库单）
     */
    private String inventoryBillType;

    /**
     * 创建者
     */
    private String founder;

    /**
     * 修改者
     */
    private String modifier;

    /**
     * 审核人
     */
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    private String checker;

    /**
     * 审核状态（默认为 0，0 - 未审核，1 - 已审核）
     */
    private Integer checkState;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 修改时间
     */
    private Date updateTime;

    /**
     * 审核时间
     */
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    private Date checkTime;

    /**
     * 逻辑删除
     */
    @TableLogic
    private Integer isDelete;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}