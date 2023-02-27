package com.clarity.ipmsbackend.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.clarity.ipmsbackend.common.ErrorCode;
import com.clarity.ipmsbackend.exception.BusinessException;
import com.clarity.ipmsbackend.mapper.IpmsCustomerLinkmanMapper;
import com.clarity.ipmsbackend.model.dto.customer.AddCustomerLinkmanRequest;
import com.clarity.ipmsbackend.model.dto.customer.UpdateCustomerLinkmanRequest;
import com.clarity.ipmsbackend.model.entity.IpmsCustomerLinkman;
import com.clarity.ipmsbackend.service.IpmsCustomerLinkmanService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;

/**
* @author Clarity
* @description 针对表【ipms_customer_linkman(客户联系人表)】的数据库操作Service实现
* @createDate 2023-02-25 16:53:32
*/
@Service
public class IpmsCustomerLinkmanServiceImpl extends ServiceImpl<IpmsCustomerLinkmanMapper, IpmsCustomerLinkman>
    implements IpmsCustomerLinkmanService{

    @Resource
    private IpmsCustomerLinkmanMapper ipmsCustomerLinkmanMapper;

    @Override
    public int addCustomerLinkman(AddCustomerLinkmanRequest addCustomerLinkmanRequest, long customerId) {
        // 如果有需要这里可继续参数校验
        if (customerId <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "客户 id 不合法");
        }
        IpmsCustomerLinkman customerLinkman = new IpmsCustomerLinkman();
        BeanUtils.copyProperties(addCustomerLinkmanRequest, customerLinkman);
        // 客户联系人表必须有客户 id
        customerLinkman.setCustomerId(customerId);
        customerLinkman.setCreateTime(new Date());
        customerLinkman.setUpdateTime(new Date());
        int result = ipmsCustomerLinkmanMapper.insert(customerLinkman);
        if (result != 1) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR);
        }
        return result;
    }

    @Override
    public int deleteCustomerLinkmanById(long customerId) {
        if (customerId <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "客户 id 不合法");
        }
        QueryWrapper<IpmsCustomerLinkman> customerLinkmanQueryWrapper = new QueryWrapper<>();
        customerLinkmanQueryWrapper.eq("customer_id", customerId);
        int result = ipmsCustomerLinkmanMapper.delete(customerLinkmanQueryWrapper);
        if (result <= 0) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR);
        }
        return result;
    }

    @Override
    public int updateCustomerLinkman(UpdateCustomerLinkmanRequest updateCustomerLinkmanRequest, long customerId) {
        Long linkmanId = updateCustomerLinkmanRequest.getLinkmanId();
        if (linkmanId == null || linkmanId <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "联系人 id 不存在或者 id 不合法");
        }
        // 如果客户联系表不存在该条客户信息，则表面是修改的时候增加的信息
        QueryWrapper<IpmsCustomerLinkman> customerLinkmanQueryWrapper = new QueryWrapper<>();
        customerLinkmanQueryWrapper.eq("customer_id", customerId);
        customerLinkmanQueryWrapper.eq("linkman_id", linkmanId);
        IpmsCustomerLinkman oldCustomerLinkman = ipmsCustomerLinkmanMapper.selectOne(customerLinkmanQueryWrapper);
        if (oldCustomerLinkman == null) {
            // 这条记录等于新增记录
            AddCustomerLinkmanRequest addCustomerLinkmanRequest = new AddCustomerLinkmanRequest();
            updateCustomerLinkmanRequest.setLinkmanId(null);
            BeanUtils.copyProperties(updateCustomerLinkmanRequest, addCustomerLinkmanRequest);
            this.addCustomerLinkman(addCustomerLinkmanRequest, customerId);
        } else {
            IpmsCustomerLinkman customerLinkman = new IpmsCustomerLinkman();
            BeanUtils.copyProperties(updateCustomerLinkmanRequest, customerLinkman);
            customerLinkman.setUpdateTime(new Date());
            int updateCustomerLinkmanResult = ipmsCustomerLinkmanMapper.updateById(customerLinkman);
            if (updateCustomerLinkmanResult != 1) {
                throw new BusinessException(ErrorCode.SYSTEM_ERROR);
            }
        }
        return 1;
    }
}




