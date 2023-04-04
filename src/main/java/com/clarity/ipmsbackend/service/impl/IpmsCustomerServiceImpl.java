package com.clarity.ipmsbackend.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.plugins.pagination.PageDTO;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.clarity.ipmsbackend.common.ErrorCode;
import com.clarity.ipmsbackend.common.FuzzyQueryRequest;
import com.clarity.ipmsbackend.exception.BusinessException;
import com.clarity.ipmsbackend.mapper.IpmsCustomerMapper;
import com.clarity.ipmsbackend.model.dto.customer.AddCustomerRequest;
import com.clarity.ipmsbackend.model.dto.customer.UpdateCustomerRequest;
import com.clarity.ipmsbackend.model.dto.customer.linkman.AddCustomerLinkmanRequest;
import com.clarity.ipmsbackend.model.dto.customer.linkman.UpdateCustomerLinkmanRequest;
import com.clarity.ipmsbackend.model.entity.IpmsCustomer;
import com.clarity.ipmsbackend.model.entity.IpmsCustomerLinkman;
import com.clarity.ipmsbackend.model.entity.IpmsInventoryBill;
import com.clarity.ipmsbackend.model.entity.IpmsSaleBill;
import com.clarity.ipmsbackend.model.vo.SafeCustomerLinkmanVO;
import com.clarity.ipmsbackend.model.vo.SafeCustomerVO;
import com.clarity.ipmsbackend.model.vo.SafeUserVO;
import com.clarity.ipmsbackend.service.*;
import com.clarity.ipmsbackend.utils.CodeAutoGenerator;
import com.clarity.ipmsbackend.utils.TimeFormatUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
* @author Clarity
* @description 针对表【ipms_customer(客户表)】的数据库操作Service实现
* @createDate 2023-02-25 16:49:42
*/

@Service
@Slf4j
public class IpmsCustomerServiceImpl extends ServiceImpl<IpmsCustomerMapper, IpmsCustomer>
    implements IpmsCustomerService{

    @Resource
    private IpmsCustomerMapper ipmsCustomerMapper;

    @Resource
    private IpmsCustomerLinkmanService ipmsCustomerLinkmanService;

    @Resource
    private IpmsUserService ipmsUserService;

    @Resource
    private IpmsSaleBillService ipmsSaleBillService;

    @Resource
    private IpmsInventoryBillService ipmsInventoryBillService;

    @Override
    public String customerCodeAutoGenerate() {
        QueryWrapper<IpmsCustomer> ipmsCustomerQueryWrapper = new QueryWrapper<>();
        List<IpmsCustomer> ipmsCustomerList = ipmsCustomerMapper.selectList(ipmsCustomerQueryWrapper);
        String customerCode;
        if (ipmsCustomerList.size() == 0) {
            customerCode = "GK00000";
        } else {
            IpmsCustomer lastCustomer = ipmsCustomerList.get(ipmsCustomerList.size() - 1);
            customerCode = lastCustomer.getCustomerCode();
        }
        String nextCustomerCode = null;
        try {
            nextCustomerCode = CodeAutoGenerator.generatorCode(customerCode);
        } catch (Exception e) {
            log.info("编码自动生成器异常");
        }
        return nextCustomerCode;
    }

    @Override
    @Transactional // 事务注解
    public int addCustomer(AddCustomerRequest addCustomerRequest) {
        // 1. 校验参数是否为空
        String customerName = addCustomerRequest.getCustomerName();
        String customerCode = addCustomerRequest.getCustomerCode();
        if (StringUtils.isAnyBlank(customerCode, customerName)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "客户姓名或者客户编号为空");
        }
        // 2. 至少要插入一条联系人信息（调用插入联系人方法）
        List<AddCustomerLinkmanRequest> addCustomerLinkmanRequestList = addCustomerRequest.getAddCustomerLinkmanRequestList();
        if (addCustomerLinkmanRequestList == null || addCustomerLinkmanRequestList.size() == 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "联系人不存在或者联系人为空");
        }
        // 3. 编号不能重复
        QueryWrapper<IpmsCustomer> customerQueryWrapper = new QueryWrapper<>();
        customerQueryWrapper.eq("customer_code", customerCode);
        IpmsCustomer ipmsCustomer = ipmsCustomerMapper.selectOne(customerQueryWrapper);
        if (ipmsCustomer != null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "编码重复");
        }
        // 4. 插入数据到客户表
        IpmsCustomer customer = new IpmsCustomer();
        BeanUtils.copyProperties(addCustomerRequest, customer);
        customer.setCreateTime(new Date());
        customer.setUpdateTime(new Date());
        int customerAddResult = ipmsCustomerMapper.insert(customer);
        if (customerAddResult != 1) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR);
        }
        // 5. 调用插入联系人方法将列表中的信息插入客户联系人表
        int customerLinkmanAddResult = 0;
        for (AddCustomerLinkmanRequest addCustomerLinkmanRequest : addCustomerLinkmanRequestList) {
            customerLinkmanAddResult = ipmsCustomerLinkmanService.addCustomerLinkman(addCustomerLinkmanRequest, customer.getCustomerId());
        }
        return customerLinkmanAddResult;
    }

    @Override
    @Transactional // 开启事务
    public int deleteCustomerById(long id) {
        //  1. 校验 id
        if (id <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "id 不存在或者 id 不合法");
        }
        // 如果客户有订单无法删除客户，订单关联了客户的信息，存储了客户的 id，所以客户不能删除，否则查询订单会报错
        QueryWrapper<IpmsSaleBill> saleBillQueryWrapper = new QueryWrapper<>();
        saleBillQueryWrapper.eq("customer_id", id);
        List<IpmsSaleBill> validAsSaleBill = ipmsSaleBillService.list(saleBillQueryWrapper);
        if (validAsSaleBill != null && validAsSaleBill.size() > 0) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "客户已被使用");
        }
        QueryWrapper<IpmsInventoryBill> ipmsInventoryBillQueryWrapper = new QueryWrapper<>();
        ipmsInventoryBillQueryWrapper.eq("customer_id", id);
        List<IpmsInventoryBill> validAsInventoryBill = ipmsInventoryBillService.list(ipmsInventoryBillQueryWrapper);
        if (validAsInventoryBill != null && validAsInventoryBill.size() > 0) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "客户已被使用");
        }
        // 2. 删除客户信息
        int customerDeleteResult = ipmsCustomerMapper.deleteById(id);
        if (customerDeleteResult != 1) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR);
        }
        // 3. 删除客户联系人信息
        return ipmsCustomerLinkmanService.deleteCustomerLinkmanById(id);
    }

    @Override
    @Transactional // 开启事务
    public int updateCustomer(UpdateCustomerRequest updateCustomerRequest) {
        // 1. 校验参数
        Long customerId = updateCustomerRequest.getCustomerId();
        if (customerId == null || customerId <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "id 不存在或者 id 不合法");
        }
        List<UpdateCustomerLinkmanRequest> updateCustomerLinkmanRequestList = updateCustomerRequest.getUpdateCustomerLinkmanRequestList();
        if (updateCustomerLinkmanRequestList == null || updateCustomerLinkmanRequestList.size() == 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "联系人信息不存在或者为空");
        }
        // 2. 判断客户是否存在
        IpmsCustomer customer = ipmsCustomerMapper.selectById(customerId);
        if (customer == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }
        // 3. 如果客户编码有传递那么客户编号必须相同，无法修改，且验证了客户是否存在
        String customerCode = updateCustomerRequest.getCustomerCode();
        if (customerCode != null) {
            QueryWrapper<IpmsCustomer> customerQueryWrapper = new QueryWrapper<>();
            customerQueryWrapper.eq("customer_id", customerId);
            IpmsCustomer oldCustomer = ipmsCustomerMapper.selectOne(customerQueryWrapper);
            if (!customerCode.equals(oldCustomer.getCustomerCode())) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "客户编号必须相同无法修改");
            }
        }
        // todo 有时间的话实现删除
        // 4. 更新数据
        //     更新客户信息
        IpmsCustomer newCustomer = new IpmsCustomer();
        BeanUtils.copyProperties(updateCustomerRequest, newCustomer);
        newCustomer.setUpdateTime(new Date());
        int updateCustomerResult = ipmsCustomerMapper.updateById(newCustomer);
        if (updateCustomerResult != 1) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR);
        }
        //     更新客户联系人信息
        int result = 0;
        for (UpdateCustomerLinkmanRequest updateCustomerLinkmanRequest : updateCustomerLinkmanRequestList) {
            result = ipmsCustomerLinkmanService.updateCustomerLinkman(updateCustomerLinkmanRequest, customerId);
        }
        return result;
    }

    @Override
    public Page<SafeCustomerVO> pagingFuzzyQuery(FuzzyQueryRequest fuzzyQueryRequest, HttpServletRequest request) {
        SafeUserVO loginUser = ipmsUserService.getLoginUser(request);
        if (loginUser == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR);
        }
        if (fuzzyQueryRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "参数为空");
        }
        Page<IpmsCustomer> page = new Page<>(fuzzyQueryRequest.getCurrentPage(), fuzzyQueryRequest.getPageSize());
        QueryWrapper<IpmsCustomer> ipmsCustomerQueryWrapper = new QueryWrapper<>();
        String fuzzyText = fuzzyQueryRequest.getFuzzyText();
        if (StringUtils.isNotBlank(fuzzyText)) {
            ipmsCustomerQueryWrapper.like("customer_id", fuzzyText).or()
                    .like("customer_code", fuzzyText).or()
                    .like("customer_name", fuzzyText).or()
                    .like("customer_type", fuzzyText).or()
                    .like("enterprise_receive_balance", fuzzyText).or();
        }
        Page<IpmsCustomer> customerPage = ipmsCustomerMapper.selectPage(page, ipmsCustomerQueryWrapper);
        List<SafeCustomerVO> safeCustomerVOList = customerPage.getRecords().stream().map(ipmsCustomer -> {
            SafeCustomerVO safeCustomerVO = new SafeCustomerVO();
            BeanUtils.copyProperties(ipmsCustomer, safeCustomerVO);
            Long customerId = ipmsCustomer.getCustomerId();
            if (customerId != null && customerId > 0) {
                QueryWrapper<IpmsCustomerLinkman> queryWrapper = new QueryWrapper<>();
                queryWrapper.eq("customer_id", customerId);
                List<IpmsCustomerLinkman> customerLinkmanList = ipmsCustomerLinkmanService.list(queryWrapper);
                List<SafeCustomerLinkmanVO> safeCustomerLinkmanVOList = new ArrayList<>();
                for (IpmsCustomerLinkman customerLinkman : customerLinkmanList) {
                    SafeCustomerLinkmanVO safeCustomerLinkmanVO = new SafeCustomerLinkmanVO();
                    BeanUtils.copyProperties(customerLinkman, safeCustomerLinkmanVO);
                    Date linkmanBirth = customerLinkman.getLinkmanBirth();
                    if (linkmanBirth != null) {
                        safeCustomerLinkmanVO.setLinkmanBirth(TimeFormatUtil.dateFormatting2(linkmanBirth));
                    }
                    safeCustomerLinkmanVOList.add(safeCustomerLinkmanVO);
                }
                safeCustomerVO.setSafeCustomerLinkmanVOList(safeCustomerLinkmanVOList);
            }
            return safeCustomerVO;
        }).collect(Collectors.toList());
        // 关键步骤
        Page<SafeCustomerVO> safeCustomerVOPage = new PageDTO<>(customerPage.getCurrent(), customerPage.getSize(), customerPage.getTotal());
        safeCustomerVOPage.setRecords(safeCustomerVOList);
        return safeCustomerVOPage;
    }

    @Override
    public int addEnterpriseReceiveBalance(long customerId, double enterpriseReceiveBalance) {
        IpmsCustomer customer = ipmsCustomerMapper.selectById(customerId);
        if (customer == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "顾客不存在");
        }
        BigDecimal oldEnterpriseReceiveBalance = customer.getEnterpriseReceiveBalance();
        BigDecimal newEnterpriseReceiveBalance = oldEnterpriseReceiveBalance.add(new BigDecimal(String.valueOf(enterpriseReceiveBalance)));
        customer.setEnterpriseReceiveBalance(newEnterpriseReceiveBalance);
        int result = ipmsCustomerMapper.updateById(customer);
        if (result != 1) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "更新企业应收顾客金额失败");
        }
        return result;
    }

    @Override
    public int reduceEnterpriseReceiveBalance(long customerId, double enterpriseReceiveBalance) {
        IpmsCustomer customer = ipmsCustomerMapper.selectById(customerId);
        if (customer == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "客户不存在");
        }
        BigDecimal oldEnterpriseReceiveBalance = customer.getEnterpriseReceiveBalance();
        BigDecimal newEnterpriseReceiveBalance = oldEnterpriseReceiveBalance.subtract(new BigDecimal(String.valueOf(enterpriseReceiveBalance)));
        customer.setEnterpriseReceiveBalance(newEnterpriseReceiveBalance);
        int result = ipmsCustomerMapper.updateById(customer);
        if (result != 1) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "更新企业应收客户金额失败");
        }
        return result;
    }
}




