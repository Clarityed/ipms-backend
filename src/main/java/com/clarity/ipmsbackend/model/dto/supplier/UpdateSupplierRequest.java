package com.clarity.ipmsbackend.model.dto.supplier;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 更新供应商请求封装类
 *
 * @author: clarity
 * @date: 2023年02月27日 10:14
 */

@ApiModel("更新供应商请求封装类")
@Data
public class UpdateSupplierRequest implements Serializable {

    /**
     * 供应商 id
     */
    @ApiModelProperty("供应商 id，作为更新的索引")
    private Long supplierId;

    /**
     * 供应商编号
     */
    @ApiModelProperty("供应商编号，不能修改")
    private String supplierCode;

    /**
     * 供应商名称
     */
    @ApiModelProperty("供应商名称")
    private String supplierName;

    /**
     * 供应商类别
     */
    @ApiModelProperty("供应商类别")
    private String supplierType;

    /**
     * 更新供应商联系人请求列表
     */
    @ApiModelProperty("增加供应商联系人请求列表")
    private List<UpdateSupplierLinkmanRequest> updateSupplierLinkmanRequestList;

    private static final long serialVersionUID = 1L;
}
