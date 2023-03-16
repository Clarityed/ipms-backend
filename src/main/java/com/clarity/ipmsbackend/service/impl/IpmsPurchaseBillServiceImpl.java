package com.clarity.ipmsbackend.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.clarity.ipmsbackend.common.Constant;
import com.clarity.ipmsbackend.common.ErrorCode;
import com.clarity.ipmsbackend.constant.PurchaseBillConstant;
import com.clarity.ipmsbackend.exception.BusinessException;
import com.clarity.ipmsbackend.mapper.IpmsPurchaseBillMapper;
import com.clarity.ipmsbackend.model.dto.purchasebill.AddPurchaseBillRequest;
import com.clarity.ipmsbackend.model.dto.purchasebill.productnum.AddProductNumRequest;
import com.clarity.ipmsbackend.model.entity.*;
import com.clarity.ipmsbackend.model.vo.SafeUserVO;
import com.clarity.ipmsbackend.service.*;
import com.clarity.ipmsbackend.utils.CodeAutoGenerator;
import com.clarity.ipmsbackend.utils.SplitUtil;
import com.clarity.ipmsbackend.utils.TimeFormatUtil;
import com.clarity.ipmsbackend.utils.ValidType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

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
            int addPurchaseBillProductAndNumResult = ipmsPurchaseBillProductNumService.addPurchaseBillProductAndNum(addProductNumRequest, purchaseBill);
            if (addPurchaseBillProductAndNumResult != 1) {
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
                BigDecimal onePurchaseBillProductCost = ipmsProductInventoryService.addProductInventory(currentCheckingPurchaseBillProduct);
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
                BigDecimal onePurchaseBillProductCost = ipmsProductInventoryService.reduceProductInventory(purchaseReturnProduct);
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
                BigDecimal onePurchaseBillProductCost = ipmsProductInventoryService.reduceProductInventory(purchaseBillReceiptProduct);
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
                BigDecimal onePurchaseBillProductCost = ipmsProductInventoryService.addProductInventory(purchaseBillReturnProduct);
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
                    BigDecimal oldSurplusNeedWarehousingProductNum = sourceProductOfOne.getSurplusNeedWarehousingProductNum();
                    sourceProductOfOne.setSurplusNeedWarehousingProductNum(oldSurplusNeedWarehousingProductNum.add(purchaseReceiptBillProductNum.getNeedWarehousingProductNum()));
                    boolean recoverSourceProductNumResult = ipmsPurchaseBillProductNumService.updateById(sourceProductOfOne);
                    if (!recoverSourceProductNumResult) {
                        throw new BusinessException(ErrorCode.SYSTEM_ERROR, "恢复采购订单剩余数量失败");
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
                    BigDecimal oldSurplusNeedWarehousingProductNum = sourceProductOfOne.getSurplusNeedWarehousingProductNum();
                    sourceProductOfOne.setSurplusNeedWarehousingProductNum(oldSurplusNeedWarehousingProductNum.add(purchaseReceiptBillProductNum.getNeedWarehousingProductNum()));
                    boolean recoverSourceProductNumResult = ipmsPurchaseBillProductNumService.updateById(sourceProductOfOne);
                    if (!recoverSourceProductNumResult) {
                        throw new BusinessException(ErrorCode.SYSTEM_ERROR, "恢复采购订单剩余数量失败");
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
}