package com.clarity.ipmsbackend.service;

import com.baomidou.mybatisplus.extension.service.IService;

import com.clarity.ipmsbackend.model.dto.supplier.AddSupplierLinkmanRequest;
import com.clarity.ipmsbackend.model.dto.supplier.UpdateSupplierLinkmanRequest;
import com.clarity.ipmsbackend.model.entity.IpmsSupplierLinkman;

/**
* @author Clarity
* @description 针对表【ipms_supplier_linkman(供应商联系人表)】的数据库操作Service
* @createDate 2023-02-27 10:06:27
*/
public interface IpmsSupplierLinkmanService extends IService<IpmsSupplierLinkman> {


    /**
     * 增加供应商联系人
     *
     * @param addSupplierLinkmanRequest
     * @param supplierId
     * @return
     */
    int addSupplierLinkman(AddSupplierLinkmanRequest addSupplierLinkmanRequest, long supplierId);

    /**
     * 根据供应商 id 删除供应商联系人
     *
     * @param supplierId
     * @return
     */
    int deleteSupplierLinkmanById(long supplierId);

    /**
     * 更新供应商联系人
     *
     * @param updateSupplierLinkmanRequest
     * @param supplierId
     * @return
     */
    int updateSupplierLinkman(UpdateSupplierLinkmanRequest updateSupplierLinkmanRequest, long supplierId);
}
