package com.clarity.ipmsbackend.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.plugins.pagination.PageDTO;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.clarity.ipmsbackend.common.Constant;
import com.clarity.ipmsbackend.common.ErrorCode;
import com.clarity.ipmsbackend.common.PurchaseBillQueryRequest;
import com.clarity.ipmsbackend.constant.PurchaseBillConstant;
import com.clarity.ipmsbackend.exception.BusinessException;
import com.clarity.ipmsbackend.mapper.IpmsPurchaseBillMapper;
import com.clarity.ipmsbackend.model.dto.purchasebill.AddPurchaseBillRequest;
import com.clarity.ipmsbackend.model.dto.purchasebill.UpdatePurchaseBillRequest;
import com.clarity.ipmsbackend.model.dto.purchasebill.productnum.AddProductNumRequest;
import com.clarity.ipmsbackend.model.dto.purchasebill.productnum.UpdateProductNumRequest;
import com.clarity.ipmsbackend.model.entity.*;
import com.clarity.ipmsbackend.model.vo.*;
import com.clarity.ipmsbackend.model.vo.purchasebill.SafePurchaseBillVO;
import com.clarity.ipmsbackend.model.vo.purchasebill.productnum.SafePurchaseBillProductNumVO;
import com.clarity.ipmsbackend.service.*;
import com.clarity.ipmsbackend.utils.CodeAutoGenerator;
import com.clarity.ipmsbackend.utils.SplitUtil;
import com.clarity.ipmsbackend.utils.TimeFormatUtil;
import com.clarity.ipmsbackend.utils.ValidType;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author Clarity
 * @description 针对表【ipms_purchase_bill(采购单据)】的数据库操作Service实现
 * @createDate 2023-03-13 15:59:08
 */
@Service
@Slf4j
public class IpmsPurchaseBillServiceImpl extends ServiceImpl<IpmsPurchaseBillMapper, IpmsPurchaseBill>
        implements IpmsPurchaseBillService {

    @Resource
    private IpmsPurchaseBillMapper ipmsPurchaseBillMapper;

    @Resource
    private IpmsSupplierService ipmsSupplierService;

    @Resource
    private IpmsEmployeeService ipmsEmployeeService;

    @Resource
    private IpmsDepartmentService ipmsDepartmentService;

    @Resource
    private IpmsUserService ipmsUserService;

    @Resource
    private IpmsPurchaseBillProductNumService ipmsPurchaseBillProductNumService;

    @Resource
    private IpmsProductInventoryService ipmsProductInventoryService;

    @Resource
    private IpmsSupplierLinkmanService ipmsSupplierLinkmanService;

    @Resource
    private IpmsProductService ipmsProductService;

    @Resource
    private IpmsWarehouseService ipmsWarehouseService;

    @Resource
    private IpmsWarehousePositionService ipmsWarehousePositionService;

    @Resource
    private IpmsUnitService ipmsUnitService;

    @Override
    public String purchaseBillCodeAutoGenerate(String purchaseBillType) {
        if (purchaseBillType == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "单据类型为空，无法生成对于的单据编码");
        }
        QueryWrapper<IpmsPurchaseBill> ipmsPurchaseBillQueryWrapper = new QueryWrapper<>();
        ipmsPurchaseBillQueryWrapper.eq("purchase_bill_type", purchaseBillType);
        List<IpmsPurchaseBill> ipmsPurchaseBillList;
        String purchaseBillCode;
        String purchaseBillCodePrefix;
        String purchaseBillCodeInfix;
        String purchaseBillCodeSuffix;
        switch (purchaseBillType) {
            case PurchaseBillConstant.PURCHASE_ORDER:
                ipmsPurchaseBillList = ipmsPurchaseBillMapper.selectList(ipmsPurchaseBillQueryWrapper);
                if (ipmsPurchaseBillList.size() == 0) {
                    purchaseBillCodePrefix = "CGDD";
                    purchaseBillCodeInfix = TimeFormatUtil.dateFormat(new Date());
                    purchaseBillCodeSuffix = "0";
                } else {
                    IpmsPurchaseBill lastPurchaseBill = ipmsPurchaseBillList.get(ipmsPurchaseBillList.size() - 1);
                    purchaseBillCode = lastPurchaseBill.getPurchaseBillCode();
                    String[] purchaseOrderCode = SplitUtil.codeSplitByMinusSign(purchaseBillCode);
                    purchaseBillCodePrefix = purchaseOrderCode[0];
                    purchaseBillCodeInfix = purchaseOrderCode[1];
                    purchaseBillCodeSuffix = purchaseOrderCode[2];
                }
                break;
            case PurchaseBillConstant.PURCHASE_RECEIPT_ORDER:
                ipmsPurchaseBillList = ipmsPurchaseBillMapper.selectList(ipmsPurchaseBillQueryWrapper);
                if (ipmsPurchaseBillList.size() == 0) {
                    purchaseBillCodePrefix = "CGRK";
                    purchaseBillCodeInfix = TimeFormatUtil.dateFormat(new Date());
                    purchaseBillCodeSuffix = "0";
                } else {
                    IpmsPurchaseBill lastPurchaseBill = ipmsPurchaseBillList.get(ipmsPurchaseBillList.size() - 1);
                    purchaseBillCode = lastPurchaseBill.getPurchaseBillCode();
                    String[] purchaseOrderCode = SplitUtil.codeSplitByMinusSign(purchaseBillCode);
                    purchaseBillCodePrefix = purchaseOrderCode[0];
                    purchaseBillCodeInfix = purchaseOrderCode[1];
                    purchaseBillCodeSuffix = purchaseOrderCode[2];
                }
                break;
            case PurchaseBillConstant.PURCHASE_RETURN_ORDER:
                ipmsPurchaseBillList = ipmsPurchaseBillMapper.selectList(ipmsPurchaseBillQueryWrapper);
                if (ipmsPurchaseBillList.size() == 0) {
                    purchaseBillCodePrefix = "CGTH";
                    purchaseBillCodeInfix = TimeFormatUtil.dateFormat(new Date());
                    purchaseBillCodeSuffix = "0";
                } else {
                    IpmsPurchaseBill lastPurchaseBill = ipmsPurchaseBillList.get(ipmsPurchaseBillList.size() - 1);
                    purchaseBillCode = lastPurchaseBill.getPurchaseBillCode();
                    String[] purchaseOrderCode = SplitUtil.codeSplitByMinusSign(purchaseBillCode);
                    purchaseBillCodePrefix = purchaseOrderCode[0];
                    purchaseBillCodeInfix = purchaseOrderCode[1];
                    purchaseBillCodeSuffix = purchaseOrderCode[2];
                }
                break;
            default:
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "单据类型不存在");
        }
        String nextPurchaseBillCode = null;
        try {
            nextPurchaseBillCode = CodeAutoGenerator.generatorCode(purchaseBillCodePrefix, purchaseBillCodeInfix, purchaseBillCodeSuffix);
        } catch (Exception e) {
            log.info("编码自动生成器异常");
        }
        return nextPurchaseBillCode;
    }

    @Override
    @Transactional
    public int addPurchaseBill(AddPurchaseBillRequest addPurchaseBillRequest, HttpServletRequest request) {
        // 1. 单据编号不能为空，且必须不能重复
        String purchaseBillCode = addPurchaseBillRequest.getPurchaseBillCode();
        if (purchaseBillCode == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "单据编号为空");
        }
        QueryWrapper<IpmsPurchaseBill> purchaseBillQueryWrapper = new QueryWrapper<>();
        purchaseBillQueryWrapper.eq("purchase_bill_code", purchaseBillCode);
        IpmsPurchaseBill validCodePurchaseBill = ipmsPurchaseBillMapper.selectOne(purchaseBillQueryWrapper);
        if (validCodePurchaseBill != null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "单据编号重复");
        }
        // 2. 单据日期不能为空
        String purchaseBillDate = addPurchaseBillRequest.getPurchaseBillDate();
        if (purchaseBillDate == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "单据日期为空");
        }
        // 3. 结算日期不能为空
        String purchaseBillSettlementDate = addPurchaseBillRequest.getPurchaseBillSettlementDate();
        if (purchaseBillSettlementDate == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "单据结算日期为空");
        }
        // 4. 供应商 id 不能为空，且必须存在该供应商
        Long supplierId = addPurchaseBillRequest.getSupplierId();
        if (supplierId == null || supplierId <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "单据供应商 id 为空或者不合法");
        }
        IpmsSupplier validSupplier = ipmsSupplierService.getById(supplierId);
        if (validSupplier == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "单据供应商不存在");
        }
        // 5. 职员 id 和 部门 id，可以为空，如果不为空，那么必须存在对于的职员和部门信息
        Long employeeId = addPurchaseBillRequest.getEmployeeId();
        if (employeeId != null) {
            IpmsEmployee validEmployee = ipmsEmployeeService.getById(employeeId);
            if (validEmployee == null) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "单据职员不存在");
            }
        }
        Long departmentId = addPurchaseBillRequest.getDepartmentId();
        if (departmentId != null) {
            IpmsDepartment validDepartment = ipmsDepartmentService.getById(departmentId);
            if (validDepartment == null) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "单据部门不存在");
            }
        }
        // 6. 成交金额一定存在，且必须大于 0
        BigDecimal purchaseBillTransactionAmount = addPurchaseBillRequest.getPurchaseBillTransactionAmount();
        if (purchaseBillTransactionAmount == null || purchaseBillTransactionAmount.doubleValue() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "单据成交金额一定要大于 0");
        }
        // 7. 采购单据类型不能为空，且必须符合系统要求
        String purchaseBillType = addPurchaseBillRequest.getPurchaseBillType();
        if (purchaseBillType == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "单据类型不能为空");
        }
        List<String> billTypeList = Arrays.asList(PurchaseBillConstant.PURCHASE_ORDER, PurchaseBillConstant.PURCHASE_RECEIPT_ORDER, PurchaseBillConstant.PURCHASE_RETURN_ORDER);
        int validTypeResult = ValidType.valid(billTypeList, purchaseBillType);
        if (validTypeResult == 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "单据类型不存在");
        }
        // 8. 采购单据的商品及商品数量至少存在一个
        List<AddProductNumRequest> addProductNumRequestList = addPurchaseBillRequest.getAddProductNumRequestList();
        if (addProductNumRequestList == null || addProductNumRequestList.size() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "采购的商品为空，至少要存在一个采购商品");
        }
        // 并且验证单据成交金额是否一致
        double validPurchaseBillTransactionAmount = 0;
        for (AddProductNumRequest addProductNumRequest : addProductNumRequestList) {
            // 商品数量，不能为空，且必须大于 0
            BigDecimal productNum = addProductNumRequest.getProductNum();
            if (productNum == null || productNum.doubleValue() <= 0) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "商品数量为空或者小于等于 0");
            }
            // 商品单价也是，价格合计也是
            BigDecimal unitPrice = addProductNumRequest.getUnitPrice();
            BigDecimal totalPrice = addProductNumRequest.getTotalPrice();
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
            validPurchaseBillTransactionAmount += totalPrice.doubleValue();
        }
        if (validPurchaseBillTransactionAmount != purchaseBillTransactionAmount.doubleValue()) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "前后端计算单据成交金额不一致");
        }
        // 单据必须审核后才能被当作单源来使用
        Long purchaseSourceBillId = addPurchaseBillRequest.getPurchaseSourceBillId();
        if (purchaseSourceBillId != null && purchaseSourceBillId > 0) {
            QueryWrapper<IpmsPurchaseBill> ipmsPurchaseBillQueryWrapper = new QueryWrapper<>();
            ipmsPurchaseBillQueryWrapper.eq("purchase_bill_id", purchaseSourceBillId);
            IpmsPurchaseBill validCheckStatePurchaseBill = ipmsPurchaseBillMapper.selectOne(ipmsPurchaseBillQueryWrapper);
            Integer checkState = validCheckStatePurchaseBill.getCheckState();
            if (!checkState.equals(Constant.CHECKED)) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "选择的采购单源未审核");
            }
        }
        // 9. 增加数据，并且设置其他固定字段
        IpmsPurchaseBill purchaseBill = new IpmsPurchaseBill();
        BeanUtils.copyProperties(addPurchaseBillRequest, purchaseBill);
        SafeUserVO loginUser = ipmsUserService.getLoginUser(request);
        purchaseBill.setFounder(loginUser.getUserName());
        purchaseBill.setCreateTime(new Date());
        purchaseBill.setUpdateTime(new Date());
        int insertResult = ipmsPurchaseBillMapper.insert(purchaseBill);
        if (insertResult != 1) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR);
        }
        // 10. 最后调用增加采购单据商品及数量
        for (AddProductNumRequest addProductNumRequest : addProductNumRequestList) {
            long addPurchaseBillProductAndNumResult = ipmsPurchaseBillProductNumService.addPurchaseBillProductAndNum(addProductNumRequest, purchaseBill);
            if (addPurchaseBillProductAndNumResult < 0) {
                throw new BusinessException(ErrorCode.SYSTEM_ERROR, "采购单据商品及数量插入失败");
            }
        }
        // 11. 采购入库单增加并且还没审核就会影响采购订单的执行状态和关闭状态
        //     采购订单要么部分执行，还未关闭；要么完全执行并且关闭
        if (purchaseBillType.equals(PurchaseBillConstant.PURCHASE_RECEIPT_ORDER) && purchaseSourceBillId != null && purchaseSourceBillId > 0) {
            IpmsPurchaseBill sourcePurchaseBill = ipmsPurchaseBillMapper.selectById(purchaseSourceBillId);
            QueryWrapper<IpmsPurchaseBillProductNum> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("purchase_bill_id", sourcePurchaseBill.getPurchaseBillId());
            List<IpmsPurchaseBillProductNum> sourcePurchaseBillProductList = ipmsPurchaseBillProductNumService.list(queryWrapper);
            int temp = 0;
            for (IpmsPurchaseBillProductNum purchaseBillProductNum : sourcePurchaseBillProductList) {
                temp++;
                if (purchaseBillProductNum.getSurplusNeedWarehousingProductNum().doubleValue() != 0) {
                    purchaseBill = new IpmsPurchaseBill();
                    purchaseBill.setPurchaseBillId(purchaseSourceBillId);
                    purchaseBill.setExecutionState(Constant.PART_OPERATED);
                    ipmsPurchaseBillMapper.updateById(purchaseBill);
                    break;
                }
                if (temp == sourcePurchaseBillProductList.size()) {
                    purchaseBill = new IpmsPurchaseBill();
                    purchaseBill.setPurchaseBillId(purchaseSourceBillId);
                    purchaseBill.setExecutionState(Constant.FULL_OPERATED);
                    purchaseBill.setOffState(Constant.CLOSED);
                    ipmsPurchaseBillMapper.updateById(purchaseBill);
                }
            }
        }
        return insertResult;
    }

    @Override
    @Transactional
    public int checkPurchaseBill(long purchaseBillId, HttpServletRequest request) {
        if (purchaseBillId <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "单据 id 不合法");
        }
        IpmsPurchaseBill purchaseBill = ipmsPurchaseBillMapper.selectById(purchaseBillId);
        if (purchaseBill == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }
        Integer checkState = purchaseBill.getCheckState();
        if (Constant.CHECKED == checkState) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "单据已审核");
        }
        String purchaseBillType = purchaseBill.getPurchaseBillType();
        IpmsPurchaseBill checkingPurchaseBill = new IpmsPurchaseBill();
        checkingPurchaseBill.setPurchaseBillId(purchaseBill.getPurchaseBillId());
        checkingPurchaseBill.setCheckState(Constant.CHECKED);
        // 定义一个变量用于存放采购入库单或者采购退货单，供应商应该增加或者减少企业应付款金额
        double enterprisePayBalance = 0;
        if (PurchaseBillConstant.PURCHASE_RECEIPT_ORDER.equals(purchaseBillType)) {
            // 审核后要修改采购订单的入库状态
            QueryWrapper<IpmsPurchaseBill> purchaseBillQueryWrapper = new QueryWrapper<>();
            purchaseBillQueryWrapper.eq("purchase_source_bill_id", purchaseBill.getPurchaseSourceBillId());
            purchaseBillQueryWrapper.eq("check_state", Constant.CHECKED);
            List<IpmsPurchaseBill> checkedPurchaseBillList = ipmsPurchaseBillMapper.selectList(purchaseBillQueryWrapper);
            List<Long> checkedPurchaseBillIdList = new ArrayList<>();
            for (IpmsPurchaseBill checkPurchaseBill : checkedPurchaseBillList) {
                checkedPurchaseBillIdList.add(checkPurchaseBill.getPurchaseBillId());
            }
            QueryWrapper<IpmsPurchaseBillProductNum> purchaseBillProductNumQueryWrapper = new QueryWrapper<>();
            double checkedPurchaseBillProductNum = 0;
            if (checkedPurchaseBillIdList.size() > 0) {
                purchaseBillProductNumQueryWrapper.in("purchase_bill_id", checkedPurchaseBillIdList);
                List<IpmsPurchaseBillProductNum> checkedPurchaseBillProductList = ipmsPurchaseBillProductNumService.list(purchaseBillProductNumQueryWrapper);
                for (IpmsPurchaseBillProductNum checkedPurchaseBillProduct : checkedPurchaseBillProductList) {
                    checkedPurchaseBillProductNum += checkedPurchaseBillProduct.getNeedWarehousingProductNum().doubleValue();
                }
            }
            purchaseBillProductNumQueryWrapper = new QueryWrapper<>();
            purchaseBillProductNumQueryWrapper.eq("purchase_bill_id", purchaseBillId);
            List<IpmsPurchaseBillProductNum> currentCheckingPurchaseBillProductList = ipmsPurchaseBillProductNumService.list(purchaseBillProductNumQueryWrapper);
            for (IpmsPurchaseBillProductNum currentCheckingPurchaseBillProduct : currentCheckingPurchaseBillProductList) {
                checkedPurchaseBillProductNum += currentCheckingPurchaseBillProduct.getNeedWarehousingProductNum().doubleValue();
                // 并且调用增加库存的方法
                BigDecimal onePurchaseBillProductCost = ipmsProductInventoryService.addProductInventory(currentCheckingPurchaseBillProduct, purchaseBill.getPurchaseBillExchangeRate());
                enterprisePayBalance += onePurchaseBillProductCost.doubleValue();
                if (onePurchaseBillProductCost.doubleValue() <= 0) {
                    throw new BusinessException(ErrorCode.SYSTEM_ERROR, "调用增加库存的方法失败");
                }
            }
            double needWarehousingProductNum = 0;
            purchaseBillProductNumQueryWrapper = new QueryWrapper<>();
            purchaseBillProductNumQueryWrapper.eq("purchase_bill_id", purchaseBill.getPurchaseSourceBillId());
            List<IpmsPurchaseBillProductNum> sourcePurchaseBillProductList = ipmsPurchaseBillProductNumService.list(purchaseBillProductNumQueryWrapper);
            for (IpmsPurchaseBillProductNum sourcePurchaseBillProduct : sourcePurchaseBillProductList) {
                needWarehousingProductNum += sourcePurchaseBillProduct.getNeedWarehousingProductNum().doubleValue();
            }
            IpmsPurchaseBill sourcePurchaseBill = ipmsPurchaseBillMapper.selectById(purchaseBill.getPurchaseSourceBillId());
            if (sourcePurchaseBill != null) {
                if (checkedPurchaseBillProductNum == needWarehousingProductNum) {
                    sourcePurchaseBill.setWarehousingState(Constant.FULL_OPERATED);
                } else {
                    sourcePurchaseBill.setWarehousingState(Constant.PART_OPERATED);
                }
                int validSourceState = ipmsPurchaseBillMapper.updateById(sourcePurchaseBill);
                if (validSourceState != 1) {
                    throw new BusinessException(ErrorCode.SYSTEM_ERROR, "入库状态更新失败");
                }
            }
            // 调用供应商模块业务接口，实现金额的增加
            int result = ipmsSupplierService.addEnterprisePayBalance(purchaseBill.getSupplierId(), enterprisePayBalance);
            if (result != 1) {
                throw new BusinessException(ErrorCode.SYSTEM_ERROR, "增加供应商金额失败");
            }
        } else if (PurchaseBillConstant.PURCHASE_RETURN_ORDER.equals(purchaseBillType)) {
            // 采购退货单审核后要调用减少库存的方法
            QueryWrapper<IpmsPurchaseBillProductNum> purchaseBillProductNumQueryWrapper = new QueryWrapper<>();
            purchaseBillProductNumQueryWrapper.eq("purchase_bill_id", purchaseBillId);
            List<IpmsPurchaseBillProductNum> purchaseReturnProductList = ipmsPurchaseBillProductNumService.list(purchaseBillProductNumQueryWrapper);
            for (IpmsPurchaseBillProductNum purchaseReturnProduct : purchaseReturnProductList) {
                BigDecimal onePurchaseBillProductCost = ipmsProductInventoryService.reduceProductInventory(purchaseReturnProduct, purchaseBill.getPurchaseBillExchangeRate());
                enterprisePayBalance += onePurchaseBillProductCost.doubleValue();
                if (onePurchaseBillProductCost.doubleValue() <= 0) {
                    throw new BusinessException(ErrorCode.SYSTEM_ERROR, "调用减少库存的方法失败");
                }
            }
            // 调用供应商模块减少商品
            int result = ipmsSupplierService.reduceEnterprisePayBalance(purchaseBill.getSupplierId(), enterprisePayBalance);
            if (result != 1) {
                throw new BusinessException(ErrorCode.SYSTEM_ERROR, "减少供应商金额失败");
            }
        }
        SafeUserVO loginUser = ipmsUserService.getLoginUser(request);
        checkingPurchaseBill.setChecker(loginUser.getUserName());
        checkingPurchaseBill.setCheckTime(new Date());
        int result = ipmsPurchaseBillMapper.updateById(checkingPurchaseBill);
        if (result != 1) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR);
        }
        return result;
    }

    @Override
    @Transactional
    public int reverseCheckPurchaseBill(long purchaseBillId, HttpServletRequest request) {
        if (purchaseBillId <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "单据 id 不合法");
        }
        IpmsPurchaseBill purchaseBill = ipmsPurchaseBillMapper.selectById(purchaseBillId);
        if (purchaseBill == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }
        if (Constant.UNCHECKED == purchaseBill.getCheckState()) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "单据未审核");
        }
        String purchaseBillType = purchaseBill.getPurchaseBillType();
        IpmsPurchaseBill unCheckingPurchaseBill = new IpmsPurchaseBill();
        unCheckingPurchaseBill.setPurchaseBillId(purchaseBill.getPurchaseBillId());
        unCheckingPurchaseBill.setCheckState(Constant.UNCHECKED);
        unCheckingPurchaseBill.setChecker(null);
        unCheckingPurchaseBill.setCreateTime(null);
        // 如果采购订单已经作为其他单据的源单，那么无法反审核，关闭的采购订单更是无法反审核，因为它肯定拥有源单
        // 采购入库单第一种情况没有选单源并且也是已经作为其他单据的源单，那么无法反审核。
        // 采购退货单第一种情况没有选单源并且也是已经作为其他单据的源单，那么无法反审核。（没有这种情况，采购退库单不作为其他单源）
        QueryWrapper<IpmsPurchaseBill> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("purchase_source_bill_id", purchaseBillId);
        List<IpmsPurchaseBill> isAsSourceBill = ipmsPurchaseBillMapper.selectList(queryWrapper);
        if (isAsSourceBill != null && isAsSourceBill.size() > 0) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "单据已经作为其他单据的单源使用");
        }
        // 定义一个变量用于存放采购入库单或者采购退货单，供应商应该增加或者减少企业应付款金额
        double enterprisePayBalance = 0;
        if (PurchaseBillConstant.PURCHASE_RECEIPT_ORDER.equals(purchaseBillType)) {
            // 反审核的话必须将对于的源单剩余还有多少商品未被引用的数量恢复
            // 1. 遍历采购入库单入库商品及数量
            QueryWrapper<IpmsPurchaseBillProductNum> purchaseBillProductNumQueryWrapper = new QueryWrapper<>();
            purchaseBillProductNumQueryWrapper.eq("purchase_bill_id", purchaseBillId);
            List<IpmsPurchaseBillProductNum> purchaseBillReceiptProductList = ipmsPurchaseBillProductNumService.list(purchaseBillProductNumQueryWrapper);
            // 2. 遍历里面的每一个商品与源单商品做对比相同的数量恢复，并且减少库存
            for (IpmsPurchaseBillProductNum purchaseBillReceiptProduct : purchaseBillReceiptProductList) {
                BigDecimal onePurchaseBillProductCost = ipmsProductInventoryService.reduceProductInventory(purchaseBillReceiptProduct, purchaseBill.getPurchaseBillExchangeRate());
                enterprisePayBalance += onePurchaseBillProductCost.doubleValue();
                if (onePurchaseBillProductCost.doubleValue() <= 0) {
                    throw new BusinessException(ErrorCode.SYSTEM_ERROR, "采购入库单反审核减少库存失败");
                }
            }
            // 修改源单状态为原来的状态
            // 1. 查询已经审核的采购入库单的所有商品，如果采购入库单数量大于 1 且全部是已审核的单据，那把入库状态改为部分入库
            //    如果采购入库单已审核的单据数量大于 1 且只剩下 1 单了，那么入库状态改为，未入库
            Long purchaseSourceBillId = purchaseBill.getPurchaseSourceBillId();
            if (purchaseSourceBillId != null && purchaseSourceBillId > 0) {
                queryWrapper = new QueryWrapper<>();
                queryWrapper.eq("purchase_source_bill_id", purchaseSourceBillId);
                queryWrapper.eq("check_state", Constant.CHECKED);
                long checkedCount = ipmsPurchaseBillMapper.selectCount(queryWrapper);
                queryWrapper = new QueryWrapper<>();
                queryWrapper.eq("purchase_source_bill_id", purchaseSourceBillId);
                Long checkedAndUncheckedCount = ipmsPurchaseBillMapper.selectCount(queryWrapper);
                IpmsPurchaseBill sourcePurchaseBill;
                if (checkedAndUncheckedCount == 1) {
                    sourcePurchaseBill = new IpmsPurchaseBill();
                    sourcePurchaseBill.setPurchaseBillId(purchaseSourceBillId);
                    sourcePurchaseBill.setWarehousingState(Constant.NOT_OPERATED);
                    int result = ipmsPurchaseBillMapper.updateById(sourcePurchaseBill);
                    if (result != 1) {
                        throw new BusinessException(ErrorCode.SYSTEM_ERROR, "反审核采购入库单，状态修改失败");
                    }
                } else if (checkedAndUncheckedCount > 1 && checkedCount > 1) {
                    sourcePurchaseBill = new IpmsPurchaseBill();
                    sourcePurchaseBill.setPurchaseBillId(purchaseSourceBillId);
                    sourcePurchaseBill.setWarehousingState(Constant.PART_OPERATED);
                    int result = ipmsPurchaseBillMapper.updateById(sourcePurchaseBill);
                    if (result != 1) {
                        throw new BusinessException(ErrorCode.SYSTEM_ERROR, "反审核采购入库单，状态修改失败");
                    }
                } else {
                    sourcePurchaseBill = new IpmsPurchaseBill();
                    sourcePurchaseBill.setPurchaseBillId(purchaseSourceBillId);
                    sourcePurchaseBill.setWarehousingState(Constant.NOT_OPERATED);
                    int result = ipmsPurchaseBillMapper.updateById(sourcePurchaseBill);
                    if (result != 1) {
                        throw new BusinessException(ErrorCode.SYSTEM_ERROR, "反审核采购入库单，状态修改失败");
                    }
                }
            }
            // 调用供应商模块减少商品
            int result = ipmsSupplierService.reduceEnterprisePayBalance(purchaseBill.getSupplierId(), enterprisePayBalance);
            if (result != 1) {
                throw new BusinessException(ErrorCode.SYSTEM_ERROR, "减少供应商金额失败");
            }
        } else if (PurchaseBillConstant.PURCHASE_RETURN_ORDER.equals(purchaseBillType)) {
            // 采购退货单反审核后要调用增加库存的方法
            // 1. 遍历采购退货单商品及数量
            QueryWrapper<IpmsPurchaseBillProductNum> purchaseBillProductNumQueryWrapper = new QueryWrapper<>();
            purchaseBillProductNumQueryWrapper.eq("purchase_bill_id", purchaseBillId);
            List<IpmsPurchaseBillProductNum> purchaseBillReturnProductList = ipmsPurchaseBillProductNumService.list(purchaseBillProductNumQueryWrapper);
            // 2. 遍历里面的每一个商品与源单商品做对比相同的数量恢复，并且增加库存
            for (IpmsPurchaseBillProductNum purchaseBillReturnProduct : purchaseBillReturnProductList) {
                BigDecimal onePurchaseBillProductCost = ipmsProductInventoryService.addProductInventory(purchaseBillReturnProduct, purchaseBill.getPurchaseBillExchangeRate());
                enterprisePayBalance += onePurchaseBillProductCost.doubleValue();
                if (onePurchaseBillProductCost.doubleValue() <= 0) {
                    throw new BusinessException(ErrorCode.SYSTEM_ERROR, "采购退货单反审核增加库存失败");
                }
            }
            // 调用供应商模块业务接口，实现金额的增加
            int result = ipmsSupplierService.addEnterprisePayBalance(purchaseBill.getSupplierId(), enterprisePayBalance);
            if (result != 1) {
                throw new BusinessException(ErrorCode.SYSTEM_ERROR, "增加供应商金额失败");
            }
        }
        SafeUserVO loginUser = ipmsUserService.getLoginUser(request);
        unCheckingPurchaseBill.setChecker(loginUser.getUserName());
        unCheckingPurchaseBill.setCheckTime(new Date());
        int result = ipmsPurchaseBillMapper.updateById(unCheckingPurchaseBill);
        if (result != 1) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR);
        }
        return result;
    }

    @Override
    @Transactional
    public int deletePurchaseBillById(long id) {
        // 1. 判断 id 是否合法
        if (id <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "id 不合法");
        }
        // 2. 审核后的单据无法删除
        IpmsPurchaseBill oldPurchaseBill = ipmsPurchaseBillMapper.selectById(id);
        if (oldPurchaseBill == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }
        if (oldPurchaseBill.getCheckState().equals(Constant.CHECKED)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "单据已经审核无法删除");
        }
        // 3. 按照单据类型分类别删除
        String purchaseBillType = oldPurchaseBill.getPurchaseBillType();
        Long purchaseSourceBillId = oldPurchaseBill.getPurchaseSourceBillId();
        QueryWrapper<IpmsPurchaseBillProductNum> purchaseBillProductNumQueryWrapper;
        if (PurchaseBillConstant.PURCHASE_RECEIPT_ORDER.equals(purchaseBillType)) {
            if (purchaseSourceBillId != null) {
                purchaseBillProductNumQueryWrapper = new QueryWrapper<>();
                purchaseBillProductNumQueryWrapper.eq("purchase_bill_id", id);
                List<IpmsPurchaseBillProductNum> purchaseReceiptBillProductList = ipmsPurchaseBillProductNumService.list(purchaseBillProductNumQueryWrapper);
                for (IpmsPurchaseBillProductNum purchaseReceiptBillProductNum : purchaseReceiptBillProductList) {
                    purchaseBillProductNumQueryWrapper = new QueryWrapper<>();
                    purchaseBillProductNumQueryWrapper.eq("purchase_bill_id", purchaseSourceBillId);
                    purchaseBillProductNumQueryWrapper.eq("product_id", purchaseReceiptBillProductNum.getProductId());
                    purchaseBillProductNumQueryWrapper.eq("warehouse_id", purchaseReceiptBillProductNum.getWarehouseId());
                    if (purchaseReceiptBillProductNum.getWarehousePositionId() != null) {
                        purchaseBillProductNumQueryWrapper.eq("warehouse_position_id", purchaseReceiptBillProductNum.getWarehouseId());
                    }
                    // 采购订单的商品也就是源单的商品
                    IpmsPurchaseBillProductNum sourceProductOfOne = ipmsPurchaseBillProductNumService.getOne(purchaseBillProductNumQueryWrapper);
                    // 修改源单商品数量为未必采购入库单引用的剩余数量
                    if (sourceProductOfOne != null) {
                        BigDecimal oldSurplusNeedWarehousingProductNum = sourceProductOfOne.getSurplusNeedWarehousingProductNum();
                        sourceProductOfOne.setSurplusNeedWarehousingProductNum(oldSurplusNeedWarehousingProductNum.add(purchaseReceiptBillProductNum.getNeedWarehousingProductNum()));
                        boolean recoverSourceProductNumResult = ipmsPurchaseBillProductNumService.updateById(sourceProductOfOne);
                        if (!recoverSourceProductNumResult) {
                            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "恢复采购订单剩余数量失败");
                        }
                    }
                }
                // 修改采购订单状态
                purchaseBillProductNumQueryWrapper = new QueryWrapper<>();
                purchaseBillProductNumQueryWrapper.eq("purchase_bill_id", purchaseSourceBillId);
                List<IpmsPurchaseBillProductNum> sourceProductList = ipmsPurchaseBillProductNumService.list(purchaseBillProductNumQueryWrapper);
                int temp = 0;
                for (IpmsPurchaseBillProductNum sourceProduct : sourceProductList) {
                    temp++;
                    IpmsPurchaseBill sourcePurchaseBill;
                    if (!sourceProduct.getNeedWarehousingProductNum().equals(sourceProduct.getSurplusNeedWarehousingProductNum())) {
                        sourcePurchaseBill = new IpmsPurchaseBill();
                        sourcePurchaseBill.setPurchaseBillId(purchaseSourceBillId);
                        // 设置执行状态和关闭状态
                        sourcePurchaseBill.setExecutionState(Constant.PART_OPERATED);
                        sourcePurchaseBill.setOffState(Constant.NOT_CLOSED);
                        int result = ipmsPurchaseBillMapper.updateById(sourcePurchaseBill);
                        if (result != 1) {
                            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "删除采购入库单，状态修改失败");
                        }
                        break;
                    }
                    if (temp == sourceProductList.size()) {
                        sourcePurchaseBill = new IpmsPurchaseBill();
                        sourcePurchaseBill.setPurchaseBillId(purchaseSourceBillId);
                        sourcePurchaseBill.setExecutionState(Constant.NOT_OPERATED);
                        sourcePurchaseBill.setOffState(Constant.NOT_CLOSED);
                        int result = ipmsPurchaseBillMapper.updateById(sourcePurchaseBill);
                        if (result != 1) {
                            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "删除采购入库单，状态修改失败");
                        }
                    }
                }
            }
        } else if (PurchaseBillConstant.PURCHASE_RETURN_ORDER.equals(purchaseBillType)) {
            if (purchaseSourceBillId != null) {
                purchaseBillProductNumQueryWrapper = new QueryWrapper<>();
                purchaseBillProductNumQueryWrapper.eq("purchase_bill_id", id);
                List<IpmsPurchaseBillProductNum> purchaseReceiptBillProductList = ipmsPurchaseBillProductNumService.list(purchaseBillProductNumQueryWrapper);
                for (IpmsPurchaseBillProductNum purchaseReceiptBillProductNum : purchaseReceiptBillProductList) {
                    purchaseBillProductNumQueryWrapper = new QueryWrapper<>();
                    purchaseBillProductNumQueryWrapper.eq("purchase_bill_id", purchaseSourceBillId);
                    purchaseBillProductNumQueryWrapper.eq("product_id", purchaseReceiptBillProductNum.getProductId());
                    purchaseBillProductNumQueryWrapper.eq("warehouse_id", purchaseReceiptBillProductNum.getWarehouseId());
                    if (purchaseReceiptBillProductNum.getWarehousePositionId() != null) {
                        purchaseBillProductNumQueryWrapper.eq("warehouse_position_id", purchaseReceiptBillProductNum.getWarehouseId());
                    }
                    // 采购入库单的商品也就是源单的商品
                    IpmsPurchaseBillProductNum sourceProductOfOne = ipmsPurchaseBillProductNumService.getOne(purchaseBillProductNumQueryWrapper);
                    // 修改源单商品数量为未必采购入库单引用的剩余数量
                    if (sourceProductOfOne != null) {
                        BigDecimal oldSurplusNeedWarehousingProductNum = sourceProductOfOne.getSurplusNeedWarehousingProductNum();
                        sourceProductOfOne.setSurplusNeedWarehousingProductNum(oldSurplusNeedWarehousingProductNum.add(purchaseReceiptBillProductNum.getNeedWarehousingProductNum()));
                        boolean recoverSourceProductNumResult = ipmsPurchaseBillProductNumService.updateById(sourceProductOfOne);
                        if (!recoverSourceProductNumResult) {
                            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "恢复采购订单剩余数量失败");
                        }
                    }
                }
            }
        }
        int result = ipmsPurchaseBillMapper.deleteById(id);
        if (result != 1) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "删除失败");
        }
        purchaseBillProductNumQueryWrapper = new QueryWrapper<>();
        purchaseBillProductNumQueryWrapper.eq("purchase_bill_id", id);
        boolean remove = ipmsPurchaseBillProductNumService.remove(purchaseBillProductNumQueryWrapper);
        if (!remove) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "关联删除失败");
        }
        return result;
    }

    @Override
    @Transactional
    public int updatePurchaseBill(UpdatePurchaseBillRequest updatePurchaseBillRequest, HttpServletRequest request) {
        Long purchaseBillId = updatePurchaseBillRequest.getPurchaseBillId();
        if (purchaseBillId == null || purchaseBillId <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "单据 id 为空或者不合法");
        }
        // 判断该单据是否存在
        IpmsPurchaseBill oldPurchaseBill = ipmsPurchaseBillMapper.selectById(purchaseBillId);
        if (oldPurchaseBill == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "单据不存在");
        }
        Integer checkState = oldPurchaseBill.getCheckState();
        if (Constant.CHECKED == checkState) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "单据已审核，无法修改");
        }
        String purchaseBillCode = updatePurchaseBillRequest.getPurchaseBillCode();
        if (purchaseBillCode != null) {
            if (!purchaseBillCode.equals(oldPurchaseBill.getPurchaseBillCode())) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "单据编号不支持修改");
            }
        }
        Long supplierId = updatePurchaseBillRequest.getSupplierId();
        if (supplierId != null && supplierId > 0) {
            if (!supplierId.equals(oldPurchaseBill.getSupplierId())) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "无法修改单据所属供应商");
            }
        }
        Long employeeId = updatePurchaseBillRequest.getEmployeeId();
        Long departmentId = updatePurchaseBillRequest.getDepartmentId();
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
        String purchaseBillType = updatePurchaseBillRequest.getPurchaseBillType();
        if (purchaseBillType != null) {
            if (!purchaseBillType.equals(oldPurchaseBill.getPurchaseBillType())) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "单据类型不能修改");
            }
        }
        // 8. 采购单据的商品及商品数量至少存在一个
        List<UpdateProductNumRequest> updateProductNumRequestList = updatePurchaseBillRequest.getUpdateProductNumRequestList();
        if (updateProductNumRequestList == null || updateProductNumRequestList.size() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "采购的商品为空，至少要存在一个采购商品");
        }
        BigDecimal purchaseBillTransactionAmount = updatePurchaseBillRequest.getPurchaseBillTransactionAmount();
        if (purchaseBillTransactionAmount == null || purchaseBillTransactionAmount.doubleValue() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "单据成交金额一定要大于 0");
        }
        // 并且验证单据成交金额是否一致
        double validPurchaseBillTransactionAmount = 0;
        for (UpdateProductNumRequest updateProductNumRequest : updateProductNumRequestList) {
            // 商品数量，不能为空，且必须大于 0
            BigDecimal productNum = updateProductNumRequest.getProductNum();
            if (productNum == null || productNum.doubleValue() <= 0) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "商品数量为空或者小于等于 0");
            }
            // 商品单价也是，价格合计也是
            BigDecimal unitPrice = updateProductNumRequest.getUnitPrice();
            BigDecimal totalPrice = updateProductNumRequest.getTotalPrice();
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
            validPurchaseBillTransactionAmount += totalPrice.doubleValue();
        }
        if (validPurchaseBillTransactionAmount != purchaseBillTransactionAmount.doubleValue()) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "前后端计算单据成交金额不一致");
        }
        // 更新采购单据
        IpmsPurchaseBill newPurchaseBill = new IpmsPurchaseBill();
        BeanUtils.copyProperties(updatePurchaseBillRequest, newPurchaseBill);
        SafeUserVO loginUser = ipmsUserService.getLoginUser(request);
        newPurchaseBill.setModifier(loginUser.getUserName());
        newPurchaseBill.setUpdateTime(new Date());
        int result = ipmsPurchaseBillMapper.updateById(newPurchaseBill);
        if (result != 1) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "修改采购单据失败");
        }
        List<Long> updateAndInsertPurchaseBillProductList = new ArrayList<>();
        for (UpdateProductNumRequest updateProductNumRequest : updateProductNumRequestList) {
            Long purchaseBillProductId = updateProductNumRequest.getPurchaseBillProductId();
            // 如果存在 purchaseBillProductId，那么肯定是要更新的数据，进行更新，并且存入列表中
            if (purchaseBillProductId != null && purchaseBillProductId > 0) {
                updateAndInsertPurchaseBillProductList.add(purchaseBillProductId);
                ipmsPurchaseBillProductNumService.updatePurchaseBillProductAndNum(updateProductNumRequest, oldPurchaseBill);
            } else {
                // 否则，就是插入新的数据
                AddProductNumRequest addProductNumRequest = new AddProductNumRequest();
                BeanUtils.copyProperties(updateProductNumRequest, addProductNumRequest);
                long insertPurchaseBillProductId = ipmsPurchaseBillProductNumService.addPurchaseBillProductAndNum(addProductNumRequest, oldPurchaseBill);
                if (insertPurchaseBillProductId < 0) {
                    throw new BusinessException(ErrorCode.SYSTEM_ERROR, "增加采购单据商品失败");
                }
                updateAndInsertPurchaseBillProductList.add(insertPurchaseBillProductId);
            }
        }
        if (updateAndInsertPurchaseBillProductList.size() > 0) {
            // 如果更新采购单据商品的 id 不在这个列表内，那么删除采购单据
            QueryWrapper<IpmsPurchaseBillProductNum> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("purchase_bill_id", purchaseBillId);
            queryWrapper.notIn("purchase_bill_product_id", updateAndInsertPurchaseBillProductList);
            List<IpmsPurchaseBillProductNum> willRemovePurchaseBillProductList = ipmsPurchaseBillProductNumService.list(queryWrapper);
            for (IpmsPurchaseBillProductNum willRemovePurchaseBillProduct : willRemovePurchaseBillProductList) {
                // 即将被删除的数据，如果是来源源单，恢复原单源的数据
                QueryWrapper<IpmsPurchaseBillProductNum> purchaseBillProductNumQueryWrapper = new QueryWrapper<>();
                purchaseBillProductNumQueryWrapper.eq("purchase_bill_id", oldPurchaseBill.getPurchaseSourceBillId());
                purchaseBillProductNumQueryWrapper.eq("product_id", willRemovePurchaseBillProduct.getProductId());
                IpmsPurchaseBillProductNum sourcePurchaseBillProduct = ipmsPurchaseBillProductNumService.getOne(purchaseBillProductNumQueryWrapper);
                if (sourcePurchaseBillProduct != null) {
                    sourcePurchaseBillProduct.setSurplusNeedWarehousingProductNum(willRemovePurchaseBillProduct.getNeedWarehousingProductNum());
                    ipmsPurchaseBillProductNumService.updateById(sourcePurchaseBillProduct);
                }
            }
            // 删除
            ipmsPurchaseBillProductNumService.remove(queryWrapper);
        }
        // 采购订单修改不会有什么状态改变
        // 采购退货单修改也不会有什么状态改变，因为采购入库单，只有审核状态
        // 采购入库单修改会有 3 种情况：
        // 第一种：采购订单的完全执行状态改为部分执行状态，已关闭状态改为未关闭状态
        // 第二种：采购订单的部分执行状态改为完全执行状态，未关闭状态改为已关闭状态
        // 第三种：采购订单的部分执行状态不变，未关闭状态不变
        // 最后就是不做任何修改的操作，那么就是不变
        Long purchaseSourceBillId = oldPurchaseBill.getPurchaseSourceBillId();
        if (PurchaseBillConstant.PURCHASE_RECEIPT_ORDER.equals(oldPurchaseBill.getPurchaseBillType()) && purchaseSourceBillId != null && purchaseSourceBillId > 0) {
            // 如果代码执行到这里，那么已经修改完了数据里面采购订单剩余需要入库的商品数量
            IpmsPurchaseBill sourcePurchaseBill = ipmsPurchaseBillMapper.selectById(purchaseSourceBillId);
            QueryWrapper<IpmsPurchaseBillProductNum> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("purchase_bill_id", sourcePurchaseBill.getPurchaseBillId());
            List<IpmsPurchaseBillProductNum> sourcePurchaseBillProductList = ipmsPurchaseBillProductNumService.list(queryWrapper);
            int temp = 0;
            for (IpmsPurchaseBillProductNum purchaseBillProductNum : sourcePurchaseBillProductList) {
                temp++;
                if (purchaseBillProductNum.getSurplusNeedWarehousingProductNum().doubleValue() != 0) {
                    newPurchaseBill = new IpmsPurchaseBill();
                    newPurchaseBill.setPurchaseBillId(purchaseSourceBillId);
                    newPurchaseBill.setExecutionState(Constant.PART_OPERATED);
                    newPurchaseBill.setOffState(Constant.NOT_CLOSED);
                    ipmsPurchaseBillMapper.updateById(newPurchaseBill);
                    break;
                }
                if (temp == sourcePurchaseBillProductList.size()) {
                    newPurchaseBill = new IpmsPurchaseBill();
                    newPurchaseBill.setPurchaseBillId(purchaseSourceBillId);
                    newPurchaseBill.setExecutionState(Constant.FULL_OPERATED);
                    newPurchaseBill.setOffState(Constant.CLOSED);
                    ipmsPurchaseBillMapper.updateById(newPurchaseBill);
                }
            }
        }
        return result;
    }

    @Override
    public Page<SafePurchaseBillVO> selectPurchaseBill(PurchaseBillQueryRequest purchaseBillQueryRequest) {
        // 1. 参数校验
        String purchaseBillType = purchaseBillQueryRequest.getPurchaseBillType();
        if (purchaseBillType == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "单据类型不能为空");
        }
        if (purchaseBillType.equals(PurchaseBillConstant.PURCHASE_RETURN_ORDER)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "采购退货单不作为单源");
        }
        List<String> billTypeList = Arrays.asList(PurchaseBillConstant.PURCHASE_ORDER, PurchaseBillConstant.PURCHASE_RECEIPT_ORDER, PurchaseBillConstant.PURCHASE_RETURN_ORDER);
        int validTypeResult = ValidType.valid(billTypeList, purchaseBillType);
        if (validTypeResult == 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "单据类型不存在");
        }
        // 2. 进行分页查询，分情况查询，采购入库单和采购退货单
        Page<IpmsPurchaseBill> page = new Page<>(purchaseBillQueryRequest.getCurrentPage(), purchaseBillQueryRequest.getPageSize());
        QueryWrapper<IpmsPurchaseBill> ipmsPurchaseBillQueryWrapper = new QueryWrapper<>();
        String fuzzyText = purchaseBillQueryRequest.getFuzzyText();
        Long supplierId = purchaseBillQueryRequest.getSupplierId();
        if (supplierId != null && supplierId > 0) {
            IpmsSupplier supplier = ipmsSupplierService.getById(supplierId);
            if (supplier == null) {
                throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "供应商不存在");
            }
            // 找到哪些还需要被引用的单源
            QueryWrapper<IpmsPurchaseBill> purchaseBillQueryWrapper = new QueryWrapper<>();
            purchaseBillQueryWrapper.eq("supplier_id", supplierId);
            purchaseBillQueryWrapper.eq("purchase_bill_type", purchaseBillType);
            // 找出属于该供应商的所有对应类型的采购单据
            List<IpmsPurchaseBill> ipmsPurchaseBills = ipmsPurchaseBillMapper.selectList(purchaseBillQueryWrapper);
            // 收集源单 id 集合
            List<Long> sourcePurchaseBillIdList = new ArrayList<>();
            if (ipmsPurchaseBills != null && ipmsPurchaseBills.size() > 0) {
                for (IpmsPurchaseBill ipmsPurchaseBill : ipmsPurchaseBills) {
                    Long purchaseBillId = ipmsPurchaseBill.getPurchaseBillId();
                    if (purchaseBillId != null && purchaseBillId > 0) {
                        sourcePurchaseBillIdList.add(purchaseBillId);
                    }
                }
            }
            // 存储当前供应商的可以作为源单的 id 集合
            List<Long> currentCanUseSourcePurchaseBillIdList = new ArrayList<>();
            if (sourcePurchaseBillIdList.size() > 0) {
                QueryWrapper<IpmsPurchaseBillProductNum> purchaseBillProductNumQueryWrapper;
                for (Long sourcePurchaseBillId : sourcePurchaseBillIdList) {
                    purchaseBillProductNumQueryWrapper = new QueryWrapper<>();
                    purchaseBillProductNumQueryWrapper.eq("purchase_bill_id", sourcePurchaseBillId);
                    List<IpmsPurchaseBillProductNum> purchaseBillProductNums = ipmsPurchaseBillProductNumService.list(purchaseBillProductNumQueryWrapper);
                    if (purchaseBillProductNums != null && purchaseBillProductNums.size() > 0) {
                        for (IpmsPurchaseBillProductNum purchaseBillProductNum : purchaseBillProductNums) {
                            if (purchaseBillProductNum.getSurplusNeedWarehousingProductNum().doubleValue() != 0) {
                                currentCanUseSourcePurchaseBillIdList.add(purchaseBillProductNum.getPurchaseBillId());
                                break;
                            }
                        }
                    }
                }
            }
            if (currentCanUseSourcePurchaseBillIdList.size() > 0) {
                if (StringUtils.isNotBlank(fuzzyText)) {
                    ipmsPurchaseBillQueryWrapper.like("purchase_bill_code", fuzzyText)
                            .and(billType -> billType.eq("purchase_bill_type", purchaseBillType))
                            .and(supId -> supId.eq("supplier_id", supplierId))
                            .and(purchaseBillId -> purchaseBillId.in("purchase_bill_id", currentCanUseSourcePurchaseBillIdList)).or()
                            .like("purchase_bill_date", fuzzyText)
                            .and(billType -> billType.eq("purchase_bill_type", purchaseBillType))
                            .and(supId -> supId.eq("supplier_id", supplierId))
                            .and(purchaseBillId -> purchaseBillId.in("purchase_bill_id", currentCanUseSourcePurchaseBillIdList)).or()
                            .like("purchase_bill_settlement_date", fuzzyText)
                            .and(billType -> billType.eq("purchase_bill_type", purchaseBillType))
                            .and(supId -> supId.eq("supplier_id", supplierId))
                            .and(purchaseBillId -> purchaseBillId.in("purchase_bill_id", currentCanUseSourcePurchaseBillIdList)).or()
                            .like("purchase_bill_remark", fuzzyText)
                            .and(billType -> billType.eq("purchase_bill_type", purchaseBillType))
                            .and(supId -> supId.eq("supplier_id", supplierId))
                            .and(purchaseBillId -> purchaseBillId.in("purchase_bill_id", currentCanUseSourcePurchaseBillIdList)).or()
                            .like("purchase_bill_currency_type", fuzzyText)
                            .and(billType -> billType.eq("purchase_bill_type", purchaseBillType))
                            .and(supId -> supId.eq("supplier_id", supplierId))
                            .and(purchaseBillId -> purchaseBillId.in("purchase_bill_id", currentCanUseSourcePurchaseBillIdList)).or()
                            .like("purchase_bill_exchange_rate", fuzzyText)
                            .and(billType -> billType.eq("purchase_bill_type", purchaseBillType))
                            .and(supId -> supId.eq("supplier_id", supplierId))
                            .and(purchaseBillId -> purchaseBillId.in("purchase_bill_id", currentCanUseSourcePurchaseBillIdList));
                } else {
                    ipmsPurchaseBillQueryWrapper.eq("purchase_bill_type", purchaseBillType);
                    ipmsPurchaseBillQueryWrapper.eq("supplier_id", supplierId);
                    ipmsPurchaseBillQueryWrapper.in("purchase_bill_id", currentCanUseSourcePurchaseBillIdList);
                }
            } else {
                return new PageDTO<>();
            }
        } else {
            if (StringUtils.isNotBlank(fuzzyText)) {
                ipmsPurchaseBillQueryWrapper.like("purchase_bill_code", fuzzyText)
                        .and(billType -> billType.eq("purchase_bill_type", purchaseBillType)).or()
                        .like("purchase_bill_date", fuzzyText)
                        .and(billType -> billType.eq("purchase_bill_type", purchaseBillType)).or()
                        .like("purchase_bill_settlement_date", fuzzyText)
                        .and(billType -> billType.eq("purchase_bill_type", purchaseBillType)).or()
                        .like("purchase_bill_remark", fuzzyText)
                        .and(billType -> billType.eq("purchase_bill_type", purchaseBillType)).or()
                        .like("purchase_bill_currency_type", fuzzyText)
                        .and(billType -> billType.eq("purchase_bill_type", purchaseBillType)).or()
                        .like("purchase_bill_exchange_rate", fuzzyText)
                        .and(billType -> billType.eq("purchase_bill_type", purchaseBillType));
            } else {
                ipmsPurchaseBillQueryWrapper.eq("purchase_bill_type", purchaseBillType);
            }
        }
        Page<IpmsPurchaseBill> purchaseBillPage = ipmsPurchaseBillMapper.selectPage(page, ipmsPurchaseBillQueryWrapper);
        List<SafePurchaseBillVO> safePurchaseBillVOList = purchaseBillPage.getRecords().stream().map(ipmsPurchaseBill -> {
            SafePurchaseBillVO safePurchaseBillVO = new SafePurchaseBillVO();
            BeanUtils.copyProperties(ipmsPurchaseBill, safePurchaseBillVO);
            // 设置源单相关的数据
            Long purchaseSourceBillId = ipmsPurchaseBill.getPurchaseSourceBillId();
            if (purchaseSourceBillId != null && purchaseSourceBillId > 0) {
                IpmsPurchaseBill purchaseBill = ipmsPurchaseBillMapper.selectById(purchaseSourceBillId);
                if (purchaseBill == null) {
                    throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "找不到源单，系统业务错误");
                }
                safePurchaseBillVO.setPurchaseSourceBillCode(purchaseBill.getPurchaseBillCode());
                safePurchaseBillVO.setPurchaseSourceBillType(purchaseBill.getPurchaseBillType());
            }
            // 查询供应商信息，并设置到返回封装类中
            Long supId = ipmsPurchaseBill.getSupplierId();
            if (supId != null && supId > 0) {
                IpmsSupplier sup = ipmsSupplierService.getById(supId);
                if (sup == null) {
                    throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "找不到供应商信息，系统业务错误");
                }
                SafeSupplierVO safeSupplierVO = new SafeSupplierVO();
                QueryWrapper<IpmsSupplierLinkman> supplierLinkmanQueryWrapper = new QueryWrapper<>();
                supplierLinkmanQueryWrapper.eq("supplier_id", supId);
                List<IpmsSupplierLinkman> supplierLinkmanList = ipmsSupplierLinkmanService.list(supplierLinkmanQueryWrapper);
                List<SafeSupplierLinkmanVO> safeSupplierLinkmanVOList = new ArrayList<>();
                if (supplierLinkmanList != null && supplierLinkmanList.size() > 0) {
                    for (IpmsSupplierLinkman supplierLinkman : supplierLinkmanList) {
                        SafeSupplierLinkmanVO safeSupplierLinkmanVO = new SafeSupplierLinkmanVO();
                        BeanUtils.copyProperties(supplierLinkman, safeSupplierLinkmanVO);
                        safeSupplierLinkmanVO.setLinkmanBirth(TimeFormatUtil.dateFormatting2(supplierLinkman.getLinkmanBirth()));
                        safeSupplierLinkmanVOList.add(safeSupplierLinkmanVO);
                    }
                }
                BeanUtils.copyProperties(sup, safeSupplierVO);
                safeSupplierVO.setSafeSupplierLinkmanVOList(safeSupplierLinkmanVOList);
                safePurchaseBillVO.setSafeSupplierVO(safeSupplierVO);
            }
            // 查询职员信息，并设置到返回封装类中
            Long employeeId = ipmsPurchaseBill.getEmployeeId();
            if (employeeId != null && employeeId > 0) {
                IpmsEmployee employee = ipmsEmployeeService.getById(employeeId);
                if (employee == null) {
                    throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "找不到职员信息，系统业务错误");
                }
                safePurchaseBillVO.setEmployeeId(employeeId);
                safePurchaseBillVO.setEmployeeName(employee.getEmployeeName());
            }
            // 查询部门信息，并设置到返回封装类中
            Long departmentId = ipmsPurchaseBill.getDepartmentId();
            if (departmentId != null && departmentId > 0) {
                IpmsDepartment department = ipmsDepartmentService.getById(departmentId);
                if (department == null) {
                    throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "找不到部门信息，系统业务错误");
                }
                safePurchaseBillVO.setDepartmentId(departmentId);
                safePurchaseBillVO.setDepartmentName(department.getDepartmentName());
            }
            // 查询采购单据的商品信息，并设置到返回封装类中
            Long purchaseBillId = ipmsPurchaseBill.getPurchaseBillId();
            if (purchaseBillId != null && purchaseBillId > 0) {
                QueryWrapper<IpmsPurchaseBillProductNum> purchaseBillProductNumQueryWrapper = new QueryWrapper<>();
                purchaseBillProductNumQueryWrapper.eq("purchase_bill_id", purchaseBillId);
                List<IpmsPurchaseBillProductNum> purchaseBillProductList = ipmsPurchaseBillProductNumService.list(purchaseBillProductNumQueryWrapper);
                List<SafePurchaseBillProductNumVO> safePurchaseBillProductVOList = new ArrayList<>();
                if (purchaseBillProductList != null && purchaseBillProductList.size() > 0) {
                    for (IpmsPurchaseBillProductNum purchaseBillProduct : purchaseBillProductList) {
                        SafePurchaseBillProductNumVO safePurchaseBillProductVO = new SafePurchaseBillProductNumVO();
                        BeanUtils.copyProperties(purchaseBillProduct, safePurchaseBillProductVO);
                        // 在采购商品中查询商品等信息，并设置到返回封装类中
                        QueryWrapper<IpmsProductInventory> productInventoryQueryWrapper = new QueryWrapper<>();
                        Long productId = purchaseBillProduct.getProductId();
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
                            safePurchaseBillProductVO.setSafeProductVO(safeProductVO);
                            productInventoryQueryWrapper.eq("product_id", productId);
                        }
                        Long warehouseId = purchaseBillProduct.getWarehouseId();
                        if (warehouseId != null && warehouseId > 0) {
                            IpmsWarehouse warehouse = ipmsWarehouseService.getById(warehouseId);
                            if (warehouse == null) {
                                throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "找不到仓库，系统业务逻辑错误");
                            }
                            safePurchaseBillProductVO.setWarehouseId(warehouseId);
                            safePurchaseBillProductVO.setWarehouseName(warehouse.getWarehouseName());
                            productInventoryQueryWrapper.eq("warehouse_id", warehouseId);
                        }
                        Long warehousePositionId = purchaseBillProduct.getWarehousePositionId();
                        if (warehousePositionId != null && warehousePositionId > 0) {
                            IpmsWarehousePosition warehousePosition = ipmsWarehousePositionService.getById(warehousePositionId);
                            if (warehousePosition != null) {
                                safePurchaseBillProductVO.setWarehousePositionId(warehousePositionId);
                                safePurchaseBillProductVO.setWarehousePositionName(warehousePosition.getWarehousePositionName());
                                productInventoryQueryWrapper.eq("warehouse_position_id", warehousePositionId);
                            }
                        }
                        // 查询库存
                        IpmsProductInventory productInventory = ipmsProductInventoryService.getOne(productInventoryQueryWrapper);
                        if (productInventory != null) {
                            safePurchaseBillProductVO.setAvailableInventory(productInventory.getProductInventorySurplusNum());
                        }
                        safePurchaseBillProductVOList.add(safePurchaseBillProductVO);
                    }
                }
                safePurchaseBillVO.setSafePurchaseBillProductNumVOList(safePurchaseBillProductVOList);
            }
            // 单据时间格式化
            safePurchaseBillVO.setCreateTime(TimeFormatUtil.dateFormatting(ipmsPurchaseBill.getCreateTime()));
            safePurchaseBillVO.setUpdateTime(TimeFormatUtil.dateFormatting(ipmsPurchaseBill.getUpdateTime()));
            safePurchaseBillVO.setCheckTime(TimeFormatUtil.dateFormatting(ipmsPurchaseBill.getCheckTime()));
            return safePurchaseBillVO;
        }).collect(Collectors.toList());
        // 关键步骤
        Page<SafePurchaseBillVO> safePurchaseBillVOPage = new PageDTO<>(purchaseBillPage.getCurrent(), purchaseBillPage.getSize(), purchaseBillPage.getTotal());
        safePurchaseBillVOPage.setRecords(safePurchaseBillVOList);
        return safePurchaseBillVOPage;
    }
}