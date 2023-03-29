package com.clarity.ipmsbackend.model.dto.inventorybill.warehousetransferorder;

import com.clarity.ipmsbackend.model.dto.inventorybill.warehousetransferorder.productnum.UpdateWarehouseTransferOrderProductNumRequest;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 修改移仓单请求封装类
 *
 * @author: clarity
 * @date: 2023年03月28日 9:18
 */

@ApiModel("修改移仓单请求封装类")
@Data
public class UpdateWarehouseTransferOrderRequest implements Serializable {

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
    @ApiModelProperty("职员 id（在移仓单中称为经办人）")
    private Long employeeId;

    /**
     * 部门 id（在移仓单称为调出部门 id）
     */
    @ApiModelProperty("部门 id（在移仓单称为调出部门 id）")
    private Long departmentId;

    /**
     * 调入部门 id（只在移仓单、调拨入库单和调拨出库单中有该字段）
     */
    @ApiModelProperty("调入部门 id")
    private Long transferDepartmentId;

    /**
     * 库存单据备注
     */
    @ApiModelProperty("库存单据备注")
    private String inventoryBillRemark;

    /**
     * 更新移仓单商品请求列表
     */
    @ApiModelProperty("更新移仓单商品请求列表")
    private List<UpdateWarehouseTransferOrderProductNumRequest> updateWarehouseTransferOrderProductNumRequestList;

    private static final long serialVersionUID = 1L;
}
