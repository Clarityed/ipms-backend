package com.clarity.ipmsbackend.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.clarity.ipmsbackend.model.dto.customer.linkman.AddCustomerLinkmanRequest;
import com.clarity.ipmsbackend.model.dto.customer.linkman.UpdateCustomerLinkmanRequest;
import com.clarity.ipmsbackend.model.entity.IpmsCustomerLinkman;

/**
* @author Clarity
* @description 针对表【ipms_customer_linkman(客户联系人表)】的数据库操作Service
* @createDate 2023-02-25 16:53:32
*/
public interface IpmsCustomerLinkmanService extends IService<IpmsCustomerLinkman> {

    /**
     * 增加客户联系人
     *
     * @param addCustomerLinkmanRequest
     * @param customerId
     * @return
     */
    int addCustomerLinkman(AddCustomerLinkmanRequest addCustomerLinkmanRequest, long customerId);

    /**
     * 根据客户 id 删除客户联系人
     *
     * @param customerId
     * @return
     */
    int deleteCustomerLinkmanById(long customerId);

    /**
     * 更新客户联系人
     *
     * @param updateCustomerLinkmanRequest
     * @param customerId
     * @return
     */
    int updateCustomerLinkman(UpdateCustomerLinkmanRequest updateCustomerLinkmanRequest, long customerId);


}
