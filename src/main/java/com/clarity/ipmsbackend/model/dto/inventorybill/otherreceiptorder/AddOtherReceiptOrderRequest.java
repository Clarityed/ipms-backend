package com.clarity.ipmsbackend.model.dto.inventorybill.otherreceiptorder;

import com.clarity.ipmsbackend.model.dto.inventorybill.otherreceiptorder.productnum.AddOtherReceiptOrderProductNumRequest;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 增加其他入库单请求封装类
 *
 * @author: clarity
 * @date: 2023年03月28日 9:18
 */

@ApiModel("增加其他入库单请求封装类")
@Data
public class AddOtherReceiptOrderRequest implements Serializable {

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
     * 职员 id（业务员 id）
     */
    @ApiModelProperty("职员 id（业务员 id）")
    private Long employeeId;

    /**
     * 部门 id
     */
    @ApiModelProperty("部门 id")
    private Long departmentId;

    /**
     * 库存单据业务类型（现在只有一种类型其他入库）
     */
    @ApiModelProperty("库存单据业务类型（现在只有一种类型其他入库）以下拉框呈现")
    private String inventoryBillBusinessType;

    /**
     * 库存单据备注
     */
    @ApiModelProperty("库存单据备注")
    private String inventoryBillRemark;

    /**
     * 库存单据类型（其他入库单、只能是其他入库单）
     */
    @ApiModelProperty("库存单据类型（其他入库单、只能是其他入库单）")
    private String inventoryBillType;

    /**
     * 增加其他入库单商品请求列表
     */
    @ApiModelProperty("增加其他入库单商品请求列表")
    private List<AddOtherReceiptOrderProductNumRequest> addOtherReceiptOrderProductNumRequestList;

    private static final long serialVersionUID = 1L;
}
