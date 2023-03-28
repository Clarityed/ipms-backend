package com.clarity.ipmsbackend.model.dto.inventorybill.otherreceiptorder;

import com.clarity.ipmsbackend.model.dto.inventorybill.otherreceiptorder.productnum.UpdateOtherReceiptOrderProductNumRequest;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 更新其他入库单请求封装类
 *
 * @author: clarity
 * @date: 2023年03月28日 9:18
 */

@ApiModel("更新其他入库单请求封装类")
@Data
public class UpdateOtherReceiptOrderRequest implements Serializable {

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
     * 供应商 id
     */
    @ApiModelProperty("供应商 id")
    private Long supplierId;

    /**
     * 供应商联系人 id
     */
    @ApiModelProperty("供应商联系人 id")
    private Long supplierLinkmanId;

    /**
     * 客户 id
     */
    @ApiModelProperty("客户 id")
    private Long customerId;

    /**
     * 客户联系人 id
     */
    @ApiModelProperty("客户联系人 id")
    private Long customerLinkmanId;

    /**
     * 职员 id（其他入库单和其他出库单中称为业务员）
     */
    @ApiModelProperty("职员 id（其他入库单和其他出库单中称为业务员）")
    private Long employeeId;

    /**
     * 部门 id
     */
    @ApiModelProperty("部门 id")
    private Long departmentId;

    /**
     * 库存单据业务类型（在其他入库单中是其他入库）
     */
    @ApiModelProperty("库存单据业务类型（在其他入库单中是其他入库）")
    private String inventoryBillBusinessType;

    /**
     * 库存单据备注
     */
    @ApiModelProperty("库存单据备注")
    private String inventoryBillRemark;

    /**
     * 更新其他入库单商品请求列表
     */
    @ApiModelProperty("更新其他入库单商品请求列表")
    private List<UpdateOtherReceiptOrderProductNumRequest> updateOtherReceiptOrderProductNumRequestList;

    private static final long serialVersionUID = 1L;
}
