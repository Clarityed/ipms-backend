package com.clarity.ipmsbackend.model.vo.inventorybill.otherdeliveryorder;

import com.clarity.ipmsbackend.model.vo.inventorybill.otherdeliveryorder.productnum.SafeOtherDeliveryOrderProductNumVO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 安全其他出库单响应封装类
 *
 * @author: clarity
 * @date: 2023年03月28日 18:10
 */

@ApiModel("安全其他出库单响应封装类")
@Data
public class SafeOtherDeliveryOrderVO implements Serializable {

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
     * 客户 id
     */
    @ApiModelProperty("客户 id")
    private Long customerId;

    /**
     * 客户姓名
     */
    @ApiModelProperty("客户姓名")
    private String customerName;

    /**
     * 客户联系人 id
     */
    @ApiModelProperty("客户联系人 id")
    private Long customerLinkmanId;

    /**
     * 客户联系人行政区划
     */
    @ApiModelProperty("客户联系人行政区划")
    private String customerLinkmanAdminDivision;

    /**
     * 客户联系人详细地址
     */
    @ApiModelProperty("客户联系人详细地址")
    private String customerLinkmanDetailAddress;

    /**
     * 职员 id（其他入库单和其他出库单中称为业务员）
     */
    @ApiModelProperty("职员 id（其他入库单和其他出库单中称为业务员）")
    private Long employeeId;

    /**
     * 职员姓名（其他入库单和其他出库单中称为业务员）
     */
    @ApiModelProperty("职员姓名（其他入库单和其他出库单中称为业务员）")
    private String employeeName;

    /**
     * 部门 id
     */
    @ApiModelProperty("部门 id")
    private Long departmentId;

    /**
     * 部门名称
     */
    @ApiModelProperty("部门名称")
    private String departmentName;

    /**
     * 库存单据业务类型（在其他入库单中是其他入库，在其他出库单是其他出库，移仓单中没有该字段，调拨出库单和调拨入库单中有同价调拨和异价调拨）
     */
    @ApiModelProperty("库存单据业务类型（在其他入库单中是其他入库，在其他出库单是其他出库，移仓单中没有该字段，调拨出库单和调拨入库单中有同价调拨和异价调拨）")
    private String inventoryBillBusinessType;

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
     * 安全其他入库单商品列表
     */
    @ApiModelProperty("安全其他入库单商品列表")
    private List<SafeOtherDeliveryOrderProductNumVO> safeOtherDeliveryOrderProductNumVOList;

    private static final long serialVersionUID = 1L;
}
