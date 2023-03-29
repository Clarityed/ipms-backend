package com.clarity.ipmsbackend.model.vo.inventorybill.warehousetransferorder;

import com.clarity.ipmsbackend.model.vo.inventorybill.warehousetransferorder.productnum.SafeWarehouseTransferOrderProductNumVO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 安全移仓单响应封装类
 *
 * @author: clarity
 * @date: 2023年03月28日 18:10
 */

@ApiModel("安全移仓单响应封装类")
@Data
public class SafeWarehouseTransferOrderVO implements Serializable {

    /**
     * 库存单据 id
     */
    @ApiModelProperty("库存单据 id")
    private Long inventoryBillId;

    /**
     * 库存单据编号
     */
    @ApiModelProperty("库存单据编号")
    private String inventoryBillCode;

    /**
     * 库存单据日期
     */
    @ApiModelProperty("库存单据日期")
    private String inventoryBillDate;

    /**
     * 职员 id（在移仓单中称为经办人）
     */
    @ApiModelProperty("经办人 id（职员 id）")
    private Long employeeId;

    /**
     * 职员姓名（在移仓单中称为经办人）
     */
    @ApiModelProperty("经办人姓名")
    private String employeeName;

    /**
     * 部门 id（在移仓单称为调出部门 id）
     */
    @ApiModelProperty("调出部门 id（部门 id）")
    private Long departmentId;

    /**
     * 调出部门名称（在移仓单称为调出部门名称）
     */
    @ApiModelProperty("调出部门名称")
    private String departmentName;

    /**
     * 调入部门 id（只在移仓单、调拨入库单和调拨出库单中有该字段）
     */
    @ApiModelProperty("调入部门 id（部门 id）")
    private Long transferDepartmentId;

    /**
     * 调入部门名称（只在移仓单、调拨入库单和调拨出库单中有该字段）
     */
    @ApiModelProperty("调入部门名称")
    private String transferDepartmentName;

    /**
     * 库存单据备注
     */
    @ApiModelProperty("库存单据备注")
    private String inventoryBillRemark;

    /**
     * 库存单据类型（其他入库单、其他出库单、移仓单、调拨出库单、调拨入库单）
     */
    @ApiModelProperty("库存单据类型（其他入库单、其他出库单、移仓单、调拨出库单、调拨入库单）")
    private String inventoryBillType;

    /**
     * 创建者
     */
    @ApiModelProperty("创建者")
    private String founder;

    /**
     * 修改者
     */
    @ApiModelProperty("修改者")
    private String modifier;

    /**
     * 审核人
     */
    @ApiModelProperty("审核人")
    private String checker;

    /**
     * 审核状态（默认为 0，0 - 未审核，1 - 已审核）
     */
    @ApiModelProperty("审核状态（默认为 0，0 - 未审核，1 - 已审核）")
    private Integer checkState;

    /**
     * 创建时间
     */
    @ApiModelProperty("创建时间")
    private String createTime;

    /**
     * 修改时间
     */
    @ApiModelProperty("修改时间")
    private String updateTime;

    /**
     * 审核时间
     */
    @ApiModelProperty("审核时间")
    private String checkTime;

    /**
     * 安全移仓单商品响应封装类
     */
    @ApiModelProperty("安全移仓单商品响应封装类")
    private List<SafeWarehouseTransferOrderProductNumVO> safeWarehouseTransferOrderProductNumVOList;

    private static final long serialVersionUID = 1L;
}
