package com.clarity.ipmsbackend.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.clarity.ipmsbackend.common.FuzzyQueryRequest;
import com.clarity.ipmsbackend.model.dto.customer.AddCustomerRequest;
import com.clarity.ipmsbackend.model.dto.customer.UpdateCustomerRequest;
import com.clarity.ipmsbackend.model.entity.IpmsCustomer;
import com.clarity.ipmsbackend.model.vo.SafeCustomerVO;

import javax.servlet.http.HttpServletRequest;

/**
* @author Clarity
* @description 针对表【ipms_customer(客户表)】的数据库操作Service
* @createDate 2023-02-25 16:49:42
*/
public interface IpmsCustomerService extends IService<IpmsCustomer> {

    /**
     * 部门编号自动生成
     *
     * @return
     */
    String customerCodeAutoGenerate();

    /**
     * 增加客户
     *
     * @param addCustomerRequest
     * @return
     */
    int addCustomer(AddCustomerRequest addCustomerRequest);

    /**
     * 根据 id 删除客户
     *
     * @param id
     * @return
     */
    int deleteCustomerById(long id);

    /**
     * 更新客户
     *
     * @param updateCustomerRequest
     * @return
     */
    int updateCustomer(UpdateCustomerRequest updateCustomerRequest);

    /**
     * 分页查询客户，且数据脱敏，且支持模糊查询
     *
     * @param fuzzyQueryRequest
     * @param request
     * @return
     */
    Page<SafeCustomerVO> pagingFuzzyQuery(FuzzyQueryRequest fuzzyQueryRequest, HttpServletRequest request);

    /**
     * 实现客户金额的增加
     *
     * @param customerId
     * @param enterpriseReceiveBalance
     * @return
     */
    int addEnterpriseReceiveBalance(long customerId, double enterpriseReceiveBalance);

    /**
     * 实现客户金额的减少
     *
     * @param customerId
     * @param enterpriseReceiveBalance
     * @return
     */
    int reduceEnterpriseReceiveBalance(long customerId, double enterpriseReceiveBalance);
}
