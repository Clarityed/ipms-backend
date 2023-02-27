package com.clarity.ipmsbackend.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.clarity.ipmsbackend.common.ErrorCode;
import com.clarity.ipmsbackend.exception.BusinessException;
import com.clarity.ipmsbackend.mapper.IpmsSupplierLinkmanMapper;
import com.clarity.ipmsbackend.model.dto.supplier.AddSupplierLinkmanRequest;
import com.clarity.ipmsbackend.model.dto.supplier.UpdateSupplierLinkmanRequest;
import com.clarity.ipmsbackend.model.entity.IpmsSupplierLinkman;
import com.clarity.ipmsbackend.service.IpmsSupplierLinkmanService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;

/**
* @author Clarity
* @description 针对表【ipms_supplier_linkman(供应商联系人表)】的数据库操作Service实现
* @createDate 2023-02-27 10:06:27
*/
@Service
public class IpmsSupplierLinkmanServiceImpl extends ServiceImpl<IpmsSupplierLinkmanMapper, IpmsSupplierLinkman>
    implements IpmsSupplierLinkmanService{

    @Resource
    private IpmsSupplierLinkmanMapper ipmsSupplierLinkmanMapper;

    @Override
    public int addSupplierLinkman(AddSupplierLinkmanRequest addSupplierLinkmanRequest, long supplierId) {
        // 如果有需要这里可继续参数校验
        if (supplierId <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "供应商 id 不合法");
        }
        IpmsSupplierLinkman supplierLinkman = new IpmsSupplierLinkman();
        BeanUtils.copyProperties(addSupplierLinkmanRequest, supplierLinkman);
        // 供应商联系人表必须有供应商 id
        supplierLinkman.setSupplierId(supplierId);
        supplierLinkman.setCreateTime(new Date());
        supplierLinkman.setUpdateTime(new Date());
        int result = ipmsSupplierLinkmanMapper.insert(supplierLinkman);
        if (result != 1) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR);
        }
        return result;
    }

    @Override
    public int deleteSupplierLinkmanById(long supplierId) {
        if (supplierId <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "供应商 id 不合法");
        }
        QueryWrapper<IpmsSupplierLinkman> supplierLinkmanQueryWrapper = new QueryWrapper<>();
        supplierLinkmanQueryWrapper.eq("supplier_id", supplierId);
        int result = ipmsSupplierLinkmanMapper.delete(supplierLinkmanQueryWrapper);
        if (result <= 0) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR);
        }
        return result;
    }

    @Override
    public int updateSupplierLinkman(UpdateSupplierLinkmanRequest updateSupplierLinkmanRequest, long supplierId) {
        Long linkmanId = updateSupplierLinkmanRequest.getLinkmanId();
        if (linkmanId == null || linkmanId <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "联系人 id 不存在或者 id 不合法");
        }
        // 如果供应商联系表不存在该条客户信息，则表面是修改的时候增加的信息
        QueryWrapper<IpmsSupplierLinkman> supplierLinkmanQueryWrapper = new QueryWrapper<>();
        supplierLinkmanQueryWrapper.eq("supplier_id", supplierId);
        supplierLinkmanQueryWrapper.eq("linkman_id", linkmanId);
        IpmsSupplierLinkman oldSupplierLinkman = ipmsSupplierLinkmanMapper.selectOne(supplierLinkmanQueryWrapper);
        if (oldSupplierLinkman == null) {
            // 这条记录等于新增记录
            AddSupplierLinkmanRequest addSupplierLinkmanRequest = new AddSupplierLinkmanRequest();
            updateSupplierLinkmanRequest.setLinkmanId(null);
            BeanUtils.copyProperties(updateSupplierLinkmanRequest, addSupplierLinkmanRequest);
            this.addSupplierLinkman(addSupplierLinkmanRequest, supplierId);
        } else {
            IpmsSupplierLinkman supplierLinkman = new IpmsSupplierLinkman();
            BeanUtils.copyProperties(updateSupplierLinkmanRequest, supplierLinkman);
            supplierLinkman.setUpdateTime(new Date());
            int updateCustomerLinkmanResult = ipmsSupplierLinkmanMapper.updateById(supplierLinkman);
            if (updateCustomerLinkmanResult != 1) {
                throw new BusinessException(ErrorCode.SYSTEM_ERROR);
            }
        }
        return 1;
    }
}




