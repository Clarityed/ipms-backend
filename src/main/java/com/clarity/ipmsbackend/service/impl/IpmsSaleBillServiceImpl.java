package com.clarity.ipmsbackend.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.plugins.pagination.PageDTO;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.clarity.ipmsbackend.common.Constant;
import com.clarity.ipmsbackend.common.ErrorCode;
import com.clarity.ipmsbackend.common.SaleBillQueryRequest;
import com.clarity.ipmsbackend.constant.SaleBillConstant;
import com.clarity.ipmsbackend.exception.BusinessException;
import com.clarity.ipmsbackend.mapper.IpmsSaleBillMapper;
import com.clarity.ipmsbackend.model.dto.salebill.AddSaleBillRequest;
import com.clarity.ipmsbackend.model.dto.salebill.UpdateSaleBillRequest;
import com.clarity.ipmsbackend.model.dto.salebill.productnum.AddSaleProductNumRequest;
import com.clarity.ipmsbackend.model.dto.salebill.productnum.UpdateSaleProductNumRequest;
import com.clarity.ipmsbackend.model.entity.*;
import com.clarity.ipmsbackend.model.vo.SafeCustomerLinkmanVO;
import com.clarity.ipmsbackend.model.vo.SafeCustomerVO;
import com.clarity.ipmsbackend.model.vo.SafeProductVO;
import com.clarity.ipmsbackend.model.vo.SafeUserVO;
import com.clarity.ipmsbackend.model.vo.salebill.SafeSaleBillVO;
import com.clarity.ipmsbackend.model.vo.salebill.productnum.SafeSaleBillProductNumVO;
import com.clarity.ipmsbackend.service.*;
import com.clarity.ipmsbackend.utils.CodeAutoGenerator;
import com.clarity.ipmsbackend.utils.SplitUtil;
import com.clarity.ipmsbackend.utils.TimeFormatUtil;
import com.clarity.ipmsbackend.utils.ValidType;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
* @author Clarity
* @description 针对表【ipms_sale_bill(销售单据)】的数据库操作Service实现
* @createDate 2023-03-21 10:28:09
*/
@Service
@Slf4j
public class IpmsSaleBillServiceImpl extends ServiceImpl<IpmsSaleBillMapper, IpmsSaleBill>
    implements IpmsSaleBillService{

    @Resource
    private IpmsSaleBillMapper ipmsSaleBillMapper;

    @Resource
    private IpmsCustomerService ipmsCustomerService;

    @Resource
    private IpmsCustomerLinkmanService ipmsCustomerLinkmanService;

    @Resource
    private IpmsEmployeeService ipmsEmployeeService;

    @Resource
    private IpmsDepartmentService ipmsDepartmentService;

    @Resource
    private IpmsUserService ipmsUserService;

    @Resource
    private IpmsSaleBillProductNumService ipmsSaleBillProductNumService;

    @Resource
    private IpmsProductInventoryService ipmsProductInventoryService;

    @Resource
    private IpmsProductService ipmsProductService;

    @Resource
    private IpmsUnitService ipmsUnitService;

    @Resource
    private IpmsWarehouseService ipmsWarehouseService;

    @Resource
    private IpmsWarehousePositionService ipmsWarehousePositionService;

    @Override
    public String saleBillCodeAutoGenerate(String saleBillType) {
        if (saleBillType == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "单据类型为空，无法生成对于的单据编码");
        }
        QueryWrapper<IpmsSaleBill> ipmsSaleBillQueryWrapper = new QueryWrapper<>();
        ipmsSaleBillQueryWrapper.eq("sale_bill_type", saleBillType);
        List<IpmsSaleBill> ipmsSaleBillList;
        String saleBillCode;
        String saleBillCodePrefix;
        String saleBillCodeInfix;
        String saleBillCodeSuffix;
        switch (saleBillType) {
            case SaleBillConstant.SALE_ORDER:
                ipmsSaleBillList = ipmsSaleBillMapper.selectList(ipmsSaleBillQueryWrapper);
                if (ipmsSaleBillList.size() == 0) {
                    saleBillCodePrefix = "XSDD";
                    saleBillCodeInfix = TimeFormatUtil.dateFormat(new Date());
                    saleBillCodeSuffix = "0";
                } else {
                    IpmsSaleBill lastSaleBill = ipmsSaleBillList.get(ipmsSaleBillList.size() - 1);
                    saleBillCode = lastSaleBill.getSaleBillCode();
                    String[] saleOrderCode = SplitUtil.codeSplitByMinusSign(saleBillCode);
                    saleBillCodePrefix = saleOrderCode[0];
                    saleBillCodeInfix = saleOrderCode[1];
                    saleBillCodeSuffix = saleOrderCode[2];
                }
                break;
            case SaleBillConstant.SALE_DELIVERY_ORDER:
                ipmsSaleBillList = ipmsSaleBillMapper.selectList(ipmsSaleBillQueryWrapper);
                if (ipmsSaleBillList.size() == 0) {
                    saleBillCodePrefix = "XSCK";
                    saleBillCodeInfix = TimeFormatUtil.dateFormat(new Date());
                    saleBillCodeSuffix = "0";
                } else {
                    IpmsSaleBill lastSaleBill = ipmsSaleBillList.get(ipmsSaleBillList.size() - 1);
                    saleBillCode = lastSaleBill.getSaleBillCode();
                    String[] saleOrderCode = SplitUtil.codeSplitByMinusSign(saleBillCode);
                    saleBillCodePrefix = saleOrderCode[0];
                    saleBillCodeInfix = saleOrderCode[1];
                    saleBillCodeSuffix = saleOrderCode[2];
                }
                break;
            case SaleBillConstant.SALE_RETURN_ORDER:
                ipmsSaleBillList = ipmsSaleBillMapper.selectList(ipmsSaleBillQueryWrapper);
                if (ipmsSaleBillList.size() == 0) {
                    saleBillCodePrefix = "XSTH";
                    saleBillCodeInfix = TimeFormatUtil.dateFormat(new Date());
                    saleBillCodeSuffix = "0";
                } else {
                    IpmsSaleBill lastSaleBill = ipmsSaleBillList.get(ipmsSaleBillList.size() - 1);
                    saleBillCode = lastSaleBill.getSaleBillCode();
                    String[] saleOrderCode = SplitUtil.codeSplitByMinusSign(saleBillCode);
                    saleBillCodePrefix = saleOrderCode[0];
                    saleBillCodeInfix = saleOrderCode[1];
                    saleBillCodeSuffix = saleOrderCode[2];
                }
                break;
            default:
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "单据类型不存在");
        }
        String nextSaleBillCode = null;
        try {
            String todayDateFormat = TimeFormatUtil.dateFormat(new Date());
            if (!saleBillCodeInfix.equals(todayDateFormat)) {
                nextSaleBillCode = CodeAutoGenerator.generatorCode(saleBillCodePrefix, todayDateFormat, "0");
            } else {
                nextSaleBillCode = CodeAutoGenerator.generatorCode(saleBillCodePrefix, saleBillCodeInfix, saleBillCodeSuffix);
            }
        } catch (Exception e) {
            log.info("编码自动生成器异常");
        }
        return nextSaleBillCode;
    }

    @Override
    public int addSaleBill(AddSaleBillRequest addSaleBillRequest, HttpServletRequest request) {
        // 1. 单据编号不能为空，且必须不能重复
        String saleBillCode = addSaleBillRequest.getSaleBillCode();
        if (saleBillCode == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "单据编号为空");
        }
        QueryWrapper<IpmsSaleBill> saleBillQueryWrapper = new QueryWrapper<>();
        saleBillQueryWrapper.eq("sale_bill_code", saleBillCode);
        IpmsSaleBill validCodeSaleBill = ipmsSaleBillMapper.selectOne(saleBillQueryWrapper);
        if (validCodeSaleBill != null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "单据编号重复");
        }
        // 2. 单据日期不能为空
        String saleBillDate = addSaleBillRequest.getSaleBillDate();
        if (saleBillDate == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "单据日期为空");
        }
        // 3. 结算日期不能为空
        String saleBillSettlementDate = addSaleBillRequest.getSaleBillSettlementDate();
        if (saleBillSettlementDate == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "单据结算日期为空");
        }
        // 4. 客户 id 不能为空，且必须存在该客户
        Long customerId = addSaleBillRequest.getCustomerId();
        if (customerId == null || customerId <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "单据客户 id 为空或者不合法");
        }
        IpmsCustomer validCustomer = ipmsCustomerService.getById(customerId);
        if (validCustomer == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "单据客户不存在");
        }
        // 5. 职员 id 和 部门 id，可以为空，如果不为空，那么必须存在对于的职员和部门信息
        Long employeeId = addSaleBillRequest.getEmployeeId();
        if (employeeId != null) {
            IpmsEmployee validEmployee = ipmsEmployeeService.getById(employeeId);
            if (validEmployee == null) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "单据职员不存在");
            }
        }
        Long departmentId = addSaleBillRequest.getDepartmentId();
        if (departmentId != null) {
            IpmsDepartment validDepartment = ipmsDepartmentService.getById(departmentId);
            if (validDepartment == null) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "单据部门不存在");
            }
        }
        // 6. 成交金额一定存在，且必须大于 0
        BigDecimal saleBillTransactionAmount = addSaleBillRequest.getSaleBillTransactionAmount();
        if (saleBillTransactionAmount == null || saleBillTransactionAmount.doubleValue() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "单据成交金额一定要大于 0");
        }
        // 7. 销售单据类型不能为空，且必须符合系统要求
        String saleBillType = addSaleBillRequest.getSaleBillType();
        if (saleBillType == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "单据类型不能为空");
        }
        List<String> billTypeList = Arrays.asList(SaleBillConstant.SALE_ORDER, SaleBillConstant.SALE_DELIVERY_ORDER, SaleBillConstant.SALE_RETURN_ORDER);
        int validTypeResult = ValidType.valid(billTypeList, saleBillType);
        if (validTypeResult == 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "单据类型不存在");
        }
        // 8. 销售单据的商品及商品数量至少存在一个
        List<AddSaleProductNumRequest> addSaleProductNumRequestList = addSaleBillRequest.getAddSaleProductNumRequestList();
        if (addSaleProductNumRequestList == null || addSaleProductNumRequestList.size() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "销售的商品为空，至少要存在一个销售商品");
        }
        // 并且验证单据成交金额是否一致
        double validSaleBillTransactionAmount = 0;
        for (AddSaleProductNumRequest addSaleProductNumRequest : addSaleProductNumRequestList) {
            // 商品数量，不能为空，且必须大于 0
            BigDecimal productNum = addSaleProductNumRequest.getProductNum();
            if (productNum == null || productNum.doubleValue() <= 0) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "商品数量为空或者小于等于 0");
            }
            // 商品单价也是，价格合计也是
            BigDecimal unitPrice = addSaleProductNumRequest.getUnitPrice();
            BigDecimal totalPrice = addSaleProductNumRequest.getTotalPrice();
            if (unitPrice == null || unitPrice.doubleValue() <= 0) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "商品单价为空或者小于等于 0");
            }
            if (totalPrice == null || totalPrice.doubleValue() <= 0) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "价格合计为空或者小于等于 0");
            }
            BigDecimal validTotalPrice = unitPrice.multiply(productNum);
            if (!validTotalPrice.equals(totalPrice)) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "前后端计算行商品总价不一致");
            }
            validSaleBillTransactionAmount += totalPrice.doubleValue();
        }
        if (validSaleBillTransactionAmount != saleBillTransactionAmount.doubleValue()) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "前后端计算单据成交金额不一致");
        }
        // 单据必须审核后才能被当作单源来使用
        Long saleSourceBillId = addSaleBillRequest.getSaleSourceBillId();
        if (saleSourceBillId != null && saleSourceBillId > 0) {
            QueryWrapper<IpmsSaleBill> ipmsSaleBillQueryWrapper = new QueryWrapper<>();
            ipmsSaleBillQueryWrapper.eq("sale_bill_id", saleSourceBillId);
            IpmsSaleBill validCheckStateSaleBill = ipmsSaleBillMapper.selectOne(ipmsSaleBillQueryWrapper);
            Integer checkState = validCheckStateSaleBill.getCheckState();
            if (!checkState.equals(Constant.CHECKED)) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "选择的销售单源未审核");
            }
        }
        // 9. 增加数据，并且设置其他固定字段
        IpmsSaleBill saleBill = new IpmsSaleBill();
        BeanUtils.copyProperties(addSaleBillRequest, saleBill);
        SafeUserVO loginUser = ipmsUserService.getLoginUser(request);
        saleBill.setFounder(loginUser.getUserName());
        saleBill.setCreateTime(new Date());
        saleBill.setUpdateTime(new Date());
        int insertResult = ipmsSaleBillMapper.insert(saleBill);
        if (insertResult != 1) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR);
        }
        // 10. 最后调用增加销售单据商品及数量
        for (AddSaleProductNumRequest addSaleProductNumRequest : addSaleProductNumRequestList) {
            long addSaleBillProductAndNumResult = ipmsSaleBillProductNumService.addSaleBillProductAndNum(addSaleProductNumRequest, saleBill);
            if (addSaleBillProductAndNumResult < 0) {
                throw new BusinessException(ErrorCode.SYSTEM_ERROR, "销售单据商品及数量插入失败");
            }
        }
        // 11. 销售出库单增加并且还没审核就会影响销售订单的执行状态和关闭状态
        //     销售订单要么部分执行，还未关闭；要么完全执行并且关闭
        if (saleBillType.equals(SaleBillConstant.SALE_DELIVERY_ORDER) && saleSourceBillId != null && saleSourceBillId > 0) {
            IpmsSaleBill sourceSaleBill = ipmsSaleBillMapper.selectById(saleSourceBillId);
            QueryWrapper<IpmsSaleBillProductNum> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("sale_bill_id", sourceSaleBill.getSaleBillId());
            List<IpmsSaleBillProductNum> sourceSaleBillProductList = ipmsSaleBillProductNumService.list(queryWrapper);
            int temp = 0;
            for (IpmsSaleBillProductNum saleBillProductNum : sourceSaleBillProductList) {
                temp++;
                if (saleBillProductNum.getSurplusNeedDeliveryProductNum().doubleValue() != 0) {
                    sourceSaleBill.setExecutionState(Constant.PART_OPERATED);
                    ipmsSaleBillMapper.updateById(sourceSaleBill);
                    break;
                }
                if (temp == sourceSaleBillProductList.size()) {
                    sourceSaleBill.setExecutionState(Constant.FULL_OPERATED);
                    sourceSaleBill.setOffState(Constant.CLOSED);
                    ipmsSaleBillMapper.updateById(sourceSaleBill);
                }
            }
        }
        return insertResult;
    }

    @Override
    public int checkSaleBill(long saleBillId, HttpServletRequest request) {
        if (saleBillId <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "单据 id 不合法");
        }
        IpmsSaleBill saleBill = ipmsSaleBillMapper.selectById(saleBillId);
        if (saleBill == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }
        Integer checkState = saleBill.getCheckState();
        if (Constant.CHECKED == checkState) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "单据已审核");
        }
        String saleBillType = saleBill.getSaleBillType();
        IpmsSaleBill checkingSaleBill = new IpmsSaleBill();
        checkingSaleBill.setSaleBillId(saleBill.getSaleBillId());
        checkingSaleBill.setCheckState(Constant.CHECKED);
        // 定义一个变量用于存放销售出库单或者销售退货单，客户应该增加或者减少企业应付款金额
        double enterpriseReceiveBalance = 0;
        if (SaleBillConstant.SALE_DELIVERY_ORDER.equals(saleBillType)) {
            // 审核后要修改销售订单的出库状态
            QueryWrapper<IpmsSaleBill> saleBillQueryWrapper = new QueryWrapper<>();
            saleBillQueryWrapper.eq("sale_source_bill_id", saleBill.getSaleSourceBillId());
            saleBillQueryWrapper.eq("check_state", Constant.CHECKED);
            List<IpmsSaleBill> checkedSaleBillList = ipmsSaleBillMapper.selectList(saleBillQueryWrapper);
            List<Long> checkedSaleBillIdList = new ArrayList<>();
            for (IpmsSaleBill checkSaleBill : checkedSaleBillList) {
                checkedSaleBillIdList.add(checkSaleBill.getSaleBillId());
            }
            QueryWrapper<IpmsSaleBillProductNum> saleBillProductNumQueryWrapper = new QueryWrapper<>();
            double checkedSaleBillProductNum = 0;
            if (checkedSaleBillIdList.size() > 0) {
                saleBillProductNumQueryWrapper.in("sale_bill_id", checkedSaleBillIdList);
                List<IpmsSaleBillProductNum> checkedSaleBillProductList = ipmsSaleBillProductNumService.list(saleBillProductNumQueryWrapper);
                for (IpmsSaleBillProductNum checkedSaleBillProduct : checkedSaleBillProductList) {
                    checkedSaleBillProductNum += checkedSaleBillProduct.getNeedDeliveryProductNum().doubleValue();
                }
            }
            saleBillProductNumQueryWrapper = new QueryWrapper<>();
            saleBillProductNumQueryWrapper.eq("sale_bill_id", saleBillId);
            List<IpmsSaleBillProductNum> currentCheckingSaleBillProductList = ipmsSaleBillProductNumService.list(saleBillProductNumQueryWrapper);
            for (IpmsSaleBillProductNum currentCheckingSaleBillProduct : currentCheckingSaleBillProductList) {
                checkedSaleBillProductNum += currentCheckingSaleBillProduct.getNeedDeliveryProductNum().doubleValue();
                // 并且调用减少库存的方法
                BigDecimal oneSaleBillProductCost = ipmsProductInventoryService.reduceProductInventory(currentCheckingSaleBillProduct, saleBill.getSaleBillExchangeRate());
                enterpriseReceiveBalance += oneSaleBillProductCost.doubleValue();
                if (oneSaleBillProductCost.doubleValue() <= 0) {
                    throw new BusinessException(ErrorCode.SYSTEM_ERROR, "调用减少库存的方法失败");
                }
            }
            double needDeliveryProductNum = 0;
            saleBillProductNumQueryWrapper = new QueryWrapper<>();
            saleBillProductNumQueryWrapper.eq("sale_bill_id", saleBill.getSaleSourceBillId());
            List<IpmsSaleBillProductNum> sourceSaleBillProductList = ipmsSaleBillProductNumService.list(saleBillProductNumQueryWrapper);
            for (IpmsSaleBillProductNum sourceSaleBillProduct : sourceSaleBillProductList) {
                needDeliveryProductNum += sourceSaleBillProduct.getNeedDeliveryProductNum().doubleValue();
            }
            IpmsSaleBill sourceSaleBill = ipmsSaleBillMapper.selectById(saleBill.getSaleSourceBillId());
            if (sourceSaleBill != null) {
                if (checkedSaleBillProductNum == needDeliveryProductNum) {
                    sourceSaleBill.setDeliveryState(Constant.FULL_OPERATED);
                } else {
                    sourceSaleBill.setDeliveryState(Constant.PART_OPERATED);
                }
                int validSourceState = ipmsSaleBillMapper.updateById(sourceSaleBill);
                if (validSourceState != 1) {
                    throw new BusinessException(ErrorCode.SYSTEM_ERROR, "出库状态更新失败");
                }
            }
            // 调用客户模块业务接口，实现增加企业应收客户金额
            int result = ipmsCustomerService.addEnterpriseReceiveBalance(saleBill.getCustomerId(), enterpriseReceiveBalance);
            if (result != 1) {
                throw new BusinessException(ErrorCode.SYSTEM_ERROR, "增加企业应收客户金额失败");
            }
        } else if (SaleBillConstant.SALE_RETURN_ORDER.equals(saleBillType)) {
            // 销售退货单审核后要调用增加库存的方法
            QueryWrapper<IpmsSaleBillProductNum> saleBillProductNumQueryWrapper = new QueryWrapper<>();
            saleBillProductNumQueryWrapper.eq("sale_bill_id", saleBillId);
            List<IpmsSaleBillProductNum> saleReturnProductList = ipmsSaleBillProductNumService.list(saleBillProductNumQueryWrapper);
            for (IpmsSaleBillProductNum saleReturnProduct : saleReturnProductList) {
                BigDecimal oneSaleBillProductCost = ipmsProductInventoryService.addProductInventory(saleReturnProduct, saleBill.getSaleBillExchangeRate());
                enterpriseReceiveBalance += oneSaleBillProductCost.doubleValue();
                if (oneSaleBillProductCost.doubleValue() <= 0) {
                    throw new BusinessException(ErrorCode.SYSTEM_ERROR, "调用增加库存的方法失败");
                }
            }
            // 调用客户模块减少企业应收客户金额
            int result = ipmsCustomerService.reduceEnterpriseReceiveBalance(saleBill.getCustomerId(), enterpriseReceiveBalance);
            if (result != 1) {
                throw new BusinessException(ErrorCode.SYSTEM_ERROR, "减少企业应收客户金额失败");
            }
        }
        SafeUserVO loginUser = ipmsUserService.getLoginUser(request);
        checkingSaleBill.setChecker(loginUser.getUserName());
        checkingSaleBill.setCheckTime(new Date());
        int result = ipmsSaleBillMapper.updateById(checkingSaleBill);
        if (result != 1) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR);
        }
        return result;
    }

    @Override
    public int reverseCheckSaleBill(long saleBillId, HttpServletRequest request) {
        if (saleBillId <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "单据 id 不合法");
        }
        IpmsSaleBill saleBill = ipmsSaleBillMapper.selectById(saleBillId);
        if (saleBill == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }
        if (Constant.UNCHECKED == saleBill.getCheckState()) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "单据未审核");
        }
        String saleBillType = saleBill.getSaleBillType();
        IpmsSaleBill unCheckingSaleBill = new IpmsSaleBill();
        unCheckingSaleBill.setSaleBillId(saleBill.getSaleBillId());
        unCheckingSaleBill.setCheckState(Constant.UNCHECKED);
        unCheckingSaleBill.setChecker(null);
        unCheckingSaleBill.setCheckTime(null);
        // 如果销售订单已经作为其他单据的源单，那么无法反审核，关闭的销售订单更是无法反审核，因为它肯定拥有源单
        // 销售出库单第一种情况没有选单源并且也是已经作为其他单据的源单，那么无法反审核。
        // 销售退货单第一种情况没有选单源并且也是已经作为其他单据的源单，那么无法反审核。（没有这种情况，销售退库单不作为其他单源）
        QueryWrapper<IpmsSaleBill> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("sale_source_bill_id", saleBillId);
        List<IpmsSaleBill> isAsSourceBill = ipmsSaleBillMapper.selectList(queryWrapper);
        if (isAsSourceBill != null && isAsSourceBill.size() > 0) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "单据已经作为其他单据的单源使用");
        }
        // 定义一个变量用于存放销售出库单或者销售退货单，客户应该增加或者减少企业应付款金额
        double enterpriseReceiveBalance = 0;
        if (SaleBillConstant.SALE_DELIVERY_ORDER.equals(saleBillType)) {
            // 反审核的话必须将对于的源单剩余还有多少商品未被引用的数量恢复
            // 1. 遍历销售出库单出库商品及数量
            QueryWrapper<IpmsSaleBillProductNum> saleBillProductNumQueryWrapper = new QueryWrapper<>();
            saleBillProductNumQueryWrapper.eq("sale_bill_id", saleBillId);
            List<IpmsSaleBillProductNum> saleBillReceiptProductList = ipmsSaleBillProductNumService.list(saleBillProductNumQueryWrapper);
            // 2. 遍历里面的每一个商品与源单商品做对比相同的数量恢复，并且增加库存
            for (IpmsSaleBillProductNum saleBillReceiptProduct : saleBillReceiptProductList) {
                BigDecimal oneSaleBillProductCost = ipmsProductInventoryService.addProductInventory(saleBillReceiptProduct, saleBill.getSaleBillExchangeRate());
                enterpriseReceiveBalance += oneSaleBillProductCost.doubleValue();
                if (oneSaleBillProductCost.doubleValue() <= 0) {
                    throw new BusinessException(ErrorCode.SYSTEM_ERROR, "销售出库单反审核增加库存失败");
                }
            }
            // 修改源单状态为原来的状态
            // 1. 查询已经审核的销售出库单的所有商品，如果销售出库单数量大于 1 且全部是已审核的单据，那把出库状态改为部分出库
            //    如果销售出库单已审核的单据数量大于 1 且只剩下 1 单了，那么出库状态改为，未出库
            Long saleSourceBillId = saleBill.getSaleSourceBillId();
            if (saleSourceBillId != null && saleSourceBillId > 0) {
                queryWrapper = new QueryWrapper<>();
                queryWrapper.eq("sale_source_bill_id", saleSourceBillId);
                queryWrapper.eq("check_state", Constant.CHECKED);
                long checkedCount = ipmsSaleBillMapper.selectCount(queryWrapper);
                queryWrapper = new QueryWrapper<>();
                queryWrapper.eq("sale_source_bill_id", saleSourceBillId);
                Long checkedAndUncheckedCount = ipmsSaleBillMapper.selectCount(queryWrapper);
                IpmsSaleBill sourceSaleBill = ipmsSaleBillMapper.selectById(saleSourceBillId);
                if (checkedAndUncheckedCount == 1) {
                    sourceSaleBill.setDeliveryState(Constant.NOT_OPERATED);
                    int result = ipmsSaleBillMapper.updateById(sourceSaleBill);
                    if (result != 1) {
                        throw new BusinessException(ErrorCode.SYSTEM_ERROR, "反审核销售出库单，状态修改失败");
                    }
                } else if (checkedAndUncheckedCount > 1 && checkedCount > 1) {
                    sourceSaleBill.setDeliveryState(Constant.PART_OPERATED);
                    int result = ipmsSaleBillMapper.updateById(sourceSaleBill);
                    if (result != 1) {
                        throw new BusinessException(ErrorCode.SYSTEM_ERROR, "反审核销售出库单，状态修改失败");
                    }
                } else {
                    sourceSaleBill.setDeliveryState(Constant.NOT_OPERATED);
                    int result = ipmsSaleBillMapper.updateById(sourceSaleBill);
                    if (result != 1) {
                        throw new BusinessException(ErrorCode.SYSTEM_ERROR, "反审核销售出库单，状态修改失败");
                    }
                }
            }
            // 调用客户模块减少企业应收余额
            int result = ipmsCustomerService.reduceEnterpriseReceiveBalance(saleBill.getCustomerId(), enterpriseReceiveBalance);
            if (result != 1) {
                throw new BusinessException(ErrorCode.SYSTEM_ERROR, "减少企业应收客户金额失败");
            }
        } else if (SaleBillConstant.SALE_RETURN_ORDER.equals(saleBillType)) {
            // 销售退货单反审核后要调用增加库存的方法
            // 1. 遍历销售退货单商品及数量
            QueryWrapper<IpmsSaleBillProductNum> saleBillProductNumQueryWrapper = new QueryWrapper<>();
            saleBillProductNumQueryWrapper.eq("sale_bill_id", saleBillId);
            List<IpmsSaleBillProductNum> saleBillReturnProductList = ipmsSaleBillProductNumService.list(saleBillProductNumQueryWrapper);
            // 2. 遍历里面的每一个商品与源单商品做对比相同的数量恢复，并且减少库存
            for (IpmsSaleBillProductNum saleBillReturnProduct : saleBillReturnProductList) {
                BigDecimal oneSaleBillProductCost = ipmsProductInventoryService.addProductInventory(saleBillReturnProduct, saleBill.getSaleBillExchangeRate());
                enterpriseReceiveBalance += oneSaleBillProductCost.doubleValue();
                if (oneSaleBillProductCost.doubleValue() <= 0) {
                    throw new BusinessException(ErrorCode.SYSTEM_ERROR, "销售退货单反审核减少库存失败");
                }
            }
            // 调用客户模块业务接口，实现增加企业应收客户金额
            int result = ipmsCustomerService.addEnterpriseReceiveBalance(saleBill.getCustomerId(), enterpriseReceiveBalance);
            if (result != 1) {
                throw new BusinessException(ErrorCode.SYSTEM_ERROR, "增加企业应收客户金额失败");
            }
        }
        int result = ipmsSaleBillMapper.updateById(unCheckingSaleBill);
        if (result != 1) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR);
        }
        return result;
    }

    @Override
    public int deleteSaleBillById(long id) {
        // 1. 判断 id 是否合法
        if (id <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "id 不合法");
        }
        // 2. 审核后的单据无法删除
        IpmsSaleBill oldSaleBill = ipmsSaleBillMapper.selectById(id);
        if (oldSaleBill == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }
        if (oldSaleBill.getCheckState().equals(Constant.CHECKED)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "单据已经审核无法删除");
        }
        // 3. 按照单据类型分类别删除
        String saleBillType = oldSaleBill.getSaleBillType();
        Long saleSourceBillId = oldSaleBill.getSaleSourceBillId();
        QueryWrapper<IpmsSaleBillProductNum> saleBillProductNumQueryWrapper;
        if (SaleBillConstant.SALE_DELIVERY_ORDER.equals(saleBillType)) {
            if (saleSourceBillId != null) {
                saleBillProductNumQueryWrapper = new QueryWrapper<>();
                saleBillProductNumQueryWrapper.eq("sale_bill_id", id);
                List<IpmsSaleBillProductNum> saleReceiptBillProductList = ipmsSaleBillProductNumService.list(saleBillProductNumQueryWrapper);
                for (IpmsSaleBillProductNum saleReceiptBillProductNum : saleReceiptBillProductList) {
                    saleBillProductNumQueryWrapper = new QueryWrapper<>();
                    saleBillProductNumQueryWrapper.eq("sale_bill_id", saleSourceBillId);
                    saleBillProductNumQueryWrapper.eq("product_id", saleReceiptBillProductNum.getProductId());
                    saleBillProductNumQueryWrapper.eq("warehouse_id", saleReceiptBillProductNum.getWarehouseId());
                    if (saleReceiptBillProductNum.getWarehousePositionId() != null) {
                        saleBillProductNumQueryWrapper.eq("warehouse_position_id", saleReceiptBillProductNum.getWarehouseId());
                    }
                    // 销售订单的商品也就是源单的商品
                    IpmsSaleBillProductNum sourceProductOfOne = ipmsSaleBillProductNumService.getOne(saleBillProductNumQueryWrapper);
                    // 修改源单商品数量为未必销售出库单引用的剩余数量
                    if (sourceProductOfOne != null) {
                        BigDecimal oldSurplusNeedDeliveryProductNum = sourceProductOfOne.getSurplusNeedDeliveryProductNum();
                        sourceProductOfOne.setSurplusNeedDeliveryProductNum(oldSurplusNeedDeliveryProductNum.add(saleReceiptBillProductNum.getNeedDeliveryProductNum()));
                        boolean recoverSourceProductNumResult = ipmsSaleBillProductNumService.updateById(sourceProductOfOne);
                        if (!recoverSourceProductNumResult) {
                            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "恢复销售订单剩余数量失败");
                        }
                    }
                }
                // 修改销售订单状态
                saleBillProductNumQueryWrapper = new QueryWrapper<>();
                saleBillProductNumQueryWrapper.eq("sale_bill_id", saleSourceBillId);
                List<IpmsSaleBillProductNum> sourceProductList = ipmsSaleBillProductNumService.list(saleBillProductNumQueryWrapper);
                IpmsSaleBill sourceSaleBill = ipmsSaleBillMapper.selectById(saleSourceBillId);
                int temp = 0;
                for (IpmsSaleBillProductNum sourceProduct : sourceProductList) {
                    temp++;
                    if (!sourceProduct.getNeedDeliveryProductNum().equals(sourceProduct.getSurplusNeedDeliveryProductNum())) {
                        // 设置执行状态和关闭状态
                        sourceSaleBill.setExecutionState(Constant.PART_OPERATED);
                        sourceSaleBill.setOffState(Constant.NOT_CLOSED);
                        int result = ipmsSaleBillMapper.updateById(sourceSaleBill);
                        if (result != 1) {
                            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "删除销售出库单，状态修改失败");
                        }
                        break;
                    }
                    if (temp == sourceProductList.size()) {
                        sourceSaleBill.setExecutionState(Constant.NOT_OPERATED);
                        sourceSaleBill.setOffState(Constant.NOT_CLOSED);
                        int result = ipmsSaleBillMapper.updateById(sourceSaleBill);
                        if (result != 1) {
                            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "删除销售出库单，状态修改失败");
                        }
                    }
                }
            }
        } else if (SaleBillConstant.SALE_RETURN_ORDER.equals(saleBillType)) {
            if (saleSourceBillId != null) {
                saleBillProductNumQueryWrapper = new QueryWrapper<>();
                saleBillProductNumQueryWrapper.eq("sale_bill_id", id);
                List<IpmsSaleBillProductNum> saleReceiptBillProductList = ipmsSaleBillProductNumService.list(saleBillProductNumQueryWrapper);
                for (IpmsSaleBillProductNum saleReceiptBillProductNum : saleReceiptBillProductList) {
                    saleBillProductNumQueryWrapper = new QueryWrapper<>();
                    saleBillProductNumQueryWrapper.eq("sale_bill_id", saleSourceBillId);
                    saleBillProductNumQueryWrapper.eq("product_id", saleReceiptBillProductNum.getProductId());
                    saleBillProductNumQueryWrapper.eq("warehouse_id", saleReceiptBillProductNum.getWarehouseId());
                    if (saleReceiptBillProductNum.getWarehousePositionId() != null) {
                        saleBillProductNumQueryWrapper.eq("warehouse_position_id", saleReceiptBillProductNum.getWarehouseId());
                    }
                    // 销售出库单的商品也就是源单的商品
                    IpmsSaleBillProductNum sourceProductOfOne = ipmsSaleBillProductNumService.getOne(saleBillProductNumQueryWrapper);
                    // 修改源单商品数量为未必销售出库单引用的剩余数量
                    if (sourceProductOfOne != null) {
                        BigDecimal oldSurplusNeedDeliveryProductNum = sourceProductOfOne.getSurplusNeedDeliveryProductNum();
                        sourceProductOfOne.setSurplusNeedDeliveryProductNum(oldSurplusNeedDeliveryProductNum.add(saleReceiptBillProductNum.getNeedDeliveryProductNum()));
                        boolean recoverSourceProductNumResult = ipmsSaleBillProductNumService.updateById(sourceProductOfOne);
                        if (!recoverSourceProductNumResult) {
                            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "恢复销售订单剩余数量失败");
                        }
                    }
                }
            }
        }
        int result = ipmsSaleBillMapper.deleteById(id);
        if (result != 1) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "删除失败");
        }
        saleBillProductNumQueryWrapper = new QueryWrapper<>();
        saleBillProductNumQueryWrapper.eq("sale_bill_id", id);
        boolean remove = ipmsSaleBillProductNumService.remove(saleBillProductNumQueryWrapper);
        if (!remove) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "关联删除失败");
        }
        return result;
    }

    @Override
    public int updateSaleBill(UpdateSaleBillRequest updateSaleBillRequest, HttpServletRequest request) {
        Long saleBillId = updateSaleBillRequest.getSaleBillId();
        if (saleBillId == null || saleBillId <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "单据 id 为空或者不合法");
        }
        // 判断该单据是否存在
        IpmsSaleBill oldSaleBill = ipmsSaleBillMapper.selectById(saleBillId);
        if (oldSaleBill == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "单据不存在");
        }
        Integer checkState = oldSaleBill.getCheckState();
        if (Constant.CHECKED == checkState) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "单据已审核，无法修改");
        }
        String saleBillCode = updateSaleBillRequest.getSaleBillCode();
        if (saleBillCode != null) {
            if (!saleBillCode.equals(oldSaleBill.getSaleBillCode())) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "单据编号不支持修改");
            }
        }
        Long customerId = updateSaleBillRequest.getCustomerId();
        if (customerId != null && customerId > 0) {
            if (!customerId.equals(oldSaleBill.getCustomerId())) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "无法修改单据所属客户");
            }
        }
        Long employeeId = updateSaleBillRequest.getEmployeeId();
        Long departmentId = updateSaleBillRequest.getDepartmentId();
        if (employeeId != null && employeeId > 0) {
            IpmsEmployee employee = ipmsEmployeeService.getById(employeeId);
            if (employee == null) {
                throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "职员不存在");
            }
        }
        if (departmentId != null && departmentId > 0) {
            IpmsDepartment department = ipmsDepartmentService.getById(departmentId);
            if (department == null) {
                throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "部门不存在");
            }
        }
        String saleBillType = updateSaleBillRequest.getSaleBillType();
        if (saleBillType != null) {
            if (!saleBillType.equals(oldSaleBill.getSaleBillType())) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "单据类型不能修改");
            }
        }
        // 8. 销售单据的商品及商品数量至少存在一个
        List<UpdateSaleProductNumRequest> updateSaleProductNumRequestList = updateSaleBillRequest.getUpdateSaleProductNumRequestList();
        if (updateSaleProductNumRequestList == null || updateSaleProductNumRequestList.size() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "销售的商品为空，至少要存在一个销售商品");
        }
        BigDecimal saleBillTransactionAmount = updateSaleBillRequest.getSaleBillTransactionAmount();
        if (saleBillTransactionAmount == null || saleBillTransactionAmount.doubleValue() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "单据成交金额一定要大于 0");
        }
        // 并且验证单据成交金额是否一致
        double validSaleBillTransactionAmount = 0;
        for (UpdateSaleProductNumRequest updateSaleProductNumRequest : updateSaleProductNumRequestList) {
            // 商品数量，不能为空，且必须大于 0
            BigDecimal productNum = updateSaleProductNumRequest.getProductNum();
            if (productNum == null || productNum.doubleValue() <= 0) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "商品数量为空或者小于等于 0");
            }
            // 商品单价也是，价格合计也是
            BigDecimal unitPrice = updateSaleProductNumRequest.getUnitPrice();
            BigDecimal totalPrice = updateSaleProductNumRequest.getTotalPrice();
            if (unitPrice == null || unitPrice.doubleValue() <= 0) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "商品单价为空或者小于等于 0");
            }
            if (totalPrice == null || totalPrice.doubleValue() <= 0) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "价格合计为空或者小于等于 0");
            }
            BigDecimal validTotalPrice = unitPrice.multiply(productNum);
            if (!validTotalPrice.equals(totalPrice)) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "前后端计算行商品总价不一致");
            }
            validSaleBillTransactionAmount += totalPrice.doubleValue();
        }
        if (validSaleBillTransactionAmount != saleBillTransactionAmount.doubleValue()) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "前后端计算单据成交金额不一致");
        }
        // 更新销售单据
        IpmsSaleBill newSaleBill = new IpmsSaleBill();
        BeanUtils.copyProperties(updateSaleBillRequest, newSaleBill);
        SafeUserVO loginUser = ipmsUserService.getLoginUser(request);
        newSaleBill.setModifier(loginUser.getUserName());
        newSaleBill.setUpdateTime(new Date());
        int result = ipmsSaleBillMapper.updateById(newSaleBill);
        if (result != 1) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "修改销售单据失败");
        }
        List<Long> updateAndInsertSaleBillProductList = new ArrayList<>();
        for (UpdateSaleProductNumRequest updateSaleProductNumRequest : updateSaleProductNumRequestList) {
            Long saleBillProductId = updateSaleProductNumRequest.getSaleBillProductId();
            // 如果存在 saleBillProductId，那么肯定是要更新的数据，进行更新，并且存入列表中
            if (saleBillProductId != null && saleBillProductId > 0) {
                updateAndInsertSaleBillProductList.add(saleBillProductId);
                ipmsSaleBillProductNumService.updateSaleBillProductAndNum(updateSaleProductNumRequest, oldSaleBill);
            } else {
                // 否则，就是插入新的数据
                AddSaleProductNumRequest addSaleProductNumRequest = new AddSaleProductNumRequest();
                BeanUtils.copyProperties(updateSaleProductNumRequest, addSaleProductNumRequest);
                long insertSaleBillProductId = ipmsSaleBillProductNumService.addSaleBillProductAndNum(addSaleProductNumRequest, oldSaleBill);
                if (insertSaleBillProductId < 0) {
                    throw new BusinessException(ErrorCode.SYSTEM_ERROR, "增加销售单据商品失败");
                }
                updateAndInsertSaleBillProductList.add(insertSaleBillProductId);
            }
        }
        if (updateAndInsertSaleBillProductList.size() > 0) {
            // 如果更新销售单据商品的 id 不在这个列表内，那么删除销售单据
            QueryWrapper<IpmsSaleBillProductNum> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("sale_bill_id", saleBillId);
            queryWrapper.notIn("sale_bill_product_id", updateAndInsertSaleBillProductList);
            List<IpmsSaleBillProductNum> willRemoveSaleBillProductList = ipmsSaleBillProductNumService.list(queryWrapper);
            for (IpmsSaleBillProductNum willRemoveSaleBillProduct : willRemoveSaleBillProductList) {
                // 即将被删除的数据，如果是来源源单，恢复原单源的数据
                QueryWrapper<IpmsSaleBillProductNum> saleBillProductNumQueryWrapper = new QueryWrapper<>();
                saleBillProductNumQueryWrapper.eq("sale_bill_id", oldSaleBill.getSaleSourceBillId());
                saleBillProductNumQueryWrapper.eq("product_id", willRemoveSaleBillProduct.getProductId());
                IpmsSaleBillProductNum sourceSaleBillProduct = ipmsSaleBillProductNumService.getOne(saleBillProductNumQueryWrapper);
                if (sourceSaleBillProduct != null) {
                    sourceSaleBillProduct.setSurplusNeedDeliveryProductNum(willRemoveSaleBillProduct.getNeedDeliveryProductNum());
                    ipmsSaleBillProductNumService.updateById(sourceSaleBillProduct);
                }
            }
            // 删除
            ipmsSaleBillProductNumService.remove(queryWrapper);
        }
        // 销售订单修改不会有什么状态改变
        // 销售退货单修改也不会有什么状态改变，因为销售出库单，只有审核状态
        // 销售出库单修改会有 3 种情况：
        // 第一种：销售订单的完全执行状态改为部分执行状态，已关闭状态改为未关闭状态
        // 第二种：销售订单的部分执行状态改为完全执行状态，未关闭状态改为已关闭状态
        // 第三种：销售订单的部分执行状态不变，未关闭状态不变
        // 最后就是不做任何修改的操作，那么就是不变
        Long saleSourceBillId = oldSaleBill.getSaleSourceBillId();
        if (SaleBillConstant.SALE_DELIVERY_ORDER.equals(oldSaleBill.getSaleBillType()) && saleSourceBillId != null && saleSourceBillId > 0) {
            // 如果代码执行到这里，那么已经修改完了数据里面销售订单剩余需要出库的商品数量
            IpmsSaleBill sourceSaleBill = ipmsSaleBillMapper.selectById(saleSourceBillId);
            QueryWrapper<IpmsSaleBillProductNum> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("sale_bill_id", sourceSaleBill.getSaleBillId());
            List<IpmsSaleBillProductNum> sourceSaleBillProductList = ipmsSaleBillProductNumService.list(queryWrapper);
            int temp = 0;
            for (IpmsSaleBillProductNum saleBillProductNum : sourceSaleBillProductList) {
                temp++;
                if (saleBillProductNum.getSurplusNeedDeliveryProductNum().doubleValue() != 0) {
                    sourceSaleBill.setExecutionState(Constant.PART_OPERATED);
                    sourceSaleBill.setOffState(Constant.NOT_CLOSED);
                    ipmsSaleBillMapper.updateById(newSaleBill);
                    break;
                }
                if (temp == sourceSaleBillProductList.size()) {
                    sourceSaleBill.setExecutionState(Constant.FULL_OPERATED);
                    sourceSaleBill.setOffState(Constant.CLOSED);
                    ipmsSaleBillMapper.updateById(newSaleBill);
                }
            }
        }
        return result;
    }

    @Override
    public Page<SafeSaleBillVO> selectSaleBill(SaleBillQueryRequest saleBillQueryRequest) {
        // 1. 参数校验
        String saleBillType = saleBillQueryRequest.getSaleBillType();
        if (saleBillType == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "单据类型不能为空");
        }
        List<String> billTypeList = Arrays.asList(SaleBillConstant.SALE_ORDER, SaleBillConstant.SALE_DELIVERY_ORDER, SaleBillConstant.SALE_RETURN_ORDER);
        int validTypeResult = ValidType.valid(billTypeList, saleBillType);
        if (validTypeResult == 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "单据类型不存在");
        }
        // 2. 进行分页查询，分情况查询，销售订单，销售出库单和销售退货单
        Page<IpmsSaleBill> page = new Page<>(saleBillQueryRequest.getCurrentPage(), saleBillQueryRequest.getPageSize());
        QueryWrapper<IpmsSaleBill> ipmsSaleBillQueryWrapper = new QueryWrapper<>();
        String fuzzyText = saleBillQueryRequest.getFuzzyText();
        Long customerId = saleBillQueryRequest.getCustomerId();
        if (customerId != null && customerId > 0) {
            IpmsCustomer customer = ipmsCustomerService.getById(customerId);
            if (customer == null) {
                throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "客户不存在");
            }
            // 找到哪些还需要被引用的单源
            QueryWrapper<IpmsSaleBill> saleBillQueryWrapper = new QueryWrapper<>();
            saleBillQueryWrapper.eq("customer_id", customerId);
            saleBillQueryWrapper.eq("sale_bill_type", saleBillType);
            // 找出属于该客户的所有对应类型的销售单据
            List<IpmsSaleBill> ipmsSaleBills = ipmsSaleBillMapper.selectList(saleBillQueryWrapper);
            // 收集源单 id 集合
            List<Long> sourceSaleBillIdList = new ArrayList<>();
            if (ipmsSaleBills != null && ipmsSaleBills.size() > 0) {
                for (IpmsSaleBill ipmsSaleBill : ipmsSaleBills) {
                    Long saleBillId = ipmsSaleBill.getSaleBillId();
                    if (saleBillId != null && saleBillId > 0) {
                        sourceSaleBillIdList.add(saleBillId);
                    }
                }
            }
            // 存储当前客户的可以作为源单的 id 集合
            List<Long> currentCanUseSourceSaleBillIdList = new ArrayList<>();
            if (sourceSaleBillIdList.size() > 0) {
                QueryWrapper<IpmsSaleBillProductNum> saleBillProductNumQueryWrapper;
                for (Long sourceSaleBillId : sourceSaleBillIdList) {
                    saleBillProductNumQueryWrapper = new QueryWrapper<>();
                    saleBillProductNumQueryWrapper.eq("sale_bill_id", sourceSaleBillId);
                    List<IpmsSaleBillProductNum> saleBillProductNums = ipmsSaleBillProductNumService.list(saleBillProductNumQueryWrapper);
                    if (saleBillProductNums != null && saleBillProductNums.size() > 0) {
                        for (IpmsSaleBillProductNum saleBillProductNum : saleBillProductNums) {
                            if (saleBillProductNum.getSurplusNeedDeliveryProductNum().doubleValue() != 0) {
                                currentCanUseSourceSaleBillIdList.add(saleBillProductNum.getSaleBillId());
                                break;
                            }
                        }
                    }
                }
            }
            if (currentCanUseSourceSaleBillIdList.size() > 0) {
                if (StringUtils.isNotBlank(fuzzyText)) {
                    ipmsSaleBillQueryWrapper.like("sale_bill_code", fuzzyText)
                            .and(billType -> billType.eq("sale_bill_type", saleBillType))
                            .and(supId -> supId.eq("customer_id", customerId))
                            .and(saleBillId -> saleBillId.in("sale_bill_id", currentCanUseSourceSaleBillIdList)).or()
                            .like("sale_bill_date", fuzzyText)
                            .and(billType -> billType.eq("sale_bill_type", saleBillType))
                            .and(supId -> supId.eq("customer_id", customerId))
                            .and(saleBillId -> saleBillId.in("sale_bill_id", currentCanUseSourceSaleBillIdList)).or()
                            .like("sale_bill_settlement_date", fuzzyText)
                            .and(billType -> billType.eq("sale_bill_type", saleBillType))
                            .and(supId -> supId.eq("customer_id", customerId))
                            .and(saleBillId -> saleBillId.in("sale_bill_id", currentCanUseSourceSaleBillIdList)).or()
                            .like("sale_bill_remark", fuzzyText)
                            .and(billType -> billType.eq("sale_bill_type", saleBillType))
                            .and(supId -> supId.eq("customer_id", customerId))
                            .and(saleBillId -> saleBillId.in("sale_bill_id", currentCanUseSourceSaleBillIdList)).or()
                            .like("sale_bill_currency_type", fuzzyText)
                            .and(billType -> billType.eq("sale_bill_type", saleBillType))
                            .and(supId -> supId.eq("customer_id", customerId))
                            .and(saleBillId -> saleBillId.in("sale_bill_id", currentCanUseSourceSaleBillIdList)).or()
                            .like("sale_bill_exchange_rate", fuzzyText)
                            .and(billType -> billType.eq("sale_bill_type", saleBillType))
                            .and(supId -> supId.eq("customer_id", customerId))
                            .and(saleBillId -> saleBillId.in("sale_bill_id", currentCanUseSourceSaleBillIdList));
                } else {
                    ipmsSaleBillQueryWrapper.eq("sale_bill_type", saleBillType);
                    ipmsSaleBillQueryWrapper.eq("customer_id", customerId);
                    ipmsSaleBillQueryWrapper.in("sale_bill_id", currentCanUseSourceSaleBillIdList);
                }
            } else {
                return new PageDTO<>();
            }
        } else {
            if (StringUtils.isNotBlank(fuzzyText)) {
                ipmsSaleBillQueryWrapper.like("sale_bill_code", fuzzyText)
                        .and(billType -> billType.eq("sale_bill_type", saleBillType)).or()
                        .like("sale_bill_date", fuzzyText)
                        .and(billType -> billType.eq("sale_bill_type", saleBillType)).or()
                        .like("sale_bill_settlement_date", fuzzyText)
                        .and(billType -> billType.eq("sale_bill_type", saleBillType)).or()
                        .like("sale_bill_remark", fuzzyText)
                        .and(billType -> billType.eq("sale_bill_type", saleBillType)).or()
                        .like("sale_bill_currency_type", fuzzyText)
                        .and(billType -> billType.eq("sale_bill_type", saleBillType)).or()
                        .like("sale_bill_exchange_rate", fuzzyText)
                        .and(billType -> billType.eq("sale_bill_type", saleBillType));
            } else {
                ipmsSaleBillQueryWrapper.eq("sale_bill_type", saleBillType);
            }
        }
        Page<IpmsSaleBill> saleBillPage = ipmsSaleBillMapper.selectPage(page, ipmsSaleBillQueryWrapper);
        List<SafeSaleBillVO> safeSaleBillVOList = saleBillPage.getRecords().stream().map(ipmsSaleBill -> {
            SafeSaleBillVO safeSaleBillVO = new SafeSaleBillVO();
            BeanUtils.copyProperties(ipmsSaleBill, safeSaleBillVO);
            // 设置源单相关的数据
            Long saleSourceBillId = ipmsSaleBill.getSaleSourceBillId();
            if (saleSourceBillId != null && saleSourceBillId > 0) {
                IpmsSaleBill saleBill = ipmsSaleBillMapper.selectById(saleSourceBillId);
                if (saleBill == null) {
                    throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "找不到源单，系统业务错误");
                }
                safeSaleBillVO.setSaleSourceBillCode(saleBill.getSaleBillCode());
                safeSaleBillVO.setSaleSourceBillType(saleBill.getSaleBillType());
            }
            // 查询客户信息，并设置到返回封装类中
            Long supId = ipmsSaleBill.getCustomerId();
            if (supId != null && supId > 0) {
                IpmsCustomer sup = ipmsCustomerService.getById(supId);
                if (sup == null) {
                    throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "找不到客户信息，系统业务错误");
                }
                SafeCustomerVO safeCustomerVO = new SafeCustomerVO();
                QueryWrapper<IpmsCustomerLinkman> customerLinkmanQueryWrapper = new QueryWrapper<>();
                customerLinkmanQueryWrapper.eq("customer_id", supId);
                List<IpmsCustomerLinkman> customerLinkmanList = ipmsCustomerLinkmanService.list(customerLinkmanQueryWrapper);
                List<SafeCustomerLinkmanVO> safeCustomerLinkmanVOList = new ArrayList<>();
                if (customerLinkmanList != null && customerLinkmanList.size() > 0) {
                    for (IpmsCustomerLinkman customerLinkman : customerLinkmanList) {
                        SafeCustomerLinkmanVO safeCustomerLinkmanVO = new SafeCustomerLinkmanVO();
                        BeanUtils.copyProperties(customerLinkman, safeCustomerLinkmanVO);
                        Date linkmanBirth = customerLinkman.getLinkmanBirth();
                        if (linkmanBirth != null) {
                            safeCustomerLinkmanVO.setLinkmanBirth(TimeFormatUtil.dateFormatting2(linkmanBirth));
                        }
                        safeCustomerLinkmanVOList.add(safeCustomerLinkmanVO);
                    }
                }
                BeanUtils.copyProperties(sup, safeCustomerVO);
                safeCustomerVO.setSafeCustomerLinkmanVOList(safeCustomerLinkmanVOList);
                safeSaleBillVO.setSafeCustomerVO(safeCustomerVO);
            }
            // 查询职员信息，并设置到返回封装类中
            Long employeeId = ipmsSaleBill.getEmployeeId();
            if (employeeId != null && employeeId > 0) {
                IpmsEmployee employee = ipmsEmployeeService.getById(employeeId);
                if (employee == null) {
                    throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "找不到职员信息，系统业务错误");
                }
                safeSaleBillVO.setEmployeeId(employeeId);
                safeSaleBillVO.setEmployeeName(employee.getEmployeeName());
            }
            // 查询部门信息，并设置到返回封装类中
            Long departmentId = ipmsSaleBill.getDepartmentId();
            if (departmentId != null && departmentId > 0) {
                IpmsDepartment department = ipmsDepartmentService.getById(departmentId);
                if (department == null) {
                    throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "找不到部门信息，系统业务错误");
                }
                safeSaleBillVO.setDepartmentId(departmentId);
                safeSaleBillVO.setDepartmentName(department.getDepartmentName());
            }
            // 查询销售单据的商品信息，并设置到返回封装类中
            Long saleBillId = ipmsSaleBill.getSaleBillId();
            if (saleBillId != null && saleBillId > 0) {
                QueryWrapper<IpmsSaleBillProductNum> saleBillProductNumQueryWrapper = new QueryWrapper<>();
                saleBillProductNumQueryWrapper.eq("sale_bill_id", saleBillId);
                List<IpmsSaleBillProductNum> saleBillProductList = ipmsSaleBillProductNumService.list(saleBillProductNumQueryWrapper);
                List<SafeSaleBillProductNumVO> safeSaleBillProductVOList = new ArrayList<>();
                if (saleBillProductList != null && saleBillProductList.size() > 0) {
                    for (IpmsSaleBillProductNum saleBillProduct : saleBillProductList) {
                        SafeSaleBillProductNumVO safeSaleBillProductVO = new SafeSaleBillProductNumVO();
                        BeanUtils.copyProperties(saleBillProduct, safeSaleBillProductVO);
                        // 在销售商品中查询商品等信息，并设置到返回封装类中
                        QueryWrapper<IpmsProductInventory> productInventoryQueryWrapper = new QueryWrapper<>();
                        Long productId = saleBillProduct.getProductId();
                        if (productId != null && productId > 0) {
                            IpmsProduct product = ipmsProductService.getById(productId);
                            if (product == null) {
                                throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "找不到商品，系统业务逻辑错误");
                            }
                            Long unitId = product.getUnitId();
                            if (unitId == null) {
                                throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "商品单位 id 为空，系统业务逻辑错误");
                            }
                            IpmsUnit productUnit = ipmsUnitService.getById(unitId);
                            if (productUnit == null) {
                                throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "商品单位为空，系统业务逻辑错误");
                            }
                            SafeProductVO safeProductVO = new SafeProductVO();
                            BeanUtils.copyProperties(product, safeProductVO);
                            safeProductVO.setUnitName(productUnit.getUnitName());
                            safeSaleBillProductVO.setSafeProductVO(safeProductVO);
                            productInventoryQueryWrapper.eq("product_id", productId);
                        }
                        Long warehouseId = saleBillProduct.getWarehouseId();
                        if (warehouseId != null && warehouseId > 0) {
                            IpmsWarehouse warehouse = ipmsWarehouseService.getById(warehouseId);
                            if (warehouse == null) {
                                throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "找不到仓库，系统业务逻辑错误");
                            }
                            safeSaleBillProductVO.setWarehouseId(warehouseId);
                            safeSaleBillProductVO.setWarehouseName(warehouse.getWarehouseName());
                            productInventoryQueryWrapper.eq("warehouse_id", warehouseId);
                        }
                        Long warehousePositionId = saleBillProduct.getWarehousePositionId();
                        if (warehousePositionId != null && warehousePositionId > 0) {
                            IpmsWarehousePosition warehousePosition = ipmsWarehousePositionService.getById(warehousePositionId);
                            if (warehousePosition != null) {
                                safeSaleBillProductVO.setWarehousePositionId(warehousePositionId);
                                safeSaleBillProductVO.setWarehousePositionName(warehousePosition.getWarehousePositionName());
                                productInventoryQueryWrapper.eq("warehouse_position_id", warehousePositionId);
                            }
                        }
                        // 查询库存
                        IpmsProductInventory productInventory = ipmsProductInventoryService.getOne(productInventoryQueryWrapper);
                        if (productInventory != null) {
                            safeSaleBillProductVO.setAvailableInventory(productInventory.getProductInventorySurplusNum());
                        }
                        safeSaleBillProductVOList.add(safeSaleBillProductVO);
                    }
                }
                safeSaleBillVO.setSafeSaleBillProductNumVOList(safeSaleBillProductVOList);
            }
            // 单据时间格式化
            safeSaleBillVO.setCreateTime(TimeFormatUtil.dateFormatting(ipmsSaleBill.getCreateTime()));
            safeSaleBillVO.setUpdateTime(TimeFormatUtil.dateFormatting(ipmsSaleBill.getUpdateTime()));
            safeSaleBillVO.setCheckTime(TimeFormatUtil.dateFormatting(ipmsSaleBill.getCheckTime()));
            return safeSaleBillVO;
        }).collect(Collectors.toList());
        // 关键步骤
        Page<SafeSaleBillVO> safeSaleBillVOPage = new PageDTO<>(saleBillPage.getCurrent(), saleBillPage.getSize(), saleBillPage.getTotal());
        safeSaleBillVOPage.setRecords(safeSaleBillVOList);
        return safeSaleBillVOPage;
    }
}




