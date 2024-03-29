package com.clarity.ipmsbackend.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.plugins.pagination.PageDTO;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.clarity.ipmsbackend.common.Constant;
import com.clarity.ipmsbackend.common.ErrorCode;
import com.clarity.ipmsbackend.common.ProductionBillQueryRequest;
import com.clarity.ipmsbackend.constant.ProductionBillConstant;
import com.clarity.ipmsbackend.constant.WarehouseConstant;
import com.clarity.ipmsbackend.exception.BusinessException;
import com.clarity.ipmsbackend.mapper.IpmsProductionBillMapper;
import com.clarity.ipmsbackend.model.dto.productionbill.AddProductionBillRequest;
import com.clarity.ipmsbackend.model.dto.productionbill.UpdateProductionBillRequest;
import com.clarity.ipmsbackend.model.dto.productionbill.productnum.AddProductionProductNumRequest;
import com.clarity.ipmsbackend.model.dto.productionbill.productnum.UpdateProductionProductNumRequest;
import com.clarity.ipmsbackend.model.entity.*;
import com.clarity.ipmsbackend.model.vo.SafeProductVO;
import com.clarity.ipmsbackend.model.vo.SafeUserVO;
import com.clarity.ipmsbackend.model.vo.productionbill.SafeProductionBillVO;
import com.clarity.ipmsbackend.model.vo.productionbill.productnum.SafeProductionBillProductNumVO;
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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Clarity
 * @description 针对表【ipms_production_bill(生产单据)】的数据库操作Service实现
 * @createDate 2023-03-23 14:42:11
 */
@Service
@Slf4j
public class IpmsProductionBillServiceImpl extends ServiceImpl<IpmsProductionBillMapper, IpmsProductionBill>
        implements IpmsProductionBillService {

    @Resource
    private IpmsProductionBillMapper ipmsProductionBillMapper;

    @Resource
    private IpmsEmployeeService ipmsEmployeeService;

    @Resource
    private IpmsDepartmentService ipmsDepartmentService;

    @Resource
    private IpmsProductService ipmsProductService;

    @Resource
    private IpmsWarehouseService ipmsWarehouseService;

    @Resource
    private IpmsWarehousePositionService ipmsWarehousePositionService;

    @Resource
    private IpmsUserService ipmsUserService;

    @Resource
    private IpmsProductionBillProductNumService ipmsProductionBillProductNumService;

    @Resource
    private IpmsProductInventoryService ipmsProductInventoryService;

    @Resource
    private IpmsUnitService ipmsUnitService;

    @Override
    public String productionBillCodeAutoGenerate(String productionBillType) {
        if (productionBillType == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "单据类型为空，无法生成对于的单据编码");
        }
        QueryWrapper<IpmsProductionBill> ipmsProductionBillQueryWrapper = new QueryWrapper<>();
        ipmsProductionBillQueryWrapper.eq("production_bill_type", productionBillType);
        List<IpmsProductionBill> ipmsProductionBillList;
        String productionBillCode;
        String productionBillCodePrefix;
        String productionBillCodeInfix;
        String productionBillCodeSuffix;
        switch (productionBillType) {
            case ProductionBillConstant.PRODUCTION_TASK_ORDER:
                ipmsProductionBillList = ipmsProductionBillMapper.selectList(ipmsProductionBillQueryWrapper);
                if (ipmsProductionBillList.size() == 0) {
                    productionBillCodePrefix = "SCRWD";
                    productionBillCodeInfix = TimeFormatUtil.dateFormat(new Date());
                    productionBillCodeSuffix = "0";
                } else {
                    IpmsProductionBill lastProductionBill = ipmsProductionBillList.get(ipmsProductionBillList.size() - 1);
                    productionBillCode = lastProductionBill.getProductionBillCode();
                    String[] productionOrderCode = SplitUtil.codeSplitByMinusSign(productionBillCode);
                    productionBillCodePrefix = productionOrderCode[0];
                    productionBillCodeInfix = productionOrderCode[1];
                    productionBillCodeSuffix = productionOrderCode[2];
                }
                break;
            case ProductionBillConstant.PRODUCTION_PICKING_ORDER:
                ipmsProductionBillList = ipmsProductionBillMapper.selectList(ipmsProductionBillQueryWrapper);
                if (ipmsProductionBillList.size() == 0) {
                    productionBillCodePrefix = "SCLLD";
                    productionBillCodeInfix = TimeFormatUtil.dateFormat(new Date());
                    productionBillCodeSuffix = "0";
                } else {
                    IpmsProductionBill lastProductionBill = ipmsProductionBillList.get(ipmsProductionBillList.size() - 1);
                    productionBillCode = lastProductionBill.getProductionBillCode();
                    String[] productionOrderCode = SplitUtil.codeSplitByMinusSign(productionBillCode);
                    productionBillCodePrefix = productionOrderCode[0];
                    productionBillCodeInfix = productionOrderCode[1];
                    productionBillCodeSuffix = productionOrderCode[2];
                }
                break;
            case ProductionBillConstant.PRODUCTION_RETURN_ORDER:
                ipmsProductionBillList = ipmsProductionBillMapper.selectList(ipmsProductionBillQueryWrapper);
                if (ipmsProductionBillList.size() == 0) {
                    productionBillCodePrefix = "SCTLD";
                    productionBillCodeInfix = TimeFormatUtil.dateFormat(new Date());
                    productionBillCodeSuffix = "0";
                } else {
                    IpmsProductionBill lastProductionBill = ipmsProductionBillList.get(ipmsProductionBillList.size() - 1);
                    productionBillCode = lastProductionBill.getProductionBillCode();
                    String[] productionOrderCode = SplitUtil.codeSplitByMinusSign(productionBillCode);
                    productionBillCodePrefix = productionOrderCode[0];
                    productionBillCodeInfix = productionOrderCode[1];
                    productionBillCodeSuffix = productionOrderCode[2];
                }
                break;
            case ProductionBillConstant.PRODUCTION_RECEIPT_ORDER:
                ipmsProductionBillList = ipmsProductionBillMapper.selectList(ipmsProductionBillQueryWrapper);
                if (ipmsProductionBillList.size() == 0) {
                    productionBillCodePrefix = "SCRKD";
                    productionBillCodeInfix = TimeFormatUtil.dateFormat(new Date());
                    productionBillCodeSuffix = "0";
                } else {
                    IpmsProductionBill lastProductionBill = ipmsProductionBillList.get(ipmsProductionBillList.size() - 1);
                    productionBillCode = lastProductionBill.getProductionBillCode();
                    String[] productionOrderCode = SplitUtil.codeSplitByMinusSign(productionBillCode);
                    productionBillCodePrefix = productionOrderCode[0];
                    productionBillCodeInfix = productionOrderCode[1];
                    productionBillCodeSuffix = productionOrderCode[2];
                }
                break;
            case ProductionBillConstant.PRODUCTION_STOCK_RETURN_ORDER:
                ipmsProductionBillList = ipmsProductionBillMapper.selectList(ipmsProductionBillQueryWrapper);
                if (ipmsProductionBillList.size() == 0) {
                    productionBillCodePrefix = "SCTKD";
                    productionBillCodeInfix = TimeFormatUtil.dateFormat(new Date());
                    productionBillCodeSuffix = "0";
                } else {
                    IpmsProductionBill lastProductionBill = ipmsProductionBillList.get(ipmsProductionBillList.size() - 1);
                    productionBillCode = lastProductionBill.getProductionBillCode();
                    String[] productionOrderCode = SplitUtil.codeSplitByMinusSign(productionBillCode);
                    productionBillCodePrefix = productionOrderCode[0];
                    productionBillCodeInfix = productionOrderCode[1];
                    productionBillCodeSuffix = productionOrderCode[2];
                }
                break;
            default:
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "单据类型不存在");
        }
        String nextProductionBillCode = null;
        try {
            String todayDateFormat = TimeFormatUtil.dateFormat(new Date());
            if (!productionBillCodeInfix.equals(todayDateFormat)) {
                nextProductionBillCode = CodeAutoGenerator.generatorCode(productionBillCodePrefix, todayDateFormat, "0");
            } else {
                nextProductionBillCode = CodeAutoGenerator.generatorCode(productionBillCodePrefix, productionBillCodeInfix, productionBillCodeSuffix);
            }
        } catch (Exception e) {
            log.info("编码自动生成器异常");
        }
        return nextProductionBillCode;
    }

    @Override
    @Transactional
    public int addProductionBill(AddProductionBillRequest addProductionBillRequest, HttpServletRequest request) {
        // 单据编号不能为空，且必须不能重复
        String productionBillCode = addProductionBillRequest.getProductionBillCode();
        if (productionBillCode == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "单据编号为空");
        }
        QueryWrapper<IpmsProductionBill> productionBillQueryWrapper = new QueryWrapper<>();
        productionBillQueryWrapper.eq("production_bill_code", productionBillCode);
        IpmsProductionBill validCodeProductionBill = ipmsProductionBillMapper.selectOne(productionBillQueryWrapper);
        if (validCodeProductionBill != null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "单据编号重复");
        }
        // 单据日期不能为空
        String productionBillDate = addProductionBillRequest.getProductionBillDate();
        if (productionBillDate == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "单据日期为空");
        }
        // 职员 id 和 部门 id，可以为空，如果不为空，那么必须存在对于的职员和部门信息
        Long employeeId = addProductionBillRequest.getEmployeeId();
        if (employeeId != null) {
            IpmsEmployee validEmployee = ipmsEmployeeService.getById(employeeId);
            if (validEmployee == null) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "单据职员不存在");
            }
        }
        Long departmentId = addProductionBillRequest.getDepartmentId();
        if (departmentId != null) {
            IpmsDepartment validDepartment = ipmsDepartmentService.getById(departmentId);
            if (validDepartment == null) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "单据部门不存在");
            }
        }
        // 仓管员 id，可以为空，如果不为空，那么必须存在该员工
        Long storekeeperId = addProductionBillRequest.getStorekeeperId();
        if (storekeeperId != null) {
            IpmsEmployee validEmployee = ipmsEmployeeService.getById(storekeeperId);
            if (validEmployee == null) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "单据仓管不存在");
            }
        }
        // 生产单据类型不能为空，且必须符合系统要求
        String productionBillType = addProductionBillRequest.getProductionBillType();
        if (productionBillType == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "单据类型不能为空");
        }
        List<String> billTypeList = Arrays.asList(ProductionBillConstant.PRODUCTION_TASK_ORDER, ProductionBillConstant.PRODUCTION_PICKING_ORDER,
                ProductionBillConstant.PRODUCTION_RETURN_ORDER, ProductionBillConstant.PRODUCTION_RECEIPT_ORDER, ProductionBillConstant.PRODUCTION_STOCK_RETURN_ORDER);
        int validTypeResult = ValidType.valid(billTypeList, productionBillType);
        if (validTypeResult == 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "单据类型不存在");
        }
        // 生产单据的商品及商品数量至少存在一个
        List<AddProductionProductNumRequest> addProductionProductNumRequestList = addProductionBillRequest.getAddProductionProductNumRequestList();
        if (addProductionProductNumRequestList == null || addProductionProductNumRequestList.size() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "生产的商品为空，至少要存在一个生产商品");
        }
        String productionBillBusinessType = addProductionBillRequest.getProductionBillBusinessType();
        String productionBillReturnReason = addProductionBillRequest.getProductionBillReturnReason();
        Long productId = addProductionBillRequest.getProductId();
        Long warehouseId = addProductionBillRequest.getWarehouseId();
        Long warehousePositionId = addProductionBillRequest.getWarehousePositionId();
        BigDecimal productNum = addProductionBillRequest.getProductNum();
        String planCompletionDate = addProductionBillRequest.getPlanCompletionDate();
        String planCommencementDate = addProductionBillRequest.getPlanCommencementDate();
        String productRemark = addProductionBillRequest.getProductRemark();
        boolean validPickingAndReceipt = productionBillBusinessType != null || productionBillReturnReason != null || productId != null || warehouseId != null ||
                warehousePositionId != null || productNum != null || planCompletionDate != null || planCommencementDate != null || productRemark != null;
        boolean validReturnAndStockReturn = productionBillBusinessType != null || productId != null || warehouseId != null ||
                warehousePositionId != null || productNum != null || planCompletionDate != null || planCommencementDate != null || productRemark != null;
        switch (productionBillType) {
            case ProductionBillConstant.PRODUCTION_TASK_ORDER:
                if (productionBillReturnReason != null) {
                    throw new BusinessException(ErrorCode.PARAMS_ERROR, productionBillType + "不需要输入退料或者退库原因");
                }
                if (storekeeperId != null) {
                    throw new BusinessException(ErrorCode.PARAMS_ERROR, productionBillType + "不需要仓管 id");
                }
                if (productionBillBusinessType == null) {
                    throw new BusinessException(ErrorCode.PARAMS_ERROR, productionBillType + "必须要输入业务类型");
                }
                if (productId == null || productId <= 0) {
                    throw new BusinessException(ErrorCode.PARAMS_ERROR, productionBillType + "的商品 id 为空或者不合法");
                }
                IpmsProduct product = ipmsProductService.getById(productId);
                if (product == null) {
                    throw new BusinessException(ErrorCode.PARAMS_ERROR, productionBillType + "的商品不存在");
                }
                if (warehouseId == null || warehouseId <= 0) {
                    throw new BusinessException(ErrorCode.PARAMS_ERROR, productionBillType + "商品的仓库 id 为空或者不合法");
                }
                IpmsWarehouse warehouse = ipmsWarehouseService.getById(warehouseId);
                if (warehouse == null) {
                    throw new BusinessException(ErrorCode.PARAMS_ERROR, productionBillType + "商品的仓库不存在");
                }
                Integer isWarehousePositionManagement = warehouse.getIsWarehousePositionManagement();
                if (WarehouseConstant.OPEN_WAREHOUSE_POSITION_MANAGEMENT == isWarehousePositionManagement) {
                    if (warehousePositionId == null || warehousePositionId <= 0) {
                        throw new BusinessException(ErrorCode.PARAMS_ERROR, productionBillType + "商品的仓位 id 为空或者不合法");
                    }
                    IpmsWarehousePosition warehousePosition = ipmsWarehousePositionService.getById(warehousePositionId);
                    if (warehousePosition == null) {
                        throw new BusinessException(ErrorCode.PARAMS_ERROR, productionBillType + "商品的仓位不存在");
                    }
                    if (!warehouse.getWarehouseId().equals(warehousePosition.getWarehouseId())) {
                        throw new BusinessException(ErrorCode.PARAMS_ERROR, productionBillType + "商品的仓位不属于该仓库");
                    }
                } else {
                    if (warehousePositionId != null) {
                        throw new BusinessException(ErrorCode.PARAMS_ERROR, "未开启仓位管理，不应该有仓位 id");
                    }
                }
                if (productNum == null || productNum.doubleValue() <= 0) {
                    throw new BusinessException(ErrorCode.PARAMS_ERROR, productionBillType + "商品数量为空或者数量小于等于 0");
                }
                if (planCommencementDate == null || planCompletionDate == null) {
                    throw new BusinessException(ErrorCode.PARAMS_ERROR, productionBillType + "计划开工日期或者计划完工日期为空");
                }
                break;
            case ProductionBillConstant.PRODUCTION_PICKING_ORDER:
            case ProductionBillConstant.PRODUCTION_RECEIPT_ORDER:
                if (validPickingAndReceipt) {
                    throw new BusinessException(ErrorCode.PARAMS_ERROR, "请检查表单数据，" + productionBillType + "不需要输入生产单据业务类型、生产单据退还原因、" +
                            "父级商品 id、父级商品仓库 id、父级商品仓位 id、需要入库的商品数量、计划开工日期、计划完工日期和商品备注");
                }
                break;
            case ProductionBillConstant.PRODUCTION_RETURN_ORDER:
            case ProductionBillConstant.PRODUCTION_STOCK_RETURN_ORDER:
                if (validReturnAndStockReturn) {
                    throw new BusinessException(ErrorCode.PARAMS_ERROR, "请检查表单数据，" + productionBillType + "不需要输入生产单据业务类型、" +
                            "父级商品 id、父级商品仓库 id、父级商品仓位 id、需要入库的商品数量、计划开工日期、计划完工日期和商品备注");
                }
                break;
        }
        // 单据必须审核后才能被当作单源来使用
        Long productionSourceBillId = addProductionBillRequest.getProductionSourceBillId();
        if (productionSourceBillId != null && productionSourceBillId > 0) {
            QueryWrapper<IpmsProductionBill> ipmsProductionBillQueryWrapper = new QueryWrapper<>();
            ipmsProductionBillQueryWrapper.eq("production_bill_id", productionSourceBillId);
            IpmsProductionBill validStateProductionBill = ipmsProductionBillMapper.selectOne(ipmsProductionBillQueryWrapper);
            Integer checkState = validStateProductionBill.getCheckState();
            if (!checkState.equals(Constant.CHECKED)) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "选择的生产单源未审核");
            }
            // 必须完全领料的生产任务单才能够进行生产入库操作，可以通过生产任务单领料状态来判断
            if (productionBillType.equals(ProductionBillConstant.PRODUCTION_RECEIPT_ORDER)) {
                Integer pickingState = validStateProductionBill.getPickingState();
                if (pickingState != Constant.FULL_OPERATED) {
                    throw new BusinessException(ErrorCode.PARAMS_ERROR, productionBillType + "的生产任务单未完全领料，无法执行入库操作");
                }
            }
        }
        // 增加数据，并且设置其他固定字段
        IpmsProductionBill productionBill = new IpmsProductionBill();
        BeanUtils.copyProperties(addProductionBillRequest, productionBill);
        SafeUserVO loginUser = ipmsUserService.getLoginUser(request);
        if (productionBillType.equals(ProductionBillConstant.PRODUCTION_TASK_ORDER)) {
            productionBill.setNeedWarehousingProductNum(productNum);
            productionBill.setSurplusNeedWarehousingProductNum(productNum);
        }
        productionBill.setFounder(loginUser.getUserName());
        productionBill.setCreateTime(new Date());
        productionBill.setUpdateTime(new Date());
        int insertResult = ipmsProductionBillMapper.insert(productionBill);
        if (insertResult != 1) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR);
        }
        // 最后调用增加生产单据商品及数量
        for (AddProductionProductNumRequest addProductionProductNumRequest : addProductionProductNumRequestList) {
            long addProductionBillProductAndNumResult = ipmsProductionBillProductNumService.addProductionBillProductAndNum(addProductionProductNumRequest, productionBill);
            if (addProductionBillProductAndNumResult < 0) {
                throw new BusinessException(ErrorCode.SYSTEM_ERROR, "生产单据商品及数量插入失败");
            }
        }
        // 生产任务单状态改变
        if (productionSourceBillId != null && productionSourceBillId > 0) {
            IpmsProductionBill sourceProductionBill = ipmsProductionBillMapper.selectById(productionSourceBillId);
            if (productionBillType.equals(ProductionBillConstant.PRODUCTION_PICKING_ORDER)) {
                QueryWrapper<IpmsProductionBillProductNum> queryWrapper = new QueryWrapper<>();
                queryWrapper.eq("production_bill_id", sourceProductionBill.getProductionBillId());
                List<IpmsProductionBillProductNum> sourceProductionBillProductList = ipmsProductionBillProductNumService.list(queryWrapper);
                int temp = 0;
                for (IpmsProductionBillProductNum productionBillProductNum : sourceProductionBillProductList) {
                    temp++;
                    if (productionBillProductNum.getSurplusNeedExecutionProductNum().doubleValue() != 0) {
                        sourceProductionBill.setPickingExecutionState(Constant.PART_OPERATED);
                        ipmsProductionBillMapper.updateById(sourceProductionBill);
                        break;
                    }
                    if (temp == sourceProductionBillProductList.size()) {
                        sourceProductionBill.setPickingExecutionState(Constant.FULL_OPERATED);
                        ipmsProductionBillMapper.updateById(sourceProductionBill);
                    }
                }
            } else if (productionBillType.equals(ProductionBillConstant.PRODUCTION_RECEIPT_ORDER)) {
                BigDecimal surplusNeedWarehousingProductNum = sourceProductionBill.getSurplusNeedWarehousingProductNum();
                if (surplusNeedWarehousingProductNum.doubleValue() != 0) {
                    sourceProductionBill.setWarehousingExecutionState(Constant.PART_OPERATED);
                } else {
                    sourceProductionBill.setWarehousingExecutionState(Constant.FULL_OPERATED);
                    sourceProductionBill.setFinishState(ProductionBillConstant.FINISHED);
                    sourceProductionBill.setFinishTime(new Date());
                    sourceProductionBill.setFinisher(ipmsUserService.getLoginUser(request).getUserName());
                }
                ipmsProductionBillMapper.updateById(sourceProductionBill);
            }
        }
        return insertResult;
    }

    @Override
    @Transactional
    public int checkProductionBill(long productionBillId, HttpServletRequest request) {
        if (productionBillId <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "单据 id 不合法");
        }
        IpmsProductionBill productionBill = ipmsProductionBillMapper.selectById(productionBillId);
        if (productionBill == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }
        Integer checkState = productionBill.getCheckState();
        if (Constant.CHECKED == checkState) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "单据已审核");
        }
        String productionBillType = productionBill.getProductionBillType();
        IpmsProductionBill checkingProductionBill = new IpmsProductionBill();
        checkingProductionBill.setProductionBillId(productionBill.getProductionBillId());
        checkingProductionBill.setCheckState(Constant.CHECKED);
        if (ProductionBillConstant.PRODUCTION_PICKING_ORDER.equals(productionBillType)) {
            // 审核后要修改生产订单的领料状态
            QueryWrapper<IpmsProductionBill> productionBillQueryWrapper = new QueryWrapper<>();
            productionBillQueryWrapper.eq("production_source_bill_id", productionBill.getProductionSourceBillId());
            productionBillQueryWrapper.eq("check_state", Constant.CHECKED);
            List<IpmsProductionBill> checkedProductionBillList = ipmsProductionBillMapper.selectList(productionBillQueryWrapper);
            List<Long> checkedProductionBillIdList = new ArrayList<>();
            for (IpmsProductionBill checkProductionBill : checkedProductionBillList) {
                checkedProductionBillIdList.add(checkProductionBill.getProductionBillId());
            }
            QueryWrapper<IpmsProductionBillProductNum> productionBillProductNumQueryWrapper = new QueryWrapper<>();
            double checkedProductionBillProductNum = 0;
            if (checkedProductionBillIdList.size() > 0) {
                productionBillProductNumQueryWrapper.in("production_bill_id", checkedProductionBillIdList);
                List<IpmsProductionBillProductNum> checkedProductionBillProductList = ipmsProductionBillProductNumService.list(productionBillProductNumQueryWrapper);
                for (IpmsProductionBillProductNum checkedProductionBillProduct : checkedProductionBillProductList) {
                    checkedProductionBillProductNum += checkedProductionBillProduct.getNeedExecutionProductNum().doubleValue();
                }
            }
            productionBillProductNumQueryWrapper = new QueryWrapper<>();
            productionBillProductNumQueryWrapper.eq("production_bill_id", productionBillId);
            List<IpmsProductionBillProductNum> currentCheckingProductionBillProductList = ipmsProductionBillProductNumService.list(productionBillProductNumQueryWrapper);
            for (IpmsProductionBillProductNum currentCheckingProductionBillProduct : currentCheckingProductionBillProductList) {
                checkedProductionBillProductNum += currentCheckingProductionBillProduct.getNeedExecutionProductNum().doubleValue();
                // 并且调用减少库存的方法
                int result = ipmsProductInventoryService.reduceProductInventory(currentCheckingProductionBillProduct);
                if (result != 1) {
                    throw new BusinessException(ErrorCode.SYSTEM_ERROR, "调用减少库存的方法失败");
                }
            }
            double needExecutionProductNum = 0;
            productionBillProductNumQueryWrapper = new QueryWrapper<>();
            productionBillProductNumQueryWrapper.eq("production_bill_id", productionBill.getProductionSourceBillId());
            List<IpmsProductionBillProductNum> sourceProductionBillProductList = ipmsProductionBillProductNumService.list(productionBillProductNumQueryWrapper);
            for (IpmsProductionBillProductNum sourceProductionBillProduct : sourceProductionBillProductList) {
                needExecutionProductNum += sourceProductionBillProduct.getNeedExecutionProductNum().doubleValue();
            }
            IpmsProductionBill sourceProductionBill = ipmsProductionBillMapper.selectById(productionBill.getProductionSourceBillId());
            if (sourceProductionBill != null) {
                if (checkedProductionBillProductNum == needExecutionProductNum) {
                    sourceProductionBill.setPickingState(Constant.FULL_OPERATED);
                } else {
                    sourceProductionBill.setPickingState(Constant.PART_OPERATED);
                }
                int validSourceState = ipmsProductionBillMapper.updateById(sourceProductionBill);
                if (validSourceState != 1) {
                    throw new BusinessException(ErrorCode.SYSTEM_ERROR, "生产领料状态更新失败");
                }
            }
        } else if (ProductionBillConstant.PRODUCTION_RETURN_ORDER.equals(productionBillType)) {
            // 生产退料单审核后要调用增加库存的方法
            QueryWrapper<IpmsProductionBillProductNum> productionBillProductNumQueryWrapper = new QueryWrapper<>();
            productionBillProductNumQueryWrapper.eq("production_bill_id", productionBillId);
            List<IpmsProductionBillProductNum> productionReturnProductList = ipmsProductionBillProductNumService.list(productionBillProductNumQueryWrapper);
            for (IpmsProductionBillProductNum productionReturnProduct : productionReturnProductList) {
                // 调用增加库存的方法
                int result = ipmsProductInventoryService.addProductInventory(productionReturnProduct, new ArrayList<>());
                if (result != 1) {
                    throw new BusinessException(ErrorCode.SYSTEM_ERROR, "调用增加库存的方法失败");
                }
            }
        } else if (ProductionBillConstant.PRODUCTION_RECEIPT_ORDER.equals(productionBillType)) {
            // 审核后要修改生产订单的入库状态
            QueryWrapper<IpmsProductionBillProductNum> productionBillProductNumQueryWrapper = new QueryWrapper<>();
            productionBillProductNumQueryWrapper.eq("production_bill_id", productionBillId);
            List<IpmsProductionBillProductNum> currentCheckingProductionBillProductList = ipmsProductionBillProductNumService.list(productionBillProductNumQueryWrapper);
            for (IpmsProductionBillProductNum currentCheckingProductionBillProduct : currentCheckingProductionBillProductList) {
                // 并且调用增加库存的方法
                // 如果生产入库单有单源，那第一条数据必定是生产任务单的父级商品
                if (productionBill.getProductionSourceBillId() != null && productionBill.getProductionSourceBillId() > 0
                        && currentCheckingProductionBillProduct == currentCheckingProductionBillProductList.get(0)) {
                    productionBillProductNumQueryWrapper = new QueryWrapper<>();
                    productionBillProductNumQueryWrapper.eq("production_bill_id", productionBill.getProductionSourceBillId());
                    List<IpmsProductionBillProductNum> taskOrderProductList = ipmsProductionBillProductNumService.list(productionBillProductNumQueryWrapper);
                    int result = ipmsProductInventoryService.addProductInventory(currentCheckingProductionBillProduct, taskOrderProductList);
                    if (result != 1) {
                        throw new BusinessException(ErrorCode.SYSTEM_ERROR, "调用增加库存的方法失败");
                    }
                } else {
                    int result = ipmsProductInventoryService.addProductInventory(currentCheckingProductionBillProduct, new ArrayList<>());
                    if (result != 1) {
                        throw new BusinessException(ErrorCode.SYSTEM_ERROR, "调用增加库存的方法失败");
                    }
                }
            }
            // 生产入库单如果是选单源的话，肯定只有一个要入库的源单商品
            QueryWrapper<IpmsProductionBill> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("production_bill_id", productionBill.getProductionSourceBillId());
            IpmsProductionBill taskOrderProduct = ipmsProductionBillMapper.selectOne(queryWrapper);
            IpmsProductionBill sourceProductionBill = ipmsProductionBillMapper.selectById(productionBill.getProductionSourceBillId());
            queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("production_source_bill_id", productionBill.getProductionSourceBillId());
            queryWrapper.eq("production_bill_type", ProductionBillConstant.PRODUCTION_RECEIPT_ORDER);
            queryWrapper.eq("check_state", Constant.CHECKED);
            List<IpmsProductionBill> checkedReceiptProductionBillList = ipmsProductionBillMapper.selectList(queryWrapper);
            queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("production_source_bill_id", productionBill.getProductionSourceBillId());
            queryWrapper.eq("production_bill_type", ProductionBillConstant.PRODUCTION_RECEIPT_ORDER);
            List<IpmsProductionBill> checkedAndUncheckedReceiptProductionBillList = ipmsProductionBillMapper.selectList(queryWrapper);
            if (sourceProductionBill != null) {
                if (taskOrderProduct.getSurplusNeedWarehousingProductNum().doubleValue() == 0 &&
                        checkedAndUncheckedReceiptProductionBillList.size() - checkedReceiptProductionBillList.size() == 1) {
                    sourceProductionBill.setWarehousingState(Constant.FULL_OPERATED);
                } else {
                    sourceProductionBill.setWarehousingState(Constant.PART_OPERATED);
                }
                int validSourceState = ipmsProductionBillMapper.updateById(sourceProductionBill);
                if (validSourceState != 1) {
                    throw new BusinessException(ErrorCode.SYSTEM_ERROR, "生产入库状态更新失败");
                }
            }
        } else if (ProductionBillConstant.PRODUCTION_STOCK_RETURN_ORDER.equals(productionBillType)) {
            // 生产退库单审核后要调用减少库存的方法
            QueryWrapper<IpmsProductionBillProductNum> productionBillProductNumQueryWrapper = new QueryWrapper<>();
            productionBillProductNumQueryWrapper.eq("production_bill_id", productionBillId);
            List<IpmsProductionBillProductNum> productionReturnProductList = ipmsProductionBillProductNumService.list(productionBillProductNumQueryWrapper);
            for (IpmsProductionBillProductNum productionReturnProduct : productionReturnProductList) {
                // 调用减少库存的方法
                int result = ipmsProductInventoryService.reduceProductInventory(productionReturnProduct);
                if (result != 1) {
                    throw new BusinessException(ErrorCode.SYSTEM_ERROR, "调用减少库存的方法失败");
                }
            }
        }
        SafeUserVO loginUser = ipmsUserService.getLoginUser(request);
        checkingProductionBill.setChecker(loginUser.getUserName());
        checkingProductionBill.setCheckTime(new Date());
        int result = ipmsProductionBillMapper.updateById(checkingProductionBill);
        if (result != 1) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR);
        }
        return result;
    }

    @Override
    public int reverseCheckProductionBill(long productionBillId, HttpServletRequest request) {
        if (productionBillId <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "单据 id 不合法");
        }
        IpmsProductionBill productionBill = ipmsProductionBillMapper.selectById(productionBillId);
        if (productionBill == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }
        if (Constant.UNCHECKED == productionBill.getCheckState()) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "单据未审核");
        }
        String productionBillType = productionBill.getProductionBillType();
        IpmsProductionBill unCheckingProductionBill = new IpmsProductionBill();
        unCheckingProductionBill.setProductionBillId(productionBill.getProductionBillId());
        unCheckingProductionBill.setCheckState(Constant.UNCHECKED);
        unCheckingProductionBill.setChecker(null);
        unCheckingProductionBill.setCheckTime(null);
        // 如果生产任务单已经作为其他单据的源单，那么无法反审核。
        // 生产领料单第一种情况没有选单源并且也是已经作为其他单据的源单，那么无法反审核。
        // 生产退料单第一种情况没有选单源并且也是已经作为其他单据的源单，那么无法反审核。（没有这种情况，生产退料单不作为其他单源）
        QueryWrapper<IpmsProductionBill> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("production_source_bill_id", productionBillId);
        List<IpmsProductionBill> isAsSourceBill = ipmsProductionBillMapper.selectList(queryWrapper);
        if (isAsSourceBill != null && isAsSourceBill.size() > 0) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "单据已经作为其他单据的单源使用");
        }
        if (ProductionBillConstant.PRODUCTION_PICKING_ORDER.equals(productionBillType)) {
            // 1. 遍历生产领料单出库商品及数量
            QueryWrapper<IpmsProductionBillProductNum> productionBillProductNumQueryWrapper = new QueryWrapper<>();
            productionBillProductNumQueryWrapper.eq("production_bill_id", productionBillId);
            List<IpmsProductionBillProductNum> productionBillReceiptProductList = ipmsProductionBillProductNumService.list(productionBillProductNumQueryWrapper);
            // 2. 增加库存
            for (IpmsProductionBillProductNum productionBillReceiptProduct : productionBillReceiptProductList) {
                int result = ipmsProductInventoryService.addProductInventory(productionBillReceiptProduct, new ArrayList<>());
                if (result != 1) {
                    throw new BusinessException(ErrorCode.SYSTEM_ERROR, productionBillType + "反审核，调用增加库存失败");
                }
            }
            // 修改源单状态为原来的状态
            // 1. 查询已经审核的生产任务单的所有子件商品，如果生产领料单数量大于 1 且全部是已审核的单据，那把出库状态改为部分出库
            //    如果生产领料单已审核的单据数量大于 1 且只剩下 1 单了，那么出库状态改为，未出库
            Long productionSourceBillId = productionBill.getProductionSourceBillId();
            if (productionSourceBillId != null && productionSourceBillId > 0) {
                queryWrapper = new QueryWrapper<>();
                queryWrapper.eq("production_source_bill_id", productionSourceBillId);
                queryWrapper.eq("check_state", Constant.CHECKED);
                long checkedCount = ipmsProductionBillMapper.selectCount(queryWrapper);
                queryWrapper = new QueryWrapper<>();
                queryWrapper.eq("production_source_bill_id", productionSourceBillId);
                Long checkedAndUncheckedCount = ipmsProductionBillMapper.selectCount(queryWrapper);
                IpmsProductionBill sourceProductionBill = ipmsProductionBillMapper.selectById(productionSourceBillId);
                if (checkedAndUncheckedCount == 1) {
                    sourceProductionBill.setProductionBillId(productionSourceBillId);
                    sourceProductionBill.setPickingState(Constant.NOT_OPERATED);
                    int result = ipmsProductionBillMapper.updateById(sourceProductionBill);
                    if (result != 1) {
                        throw new BusinessException(ErrorCode.SYSTEM_ERROR, "反审核生产领料单，状态修改失败");
                    }
                } else if (checkedAndUncheckedCount > 1 && checkedCount > 1) {
                    sourceProductionBill.setProductionBillId(productionSourceBillId);
                    sourceProductionBill.setPickingState(Constant.PART_OPERATED);
                    int result = ipmsProductionBillMapper.updateById(sourceProductionBill);
                    if (result != 1) {
                        throw new BusinessException(ErrorCode.SYSTEM_ERROR, "反审核生产领料单，状态修改失败");
                    }
                } else {
                    sourceProductionBill.setProductionBillId(productionSourceBillId);
                    sourceProductionBill.setPickingState(Constant.NOT_OPERATED);
                    int result = ipmsProductionBillMapper.updateById(sourceProductionBill);
                    if (result != 1) {
                        throw new BusinessException(ErrorCode.SYSTEM_ERROR, "反审核生产领料单，状态修改失败");
                    }
                }
            }
        } else if (ProductionBillConstant.PRODUCTION_RETURN_ORDER.equals(productionBillType)) {
            // 生产退料单反审核后要调用增加库存的方法
            // 1. 遍历生产退料单商品及数量
            QueryWrapper<IpmsProductionBillProductNum> productionBillProductNumQueryWrapper = new QueryWrapper<>();
            productionBillProductNumQueryWrapper.eq("production_bill_id", productionBillId);
            List<IpmsProductionBillProductNum> productionBillReturnProductList = ipmsProductionBillProductNumService.list(productionBillProductNumQueryWrapper);
            // 2. 减少库存
            for (IpmsProductionBillProductNum productionBillReturnProduct : productionBillReturnProductList) {
                int result = ipmsProductInventoryService.reduceProductInventory(productionBillReturnProduct);
                if (result != 1) {
                    throw new BusinessException(ErrorCode.SYSTEM_ERROR, productionBillType + "反审核，调用减少库存失败");
                }
            }
        } else if (ProductionBillConstant.PRODUCTION_RECEIPT_ORDER.equals(productionBillType)) {
            // 1. 遍历生产入库单商品及数量
            QueryWrapper<IpmsProductionBillProductNum> productionBillProductNumQueryWrapper = new QueryWrapper<>();
            productionBillProductNumQueryWrapper.eq("production_bill_id", productionBillId);
            List<IpmsProductionBillProductNum> productionBillReceiptProductList = ipmsProductionBillProductNumService.list(productionBillProductNumQueryWrapper);
            // 2. 减少库存
            for (IpmsProductionBillProductNum productionBillReceiptProduct : productionBillReceiptProductList) {
                int result = ipmsProductInventoryService.reduceProductInventory(productionBillReceiptProduct);
                if (result != 1) {
                    throw new BusinessException(ErrorCode.SYSTEM_ERROR, productionBillType + "反审核，调用减少库存失败");
                }
            }
            // 修改源单状态为原来的状态
            // 1. 查询已经审核的生产任务单的所有子件商品，如果生产入库单数量大于 1 且全部是已审核的单据，那把出库状态改为部分出库
            //    如果生产入库单已审核的单据数量大于 1 且只剩下 1 单了，那么出库状态改为，未出库
            Long productionSourceBillId = productionBill.getProductionSourceBillId();
            if (productionSourceBillId != null && productionSourceBillId > 0) {
                queryWrapper = new QueryWrapper<>();
                queryWrapper.eq("production_source_bill_id", productionSourceBillId);
                queryWrapper.eq("check_state", Constant.CHECKED);
                queryWrapper.eq("production_bill_type", ProductionBillConstant.PRODUCTION_RECEIPT_ORDER);
                long checkedCount = ipmsProductionBillMapper.selectCount(queryWrapper);
                queryWrapper = new QueryWrapper<>();
                queryWrapper.eq("production_source_bill_id", productionSourceBillId);
                queryWrapper.eq("production_bill_type", ProductionBillConstant.PRODUCTION_RECEIPT_ORDER);
                Long checkedAndUncheckedCount = ipmsProductionBillMapper.selectCount(queryWrapper);
                IpmsProductionBill sourceProductionBill = ipmsProductionBillMapper.selectById(productionSourceBillId);
                if (checkedAndUncheckedCount == 1) {
                    sourceProductionBill.setProductionBillId(productionSourceBillId);
                    sourceProductionBill.setWarehousingState(Constant.NOT_OPERATED);
                    int result = ipmsProductionBillMapper.updateById(sourceProductionBill);
                    if (result != 1) {
                        throw new BusinessException(ErrorCode.SYSTEM_ERROR, "反审核生产入库单，状态修改失败");
                    }
                } else if (checkedAndUncheckedCount > 1 && checkedCount > 1) {
                    sourceProductionBill.setProductionBillId(productionSourceBillId);
                    sourceProductionBill.setWarehousingState(Constant.PART_OPERATED);
                    int result = ipmsProductionBillMapper.updateById(sourceProductionBill);
                    if (result != 1) {
                        throw new BusinessException(ErrorCode.SYSTEM_ERROR, "反审核生产入库单，状态修改失败");
                    }
                } else {
                    sourceProductionBill.setProductionBillId(productionSourceBillId);
                    sourceProductionBill.setWarehousingState(Constant.NOT_OPERATED);
                    int result = ipmsProductionBillMapper.updateById(sourceProductionBill);
                    if (result != 1) {
                        throw new BusinessException(ErrorCode.SYSTEM_ERROR, "反审核生产入库单，状态修改失败");
                    }
                }
            }
        } else if (ProductionBillConstant.PRODUCTION_STOCK_RETURN_ORDER.equals(productionBillType)) {
            // 生产退库单反审核后要调用增加库存的方法
            // 1. 遍历生产退库单商品及数量
            QueryWrapper<IpmsProductionBillProductNum> productionBillProductNumQueryWrapper = new QueryWrapper<>();
            productionBillProductNumQueryWrapper.eq("production_bill_id", productionBillId);
            List<IpmsProductionBillProductNum> productionBillReturnProductList = ipmsProductionBillProductNumService.list(productionBillProductNumQueryWrapper);
            // 2. 增加库存
            for (IpmsProductionBillProductNum productionBillReturnProduct : productionBillReturnProductList) {
                int result = ipmsProductInventoryService.addProductInventory(productionBillReturnProduct, new ArrayList<>());
                if (result != 1) {
                    throw new BusinessException(ErrorCode.SYSTEM_ERROR, productionBillType + "反审核，调用增加库存失败");
                }
            }
        }
        int result = ipmsProductionBillMapper.updateById(unCheckingProductionBill);
        if (result != 1) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR);
        }
        return result;
    }

    @Override
    @Transactional
    public int deleteProductionBillById(long id) {
        // 1. 判断 id 是否合法
        if (id <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "id 不合法");
        }
        // 2. 审核后的单据无法删除
        IpmsProductionBill oldProductionBill = ipmsProductionBillMapper.selectById(id);
        if (oldProductionBill == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }
        if (oldProductionBill.getCheckState().equals(Constant.CHECKED)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "单据已经审核无法删除");
        }
        // 3. 按照单据类型分类别删除
        String productionBillType = oldProductionBill.getProductionBillType();
        Long productionSourceBillId = oldProductionBill.getProductionSourceBillId();
        QueryWrapper<IpmsProductionBillProductNum> productionBillProductNumQueryWrapper;
        if (ProductionBillConstant.PRODUCTION_PICKING_ORDER.equals(productionBillType)) {
            if (productionSourceBillId != null) {
                productionBillProductNumQueryWrapper = new QueryWrapper<>();
                productionBillProductNumQueryWrapper.eq("production_bill_id", id);
                List<IpmsProductionBillProductNum> productionReceiptBillProductList = ipmsProductionBillProductNumService.list(productionBillProductNumQueryWrapper);
                for (IpmsProductionBillProductNum productionReceiptBillProductNum : productionReceiptBillProductList) {
                    productionBillProductNumQueryWrapper = new QueryWrapper<>();
                    productionBillProductNumQueryWrapper.eq("production_bill_id", productionSourceBillId);
                    productionBillProductNumQueryWrapper.eq("product_id", productionReceiptBillProductNum.getProductId());
                    productionBillProductNumQueryWrapper.eq("warehouse_id", productionReceiptBillProductNum.getWarehouseId());
                    if (productionReceiptBillProductNum.getWarehousePositionId() != null) {
                        productionBillProductNumQueryWrapper.eq("warehouse_position_id", productionReceiptBillProductNum.getWarehouseId());
                    }
                    // 生产任务单的商品也就是源单的商品
                    IpmsProductionBillProductNum sourceProductOfOne = ipmsProductionBillProductNumService.getOne(productionBillProductNumQueryWrapper);
                    // 修改源单商品数量为未必生产出库单引用的剩余数量
                    if (sourceProductOfOne != null) {
                        BigDecimal oldSurplusNeedExecutionProductNum = sourceProductOfOne.getSurplusNeedExecutionProductNum();
                        sourceProductOfOne.setSurplusNeedExecutionProductNum(oldSurplusNeedExecutionProductNum.add(productionReceiptBillProductNum.getNeedExecutionProductNum()));
                        boolean recoverSourceProductNumResult = ipmsProductionBillProductNumService.updateById(sourceProductOfOne);
                        if (!recoverSourceProductNumResult) {
                            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "恢复生产任务单剩余数量失败");
                        }
                    }
                }
                // 修改生产任务单状态
                productionBillProductNumQueryWrapper = new QueryWrapper<>();
                productionBillProductNumQueryWrapper.eq("production_bill_id", productionSourceBillId);
                List<IpmsProductionBillProductNum> sourceProductList = ipmsProductionBillProductNumService.list(productionBillProductNumQueryWrapper);
                IpmsProductionBill sourceProductionBill = ipmsProductionBillMapper.selectById(productionSourceBillId);
                int temp = 0;
                for (IpmsProductionBillProductNum sourceProduct : sourceProductList) {
                    temp++;
                    if (!sourceProduct.getNeedExecutionProductNum().equals(sourceProduct.getSurplusNeedExecutionProductNum())) {
                        sourceProductionBill.setProductionBillId(productionSourceBillId);
                        // 设置执行状态
                        sourceProductionBill.setPickingExecutionState(Constant.PART_OPERATED);
                        int result = ipmsProductionBillMapper.updateById(sourceProductionBill);
                        if (result != 1) {
                            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "删除生产领料单，状态修改失败");
                        }
                        break;
                    }
                    if (temp == sourceProductList.size()) {
                        sourceProductionBill.setProductionBillId(productionSourceBillId);
                        sourceProductionBill.setPickingExecutionState(Constant.NOT_OPERATED);
                        int result = ipmsProductionBillMapper.updateById(sourceProductionBill);
                        if (result != 1) {
                            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "删除生产领料单，状态修改失败");
                        }
                    }
                }
            }
        } else if (ProductionBillConstant.PRODUCTION_RETURN_ORDER.equals(productionBillType)) {
            if (productionSourceBillId != null) {
                productionBillProductNumQueryWrapper = new QueryWrapper<>();
                productionBillProductNumQueryWrapper.eq("production_bill_id", id);
                List<IpmsProductionBillProductNum> productionReceiptBillProductList = ipmsProductionBillProductNumService.list(productionBillProductNumQueryWrapper);
                for (IpmsProductionBillProductNum productionReceiptBillProductNum : productionReceiptBillProductList) {
                    productionBillProductNumQueryWrapper = new QueryWrapper<>();
                    productionBillProductNumQueryWrapper.eq("production_bill_id", productionSourceBillId);
                    productionBillProductNumQueryWrapper.eq("product_id", productionReceiptBillProductNum.getProductId());
                    productionBillProductNumQueryWrapper.eq("warehouse_id", productionReceiptBillProductNum.getWarehouseId());
                    if (productionReceiptBillProductNum.getWarehousePositionId() != null) {
                        productionBillProductNumQueryWrapper.eq("warehouse_position_id", productionReceiptBillProductNum.getWarehouseId());
                    }
                    // 生产领料单的商品也就是源单的商品
                    IpmsProductionBillProductNum sourceProductOfOne = ipmsProductionBillProductNumService.getOne(productionBillProductNumQueryWrapper);
                    // 修改源单商品数量为未被生产退料单引用的剩余数量
                    if (sourceProductOfOne != null) {
                        BigDecimal oldSurplusNeedExecutionProductNum = sourceProductOfOne.getSurplusNeedExecutionProductNum();
                        sourceProductOfOne.setSurplusNeedExecutionProductNum(oldSurplusNeedExecutionProductNum.add(productionReceiptBillProductNum.getNeedExecutionProductNum()));
                        boolean recoverSourceProductNumResult = ipmsProductionBillProductNumService.updateById(sourceProductOfOne);
                        if (!recoverSourceProductNumResult) {
                            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "恢复生产领料单剩余数量失败");
                        }
                    }
                }
            }
        } else if (ProductionBillConstant.PRODUCTION_RECEIPT_ORDER.equals(productionBillType)) {
            if (productionSourceBillId != null) {
                productionBillProductNumQueryWrapper = new QueryWrapper<>();
                productionBillProductNumQueryWrapper.eq("production_bill_id", id);
                QueryWrapper<IpmsProductionBill> productionBillQueryWrapper;
                List<IpmsProductionBillProductNum> productionReceiptBillProductList = ipmsProductionBillProductNumService.list(productionBillProductNumQueryWrapper);
                for (IpmsProductionBillProductNum productionReceiptBillProductNum : productionReceiptBillProductList) {
                    productionBillQueryWrapper = new QueryWrapper<>();
                    productionBillQueryWrapper.eq("production_bill_id", productionSourceBillId);
                    productionBillQueryWrapper.eq("product_id", productionReceiptBillProductNum.getProductId());
                    productionBillQueryWrapper.eq("warehouse_id", productionReceiptBillProductNum.getWarehouseId());
                    if (productionReceiptBillProductNum.getWarehousePositionId() != null) {
                        productionBillQueryWrapper.eq("warehouse_position_id", productionReceiptBillProductNum.getWarehouseId());
                    }
                    // 生产入库单的源单，生产任务单，它的组件商品
                    IpmsProductionBill taskOrderFatherProduct = ipmsProductionBillMapper.selectOne(productionBillQueryWrapper);
                    // 修改源单商品数量为未必生产出库单引用的剩余数量
                    if (taskOrderFatherProduct != null) {
                        BigDecimal oldSurplusNeedExecutionProductNum = taskOrderFatherProduct.getSurplusNeedWarehousingProductNum();
                        taskOrderFatherProduct.setSurplusNeedWarehousingProductNum(oldSurplusNeedExecutionProductNum.add(productionReceiptBillProductNum.getNeedExecutionProductNum()));
                        int result = ipmsProductionBillMapper.updateById(taskOrderFatherProduct);
                        if (result != 1) {
                            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "恢复生产任务单剩余数量失败");
                        }
                    }
                }
                // 修改生产任务单状态
                productionBillQueryWrapper = new QueryWrapper<>();
                productionBillQueryWrapper.eq("production_bill_id", productionSourceBillId);
                List<IpmsProductionBill> sourceProductList = ipmsProductionBillMapper.selectList(productionBillQueryWrapper);
                IpmsProductionBill sourceProductionBill = ipmsProductionBillMapper.selectById(productionSourceBillId);
                int temp = 0;
                for (IpmsProductionBill sourceProduct : sourceProductList) {
                    temp++;
                    if (!sourceProduct.getNeedWarehousingProductNum().equals(sourceProduct.getSurplusNeedWarehousingProductNum())) {
                        sourceProductionBill.setProductionBillId(productionSourceBillId);
                        // 设置执行状态
                        sourceProductionBill.setWarehousingExecutionState(Constant.PART_OPERATED);
                        sourceProductionBill.setFinishState(ProductionBillConstant.UNFINISHED);
                        sourceProductionBill.setFinisher(null);
                        sourceProductionBill.setFinishTime(null);
                        int result = ipmsProductionBillMapper.updateById(sourceProductionBill);
                        if (result != 1) {
                            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "删除生产入库单，状态修改失败");
                        }
                        break;
                    }
                    if (temp == sourceProductList.size()) {
                        sourceProductionBill.setProductionBillId(productionSourceBillId);
                        sourceProductionBill.setWarehousingExecutionState(Constant.NOT_OPERATED);
                        sourceProductionBill.setFinishState(ProductionBillConstant.UNFINISHED);
                        sourceProductionBill.setFinisher(null);
                        sourceProductionBill.setFinishTime(null);
                        int result = ipmsProductionBillMapper.updateById(sourceProductionBill);
                        if (result != 1) {
                            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "删除生产入库单，状态修改失败");
                        }
                    }
                }
            }
        } else if (ProductionBillConstant.PRODUCTION_STOCK_RETURN_ORDER.equals(productionBillType)) {
            if (productionSourceBillId != null) {
                productionBillProductNumQueryWrapper = new QueryWrapper<>();
                productionBillProductNumQueryWrapper.eq("production_bill_id", id);
                List<IpmsProductionBillProductNum> productionReceiptBillProductList = ipmsProductionBillProductNumService.list(productionBillProductNumQueryWrapper);
                for (IpmsProductionBillProductNum productionReceiptBillProductNum : productionReceiptBillProductList) {
                    productionBillProductNumQueryWrapper = new QueryWrapper<>();
                    productionBillProductNumQueryWrapper.eq("production_bill_id", productionSourceBillId);
                    productionBillProductNumQueryWrapper.eq("product_id", productionReceiptBillProductNum.getProductId());
                    productionBillProductNumQueryWrapper.eq("warehouse_id", productionReceiptBillProductNum.getWarehouseId());
                    if (productionReceiptBillProductNum.getWarehousePositionId() != null) {
                        productionBillProductNumQueryWrapper.eq("warehouse_position_id", productionReceiptBillProductNum.getWarehouseId());
                    }
                    // 生产入库单的商品也就是源单的商品
                    IpmsProductionBillProductNum sourceProductOfOne = ipmsProductionBillProductNumService.getOne(productionBillProductNumQueryWrapper);
                    // 修改源单商品数量为未被生产退料单引用的剩余数量
                    if (sourceProductOfOne != null) {
                        BigDecimal oldSurplusNeedExecutionProductNum = sourceProductOfOne.getSurplusNeedExecutionProductNum();
                        sourceProductOfOne.setSurplusNeedExecutionProductNum(oldSurplusNeedExecutionProductNum.add(productionReceiptBillProductNum.getNeedExecutionProductNum()));
                        boolean recoverSourceProductNumResult = ipmsProductionBillProductNumService.updateById(sourceProductOfOne);
                        if (!recoverSourceProductNumResult) {
                            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "恢复生产入库单剩余数量失败");
                        }
                    }
                }
            }
        }
        int result = ipmsProductionBillMapper.deleteById(id);
        if (result != 1) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "删除失败");
        }
        productionBillProductNumQueryWrapper = new QueryWrapper<>();
        productionBillProductNumQueryWrapper.eq("production_bill_id", id);
        boolean remove = ipmsProductionBillProductNumService.remove(productionBillProductNumQueryWrapper);
        if (!remove) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "关联删除失败");
        }
        return result;
    }

    @Override
    public int updateProductionBill(UpdateProductionBillRequest updateProductionBillRequest, HttpServletRequest request) {
        Long productionBillId = updateProductionBillRequest.getProductionBillId();
        if (productionBillId == null || productionBillId <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "单据 id 为空或者不合法");
        }
        // 判断该单据是否存在
        IpmsProductionBill oldProductionBill = ipmsProductionBillMapper.selectById(productionBillId);
        if (oldProductionBill == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "单据不存在");
        }
        Integer checkState = oldProductionBill.getCheckState();
        if (Constant.CHECKED == checkState) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "单据已审核，无法修改");
        }
        String productionBillCode = updateProductionBillRequest.getProductionBillCode();
        if (productionBillCode != null) {
            if (!productionBillCode.equals(oldProductionBill.getProductionBillCode())) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "单据编号不支持修改");
            }
        }
        Long employeeId = updateProductionBillRequest.getEmployeeId();
        Long departmentId = updateProductionBillRequest.getDepartmentId();
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
        String productionBillType = updateProductionBillRequest.getProductionBillType();
        if (productionBillType != null) {
            if (!productionBillType.equals(oldProductionBill.getProductionBillType())) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "单据类型不能修改");
            }
        }
        // 仓管员 id，可以为空，如果不为空，那么必须存在该员工
        Long storekeeperId = updateProductionBillRequest.getStorekeeperId();
        if (storekeeperId != null) {
            IpmsEmployee validEmployee = ipmsEmployeeService.getById(storekeeperId);
            if (validEmployee == null) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "单据仓管不存在");
            }
        }
        // 生产单据的商品及商品数量至少存在一个
        List<UpdateProductionProductNumRequest> updateProductionProductNumRequestList = updateProductionBillRequest.getUpdateProductionProductNumRequestList();
        if (updateProductionProductNumRequestList == null || updateProductionProductNumRequestList.size() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "生产的商品为空，至少要存在一个生产商品");
        }
        String productionBillBusinessType = updateProductionBillRequest.getProductionBillBusinessType();
        String productionBillReturnReason = updateProductionBillRequest.getProductionBillReturnReason();
        Long productId = updateProductionBillRequest.getProductId();
        Long warehouseId = updateProductionBillRequest.getWarehouseId();
        Long warehousePositionId = updateProductionBillRequest.getWarehousePositionId();
        BigDecimal productNum = updateProductionBillRequest.getProductNum();
        String planCompletionDate = updateProductionBillRequest.getPlanCompletionDate();
        String planCommencementDate = updateProductionBillRequest.getPlanCommencementDate();
        String productRemark = updateProductionBillRequest.getProductRemark();
        boolean validPickingAndReceipt = productionBillBusinessType != null || productionBillReturnReason != null || productId != null || warehouseId != null ||
                warehousePositionId != null || productNum != null || planCompletionDate != null || planCommencementDate != null || productRemark != null;
        boolean validReturnAndStockReturn = productionBillBusinessType != null || productId != null || warehouseId != null ||
                warehousePositionId != null || productNum != null || planCompletionDate != null || planCommencementDate != null || productRemark != null;
        switch (oldProductionBill.getProductionBillType()) {
            case ProductionBillConstant.PRODUCTION_TASK_ORDER:
                if (productionBillReturnReason != null) {
                    throw new BusinessException(ErrorCode.PARAMS_ERROR, productionBillType + "不需要输入退料或者退库原因");
                }
                if (storekeeperId != null) {
                    throw new BusinessException(ErrorCode.PARAMS_ERROR, productionBillType + "不需要仓管 id");
                }
                if (productId != null) {
                    IpmsProduct product = ipmsProductService.getById(productId);
                    if (product == null) {
                        throw new BusinessException(ErrorCode.PARAMS_ERROR, productionBillType + "的商品不存在");
                    }
                }
                if (warehouseId != null) {
                    IpmsWarehouse warehouse = ipmsWarehouseService.getById(warehouseId);
                    if (warehouse == null) {
                        throw new BusinessException(ErrorCode.PARAMS_ERROR, productionBillType + "商品的仓库不存在");
                    }
                    Integer isWarehousePositionManagement = warehouse.getIsWarehousePositionManagement();
                    if (WarehouseConstant.OPEN_WAREHOUSE_POSITION_MANAGEMENT == isWarehousePositionManagement) {
                        if (warehousePositionId == null || warehousePositionId <= 0) {
                            throw new BusinessException(ErrorCode.PARAMS_ERROR, productionBillType + "商品的仓位 id 为空或者不合法");
                        }
                        IpmsWarehousePosition warehousePosition = ipmsWarehousePositionService.getById(warehousePositionId);
                        if (warehousePosition == null) {
                            throw new BusinessException(ErrorCode.PARAMS_ERROR, productionBillType + "商品的仓位不存在");
                        }
                        if (!warehouse.getWarehouseId().equals(warehousePosition.getWarehouseId())) {
                            throw new BusinessException(ErrorCode.PARAMS_ERROR, productionBillType + "商品的仓位不属于该仓库");
                        }
                    } else {
                        if (warehousePositionId != null) {
                            throw new BusinessException(ErrorCode.PARAMS_ERROR, "未开启仓位管理，不应该有仓位 id");
                        }
                    }
                }
                if (productNum != null) {
                    if (productNum.doubleValue() <= 0) {
                        throw new BusinessException(ErrorCode.PARAMS_ERROR, productionBillType + "商品数量为空或者数量小于等于 0");
                    }
                }
                break;
            case ProductionBillConstant.PRODUCTION_PICKING_ORDER:
            case ProductionBillConstant.PRODUCTION_RECEIPT_ORDER:
                if (validPickingAndReceipt) {
                    throw new BusinessException(ErrorCode.PARAMS_ERROR, "请检查表单数据，" + productionBillType + "不需要输入生产单据业务类型、生产单据退还原因、" +
                            "父级商品 id、父级商品仓库 id、父级商品仓位 id、需要入库的商品数量、计划开工日期、计划完工日期和商品备注");
                }
                break;
            case ProductionBillConstant.PRODUCTION_RETURN_ORDER:
            case ProductionBillConstant.PRODUCTION_STOCK_RETURN_ORDER:
                if (validReturnAndStockReturn) {
                    throw new BusinessException(ErrorCode.PARAMS_ERROR, "请检查表单数据，" + productionBillType + "不需要输入生产单据业务类型、" +
                            "父级商品 id、父级商品仓库 id、父级商品仓位 id、需要入库的商品数量、计划开工日期、计划完工日期和商品备注");
                }
                break;
        }
        // 更新生产单据
        IpmsProductionBill newProductionBill = new IpmsProductionBill();
        BeanUtils.copyProperties(updateProductionBillRequest, newProductionBill);
        SafeUserVO loginUser = ipmsUserService.getLoginUser(request);
        newProductionBill.setModifier(loginUser.getUserName());
        newProductionBill.setUpdateTime(new Date());
        int result = ipmsProductionBillMapper.updateById(newProductionBill);
        if (result != 1) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "修改生产单据失败");
        }
        List<Long> updateAndInsertProductionBillProductList = new ArrayList<>();
        for (UpdateProductionProductNumRequest updateProductionProductNumRequest : updateProductionProductNumRequestList) {
            Long productionBillProductId = updateProductionProductNumRequest.getProductionBillProductId();
            // 如果存在 productionBillProductId，那么肯定是要更新的数据，进行更新，并且存入列表中
            if (productionBillProductId != null && productionBillProductId > 0) {
                updateAndInsertProductionBillProductList.add(productionBillProductId);
                ipmsProductionBillProductNumService.updateProductionBillProductAndNum(updateProductionProductNumRequest, oldProductionBill);
            } else {
                // 否则，就是插入新的数据
                AddProductionProductNumRequest addProductionProductNumRequest = new AddProductionProductNumRequest();
                BeanUtils.copyProperties(updateProductionProductNumRequest, addProductionProductNumRequest);
                long insertProductionBillProductId = ipmsProductionBillProductNumService.addProductionBillProductAndNum(addProductionProductNumRequest, oldProductionBill);
                if (insertProductionBillProductId < 0) {
                    throw new BusinessException(ErrorCode.SYSTEM_ERROR, "增加生产单据商品失败");
                }
                updateAndInsertProductionBillProductList.add(insertProductionBillProductId);
            }
        }
        if (updateAndInsertProductionBillProductList.size() > 0) {
            // 如果更新生产单据商品的 id 不在这个列表内，那么删除生产单据
            QueryWrapper<IpmsProductionBillProductNum> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("production_bill_id", productionBillId);
            queryWrapper.notIn("production_bill_product_id", updateAndInsertProductionBillProductList);
            List<IpmsProductionBillProductNum> willRemoveProductionBillProductList = ipmsProductionBillProductNumService.list(queryWrapper);
            for (IpmsProductionBillProductNum willRemoveProductionBillProduct : willRemoveProductionBillProductList) {
                // 即将被删除的数据，如果是来源源单，恢复原单源的数据
                QueryWrapper<IpmsProductionBillProductNum> productionBillProductNumQueryWrapper = new QueryWrapper<>();
                productionBillProductNumQueryWrapper.eq("production_bill_id", oldProductionBill.getProductionSourceBillId());
                productionBillProductNumQueryWrapper.eq("product_id", willRemoveProductionBillProduct.getProductId());
                IpmsProductionBillProductNum sourceProductionBillProduct = ipmsProductionBillProductNumService.getOne(productionBillProductNumQueryWrapper);
                if (sourceProductionBillProduct != null) {
                    sourceProductionBillProduct.setSurplusNeedExecutionProductNum(willRemoveProductionBillProduct.getNeedExecutionProductNum());
                    ipmsProductionBillProductNumService.updateById(sourceProductionBillProduct);
                }
            }
            // 删除
            ipmsProductionBillProductNumService.remove(queryWrapper);
        }
        // 生产订单修改不会有什么状态改变
        // 生产退货单修改也不会有什么状态改变，因为生产出库单，只有审核状态
        // 生产出库单修改会有 3 种情况：
        // 第一种：生产订单的完全执行状态改为部分执行状态，已关闭状态改为未关闭状态
        // 第二种：生产订单的部分执行状态改为完全执行状态，未关闭状态改为已关闭状态
        // 第三种：生产订单的部分执行状态不变，未关闭状态不变
        // 最后就是不做任何修改的操作，那么就是不变
        Long productionSourceBillId = oldProductionBill.getProductionSourceBillId();
        if (productionSourceBillId != null && productionSourceBillId > 0) {
            // 如果代码执行到这里，那么已经修改完了数据里面生产订单剩余需要出库的商品数量
            IpmsProductionBill sourceProductionBill = ipmsProductionBillMapper.selectById(productionSourceBillId);
            if (ProductionBillConstant.PRODUCTION_PICKING_ORDER.equals(oldProductionBill.getProductionBillType())) {
                QueryWrapper<IpmsProductionBillProductNum> queryWrapper = new QueryWrapper<>();
                queryWrapper.eq("production_bill_id", sourceProductionBill.getProductionBillId());
                List<IpmsProductionBillProductNum> sourceProductionBillProductList = ipmsProductionBillProductNumService.list(queryWrapper);
                int temp = 0;
                for (IpmsProductionBillProductNum productionBillProductNum : sourceProductionBillProductList) {
                    temp++;
                    if (productionBillProductNum.getSurplusNeedExecutionProductNum().doubleValue() != 0) {
                        sourceProductionBill.setPickingExecutionState(Constant.PART_OPERATED);
                        ipmsProductionBillMapper.updateById(sourceProductionBill);
                        break;
                    }
                    if (temp == sourceProductionBillProductList.size()) {
                        sourceProductionBill.setPickingExecutionState(Constant.FULL_OPERATED);
                        ipmsProductionBillMapper.updateById(sourceProductionBill);
                    }
                }
            } else if (ProductionBillConstant.PRODUCTION_RECEIPT_ORDER.equals(oldProductionBill.getProductionBillType())) {
                BigDecimal surplusNeedWarehousingProductNum = sourceProductionBill.getSurplusNeedWarehousingProductNum();
                if (surplusNeedWarehousingProductNum.doubleValue() != 0) {
                    sourceProductionBill.setWarehousingExecutionState(Constant.PART_OPERATED);
                    sourceProductionBill.setFinisher(null);
                    sourceProductionBill.setFinishTime(null);
                    sourceProductionBill.setFinishState(ProductionBillConstant.UNFINISHED);
                } else {
                    sourceProductionBill.setWarehousingExecutionState(Constant.FULL_OPERATED);
                    sourceProductionBill.setFinishState(ProductionBillConstant.FINISHED);
                    sourceProductionBill.setFinishTime(new Date());
                    sourceProductionBill.setFinisher(ipmsUserService.getLoginUser(request).getUserName());
                }
                ipmsProductionBillMapper.updateById(sourceProductionBill);
            }
        }
        return result;
    }

    @Override
    public Page<SafeProductionBillVO> selectSourceProductionBill(ProductionBillQueryRequest productionBillQueryRequest) {
        String productionBillType = productionBillQueryRequest.getProductionBillType();
        if (productionBillType == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "单据类型不能为空");
        }
        if (productionBillType.equals(ProductionBillConstant.PRODUCTION_TASK_ORDER)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, productionBillType + "没有源单据");
        }
        List<String> billTypeList = Arrays.asList(ProductionBillConstant.PRODUCTION_TASK_ORDER, ProductionBillConstant.PRODUCTION_PICKING_ORDER,
                ProductionBillConstant.PRODUCTION_RETURN_ORDER, ProductionBillConstant.PRODUCTION_RECEIPT_ORDER, ProductionBillConstant.PRODUCTION_STOCK_RETURN_ORDER);
        int validTypeResult = ValidType.valid(billTypeList, productionBillType);
        if (validTypeResult == 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "单据类型不存在");
        }
        // 2. 进行分页查询，分情况查询，生产订单，生产出库单和生产退货单
        Page<IpmsProductionBill> page = new Page<>(productionBillQueryRequest.getCurrentPage(), productionBillQueryRequest.getPageSize());
        QueryWrapper<IpmsProductionBill> ipmsProductionBillQueryWrapper = new QueryWrapper<>();
        String fuzzyText = productionBillQueryRequest.getFuzzyText();
        // 不显示已经完全作为源单任务的源单信息
        // 首先查出该类型的所有单据
        // 然后根据上面的 id，查出对于的单据商品信息，查看对于的剩余被引用的单据商品数量，存在一个不为 0，那么该单据还能被查询出来
        // 生产任务单特殊处理
        // 定义一个列表用于存放还有剩余商品数量 id
        List<Long> hasSurplusProductionBillProductIdList = new ArrayList<>();
        QueryWrapper<IpmsProductionBill> productionBillQueryWrapper;
        QueryWrapper<IpmsProductionBillProductNum> productionBillProductNumQueryWrapper;
        switch (productionBillType) {
            case ProductionBillConstant.PRODUCTION_RECEIPT_ORDER: {
                // 搜索出所有的生产入库单信息
                productionBillQueryWrapper = new QueryWrapper<>();
                productionBillQueryWrapper.eq("production_bill_type", ProductionBillConstant.PRODUCTION_TASK_ORDER);
                List<IpmsProductionBill> productionBillList = ipmsProductionBillMapper.selectList(productionBillQueryWrapper);
                for (IpmsProductionBill productionBill : productionBillList) {
                    if (productionBill.getSurplusNeedWarehousingProductNum().doubleValue() != 0) {
                        hasSurplusProductionBillProductIdList.add(productionBill.getProductionBillId());
                    }
                }
                if (hasSurplusProductionBillProductIdList.size() > 0) {
                    if (StringUtils.isNotBlank(fuzzyText)) {
                        if (StringUtils.isNotBlank(fuzzyText)) {
                            ipmsProductionBillQueryWrapper.like("production_bill_code", fuzzyText)
                                    .and(billType -> billType.eq("production_bill_type", ProductionBillConstant.PRODUCTION_TASK_ORDER))
                                    .and(productionBillId -> productionBillId.in("production_bill_id", hasSurplusProductionBillProductIdList)).or()
                                    .like("production_bill_date", fuzzyText)
                                    .and(billType -> billType.eq("production_bill_type", ProductionBillConstant.PRODUCTION_TASK_ORDER))
                                    .and(productionBillId -> productionBillId.in("production_bill_id", hasSurplusProductionBillProductIdList)).or()
                                    .like("production_bill_settlement_date", fuzzyText)
                                    .and(billType -> billType.eq("production_bill_type", ProductionBillConstant.PRODUCTION_TASK_ORDER))
                                    .and(productionBillId -> productionBillId.in("production_bill_id", hasSurplusProductionBillProductIdList)).or()
                                    .like("production_bill_remark", fuzzyText)
                                    .and(billType -> billType.eq("production_bill_type", ProductionBillConstant.PRODUCTION_TASK_ORDER))
                                    .and(productionBillId -> productionBillId.in("production_bill_id", hasSurplusProductionBillProductIdList)).or()
                                    .like("production_bill_currency_type", fuzzyText)
                                    .and(billType -> billType.eq("production_bill_type", ProductionBillConstant.PRODUCTION_TASK_ORDER))
                                    .and(productionBillId -> productionBillId.in("production_bill_id", hasSurplusProductionBillProductIdList)).or()
                                    .like("production_bill_exchange_rate", fuzzyText)
                                    .and(billType -> billType.eq("production_bill_type", ProductionBillConstant.PRODUCTION_TASK_ORDER))
                                    .and(productionBillId -> productionBillId.in("production_bill_id", hasSurplusProductionBillProductIdList));
                        }
                    } else {
                        ipmsProductionBillQueryWrapper.eq("production_bill_type", ProductionBillConstant.PRODUCTION_TASK_ORDER);
                        ipmsProductionBillQueryWrapper.in("production_bill_id", hasSurplusProductionBillProductIdList);
                    }
                } else {
                    return new PageDTO<>();
                }
                break;
            }
            case ProductionBillConstant.PRODUCTION_STOCK_RETURN_ORDER: {
                productionBillQueryWrapper = new QueryWrapper<>();
                productionBillProductNumQueryWrapper = new QueryWrapper<>();
                productionBillQueryWrapper.eq("production_bill_type", ProductionBillConstant.PRODUCTION_RECEIPT_ORDER);
                List<IpmsProductionBill> productionBillList = ipmsProductionBillMapper.selectList(productionBillQueryWrapper);
                for (IpmsProductionBill productionBill : productionBillList) {
                    productionBillProductNumQueryWrapper.eq("production_bill_id", productionBill.getProductionBillId());
                    productionBillProductNumQueryWrapper.gt("surplus_need_execution_product_num", 0);
                    List<IpmsProductionBillProductNum> list = ipmsProductionBillProductNumService.list(productionBillProductNumQueryWrapper);
                    if (list != null && list.size() > 0) {
                        hasSurplusProductionBillProductIdList.add(productionBill.getProductionBillId());
                    }
                }
                if (hasSurplusProductionBillProductIdList.size() > 0) {
                    if (StringUtils.isNotBlank(fuzzyText)) {
                        if (StringUtils.isNotBlank(fuzzyText)) {
                            ipmsProductionBillQueryWrapper.like("production_bill_code", fuzzyText)
                                    .and(billType -> billType.eq("production_bill_type", ProductionBillConstant.PRODUCTION_RECEIPT_ORDER))
                                    .and(productionBillId -> productionBillId.in("production_bill_id", hasSurplusProductionBillProductIdList)).or()
                                    .like("production_bill_date", fuzzyText)
                                    .and(billType -> billType.eq("production_bill_type", ProductionBillConstant.PRODUCTION_RECEIPT_ORDER))
                                    .and(productionBillId -> productionBillId.in("production_bill_id", hasSurplusProductionBillProductIdList)).or()
                                    .like("production_bill_settlement_date", fuzzyText)
                                    .and(billType -> billType.eq("production_bill_type", ProductionBillConstant.PRODUCTION_RECEIPT_ORDER))
                                    .and(productionBillId -> productionBillId.in("production_bill_id", hasSurplusProductionBillProductIdList)).or()
                                    .like("production_bill_remark", fuzzyText)
                                    .and(billType -> billType.eq("production_bill_type", ProductionBillConstant.PRODUCTION_RECEIPT_ORDER))
                                    .and(productionBillId -> productionBillId.in("production_bill_id", hasSurplusProductionBillProductIdList)).or()
                                    .like("production_bill_currency_type", fuzzyText)
                                    .and(billType -> billType.eq("production_bill_type", ProductionBillConstant.PRODUCTION_RECEIPT_ORDER))
                                    .and(productionBillId -> productionBillId.in("production_bill_id", hasSurplusProductionBillProductIdList)).or()
                                    .like("production_bill_exchange_rate", fuzzyText)
                                    .and(billType -> billType.eq("production_bill_type", ProductionBillConstant.PRODUCTION_RECEIPT_ORDER))
                                    .and(productionBillId -> productionBillId.in("production_bill_id", hasSurplusProductionBillProductIdList));
                        }
                    } else {
                        ipmsProductionBillQueryWrapper.eq("production_bill_type", ProductionBillConstant.PRODUCTION_RECEIPT_ORDER);
                        ipmsProductionBillQueryWrapper.in("production_bill_id", hasSurplusProductionBillProductIdList);
                    }
                } else {
                    return new PageDTO<>();
                }
                break;
            }
            case ProductionBillConstant.PRODUCTION_PICKING_ORDER: {
                productionBillQueryWrapper = new QueryWrapper<>();
                productionBillProductNumQueryWrapper = new QueryWrapper<>();
                productionBillQueryWrapper.eq("production_bill_type", ProductionBillConstant.PRODUCTION_TASK_ORDER);
                List<IpmsProductionBill> productionBillList = ipmsProductionBillMapper.selectList(productionBillQueryWrapper);
                for (IpmsProductionBill productionBill : productionBillList) {
                    productionBillProductNumQueryWrapper.eq("production_bill_id", productionBill.getProductionBillId());
                    productionBillProductNumQueryWrapper.gt("surplus_need_execution_product_num", 0);
                    List<IpmsProductionBillProductNum> list = ipmsProductionBillProductNumService.list(productionBillProductNumQueryWrapper);
                    if (list != null && list.size() > 0) {
                        hasSurplusProductionBillProductIdList.add(productionBill.getProductionBillId());
                    }
                }
                if (hasSurplusProductionBillProductIdList.size() > 0) {
                    if (StringUtils.isNotBlank(fuzzyText)) {
                        if (StringUtils.isNotBlank(fuzzyText)) {
                            ipmsProductionBillQueryWrapper.like("production_bill_code", fuzzyText)
                                    .and(billType -> billType.eq("production_bill_type", ProductionBillConstant.PRODUCTION_TASK_ORDER))
                                    .and(productionBillId -> productionBillId.in("production_bill_id", hasSurplusProductionBillProductIdList)).or()
                                    .like("production_bill_date", fuzzyText)
                                    .and(billType -> billType.eq("production_bill_type", ProductionBillConstant.PRODUCTION_TASK_ORDER))
                                    .and(productionBillId -> productionBillId.in("production_bill_id", hasSurplusProductionBillProductIdList)).or()
                                    .like("production_bill_settlement_date", fuzzyText)
                                    .and(billType -> billType.eq("production_bill_type", ProductionBillConstant.PRODUCTION_TASK_ORDER))
                                    .and(productionBillId -> productionBillId.in("production_bill_id", hasSurplusProductionBillProductIdList)).or()
                                    .like("production_bill_remark", fuzzyText)
                                    .and(billType -> billType.eq("production_bill_type", ProductionBillConstant.PRODUCTION_TASK_ORDER))
                                    .and(productionBillId -> productionBillId.in("production_bill_id", hasSurplusProductionBillProductIdList)).or()
                                    .like("production_bill_currency_type", fuzzyText)
                                    .and(billType -> billType.eq("production_bill_type", ProductionBillConstant.PRODUCTION_TASK_ORDER))
                                    .and(productionBillId -> productionBillId.in("production_bill_id", hasSurplusProductionBillProductIdList)).or()
                                    .like("production_bill_exchange_rate", fuzzyText)
                                    .and(billType -> billType.eq("production_bill_type", ProductionBillConstant.PRODUCTION_TASK_ORDER))
                                    .and(productionBillId -> productionBillId.in("production_bill_id", hasSurplusProductionBillProductIdList));
                        }
                    } else {
                        ipmsProductionBillQueryWrapper.eq("production_bill_type", ProductionBillConstant.PRODUCTION_TASK_ORDER);
                        ipmsProductionBillQueryWrapper.in("production_bill_id", hasSurplusProductionBillProductIdList);
                    }
                } else {
                    return new PageDTO<>();
                }
                break;
            }
            case ProductionBillConstant.PRODUCTION_RETURN_ORDER: {
                productionBillQueryWrapper = new QueryWrapper<>();
                productionBillProductNumQueryWrapper = new QueryWrapper<>();
                productionBillQueryWrapper.eq("production_bill_type", ProductionBillConstant.PRODUCTION_PICKING_ORDER);
                List<IpmsProductionBill> productionBillList = ipmsProductionBillMapper.selectList(productionBillQueryWrapper);
                for (IpmsProductionBill productionBill : productionBillList) {
                    productionBillProductNumQueryWrapper.eq("production_bill_id", productionBill.getProductionBillId());
                    productionBillProductNumQueryWrapper.gt("surplus_need_execution_product_num", 0);
                    List<IpmsProductionBillProductNum> list = ipmsProductionBillProductNumService.list(productionBillProductNumQueryWrapper);
                    if (list != null && list.size() > 0) {
                        hasSurplusProductionBillProductIdList.add(productionBill.getProductionBillId());
                    }
                }
                if (hasSurplusProductionBillProductIdList.size() > 0) {
                    if (StringUtils.isNotBlank(fuzzyText)) {
                        if (StringUtils.isNotBlank(fuzzyText)) {
                            ipmsProductionBillQueryWrapper.like("production_bill_code", fuzzyText)
                                    .and(billType -> billType.eq("production_bill_type", ProductionBillConstant.PRODUCTION_PICKING_ORDER))
                                    .and(productionBillId -> productionBillId.in("production_bill_id", hasSurplusProductionBillProductIdList)).or()
                                    .like("production_bill_date", fuzzyText)
                                    .and(billType -> billType.eq("production_bill_type", ProductionBillConstant.PRODUCTION_PICKING_ORDER))
                                    .and(productionBillId -> productionBillId.in("production_bill_id", hasSurplusProductionBillProductIdList)).or()
                                    .like("production_bill_settlement_date", fuzzyText)
                                    .and(billType -> billType.eq("production_bill_type", ProductionBillConstant.PRODUCTION_PICKING_ORDER))
                                    .and(productionBillId -> productionBillId.in("production_bill_id", hasSurplusProductionBillProductIdList)).or()
                                    .like("production_bill_remark", fuzzyText)
                                    .and(billType -> billType.eq("production_bill_type", ProductionBillConstant.PRODUCTION_PICKING_ORDER))
                                    .and(productionBillId -> productionBillId.in("production_bill_id", hasSurplusProductionBillProductIdList)).or()
                                    .like("production_bill_currency_type", fuzzyText)
                                    .and(billType -> billType.eq("production_bill_type", ProductionBillConstant.PRODUCTION_PICKING_ORDER))
                                    .and(productionBillId -> productionBillId.in("production_bill_id", hasSurplusProductionBillProductIdList)).or()
                                    .like("production_bill_exchange_rate", fuzzyText)
                                    .and(billType -> billType.eq("production_bill_type", ProductionBillConstant.PRODUCTION_PICKING_ORDER))
                                    .and(productionBillId -> productionBillId.in("production_bill_id", hasSurplusProductionBillProductIdList));
                        }
                    } else {
                        ipmsProductionBillQueryWrapper.eq("production_bill_type", ProductionBillConstant.PRODUCTION_PICKING_ORDER);
                        ipmsProductionBillQueryWrapper.in("production_bill_id", hasSurplusProductionBillProductIdList);
                    }
                } else {
                    return new PageDTO<>();
                }
                break;
            }
        }
        Page<IpmsProductionBill> productionBillPage = ipmsProductionBillMapper.selectPage(page, ipmsProductionBillQueryWrapper);
        List<SafeProductionBillVO> safeProductionBillVOList = productionBillPage.getRecords().stream().map(ipmsProductionBill -> {
            SafeProductionBillVO safeProductionBillVO = new SafeProductionBillVO();
            BeanUtils.copyProperties(ipmsProductionBill, safeProductionBillVO);
            // 设置源单相关的数据
            Long productionSourceBillId = ipmsProductionBill.getProductionSourceBillId();
            if (productionSourceBillId != null && productionSourceBillId > 0) {
                IpmsProductionBill productionBill = ipmsProductionBillMapper.selectById(productionSourceBillId);
                if (productionBill == null) {
                    throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "找不到源单，系统业务错误");
                }
                safeProductionBillVO.setProductionSourceBillCode(productionBill.getProductionBillCode());
                safeProductionBillVO.setProductionSourceBillType(productionBill.getProductionBillType());
            }
            // 查询职员信息，并设置到返回封装类中
            Long employeeId = ipmsProductionBill.getEmployeeId();
            if (employeeId != null && employeeId > 0) {
                IpmsEmployee employee = ipmsEmployeeService.getById(employeeId);
                if (employee == null) {
                    throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "找不到职员信息，系统业务错误");
                }
                safeProductionBillVO.setEmployeeId(employeeId);
                safeProductionBillVO.setEmployeeName(employee.getEmployeeName());
            }
            // 查询部门信息，并设置到返回封装类中
            Long departmentId = ipmsProductionBill.getDepartmentId();
            if (departmentId != null && departmentId > 0) {
                IpmsDepartment department = ipmsDepartmentService.getById(departmentId);
                if (department == null) {
                    throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "找不到部门信息，系统业务错误");
                }
                safeProductionBillVO.setDepartmentId(departmentId);
                safeProductionBillVO.setDepartmentName(department.getDepartmentName());
            }
            // 查询仓管信息
            Long storekeeperId = ipmsProductionBill.getStorekeeperId();
            if (storekeeperId != null && storekeeperId > 0) {
                IpmsEmployee storekeeper = ipmsEmployeeService.getById(storekeeperId);
                if (storekeeper == null) {
                    throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "找不到仓管信息，系统业务错误");
                }
                safeProductionBillVO.setStorekeeperId(storekeeperId);
                safeProductionBillVO.setStorekeeperName(storekeeper.getEmployeeName());
            }
            // 查询生产任务单商品信息，注意不是子件物料信息，所以可能不存在
            Long fatherProductId = ipmsProductionBill.getProductId();
            if (fatherProductId != null && fatherProductId > 0) {
                IpmsProduct fatherProduct = ipmsProductService.getById(fatherProductId);
                if (fatherProduct == null) {
                    throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "找不到商品信息，系统业务错误");
                }
                Long unitId = fatherProduct.getUnitId();
                IpmsUnit unit = ipmsUnitService.getById(unitId);
                SafeProductVO safeProductVO = new SafeProductVO();
                safeProductVO.setUnitName(unit.getUnitName());
                BeanUtils.copyProperties(fatherProduct, safeProductVO);
                safeProductionBillVO.setSafeProductVO(safeProductVO);
            }
            // 查询商品对于的仓库信息
            Long fatherProductWarehouseId = ipmsProductionBill.getWarehouseId();
            if (fatherProductWarehouseId != null && fatherProductWarehouseId > 0) {
                IpmsWarehouse fatherProductWarehouse = ipmsWarehouseService.getById(fatherProductWarehouseId);
                if (fatherProductWarehouse == null) {
                    throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "找不到商品对于的仓库信息，系统业务错误");
                }
                safeProductionBillVO.setWarehouseId(fatherProductWarehouseId);
                safeProductionBillVO.setWarehouseName(fatherProductWarehouse.getWarehouseName());
            }
            // 查询商品对于的仓位信息
            Long fatherProductWarehousePositionId = ipmsProductionBill.getWarehousePositionId();
            if (fatherProductWarehousePositionId != null && fatherProductWarehousePositionId > 0) {
                IpmsWarehousePosition fatherProductWarehousePosition = ipmsWarehousePositionService.getById(fatherProductWarehousePositionId);
                if (fatherProductWarehousePosition == null) {
                    throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "找不到商品对于的仓库信息，系统业务错误");
                }
                safeProductionBillVO.setWarehouseId(fatherProductWarehousePositionId);
                safeProductionBillVO.setWarehouseName(fatherProductWarehousePosition.getWarehousePositionName());
            }
            // 查询生产单据的子件材料信息，并设置到返回封装类中
            Long productionBillId = ipmsProductionBill.getProductionBillId();
            if (productionBillId != null && productionBillId > 0) {
                QueryWrapper<IpmsProductionBillProductNum> queryWrapper = new QueryWrapper<>();
                queryWrapper.eq("production_bill_id", productionBillId);
                List<IpmsProductionBillProductNum> productionBillProductList = ipmsProductionBillProductNumService.list(queryWrapper);
                List<SafeProductionBillProductNumVO> safeProductionBillProductVOList = new ArrayList<>();
                if (productionBillProductList != null && productionBillProductList.size() > 0) {
                    for (IpmsProductionBillProductNum productionBillProduct : productionBillProductList) {
                        SafeProductionBillProductNumVO safeProductionBillProductVO = new SafeProductionBillProductNumVO();
                        BeanUtils.copyProperties(productionBillProduct, safeProductionBillProductVO);
                        // 在生产商品中查询商品等信息，并设置到返回封装类中
                        QueryWrapper<IpmsProductInventory> productInventoryQueryWrapper = new QueryWrapper<>();
                        Long productId = productionBillProduct.getProductId();
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
                            safeProductionBillProductVO.setSafeProductVO(safeProductVO);
                            productInventoryQueryWrapper.eq("product_id", productId);
                        }
                        Long warehouseId = productionBillProduct.getWarehouseId();
                        if (warehouseId != null && warehouseId > 0) {
                            IpmsWarehouse warehouse = ipmsWarehouseService.getById(warehouseId);
                            if (warehouse == null) {
                                throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "找不到仓库，系统业务逻辑错误");
                            }
                            safeProductionBillProductVO.setWarehouseId(warehouseId);
                            safeProductionBillProductVO.setWarehouseName(warehouse.getWarehouseName());
                            productInventoryQueryWrapper.eq("warehouse_id", warehouseId);
                        }
                        Long warehousePositionId = productionBillProduct.getWarehousePositionId();
                        if (warehousePositionId != null && warehousePositionId > 0) {
                            IpmsWarehousePosition warehousePosition = ipmsWarehousePositionService.getById(warehousePositionId);
                            if (warehousePosition != null) {
                                safeProductionBillProductVO.setWarehousePositionId(warehousePositionId);
                                safeProductionBillProductVO.setWarehousePositionName(warehousePosition.getWarehousePositionName());
                                productInventoryQueryWrapper.eq("warehouse_position_id", warehousePositionId);
                            }
                        }
                        // 查询库存
                        IpmsProductInventory productInventory = ipmsProductInventoryService.getOne(productInventoryQueryWrapper);
                        if (productInventory != null) {
                            safeProductionBillProductVO.setAvailableInventory(productInventory.getProductInventorySurplusNum());
                        }
                        safeProductionBillProductVOList.add(safeProductionBillProductVO);
                    }
                }
                safeProductionBillVO.setSafeProductionBillProductNumVOList(safeProductionBillProductVOList);
            }
            // 单据时间格式化
            safeProductionBillVO.setCreateTime(TimeFormatUtil.dateFormatting(ipmsProductionBill.getCreateTime()));
            safeProductionBillVO.setUpdateTime(TimeFormatUtil.dateFormatting(ipmsProductionBill.getUpdateTime()));
            safeProductionBillVO.setCheckTime(TimeFormatUtil.dateFormatting(ipmsProductionBill.getCheckTime()));
            return safeProductionBillVO;
        }).collect(Collectors.toList());
        // 关键步骤
        Page<SafeProductionBillVO> safeProductionBillVOPage = new PageDTO<>(productionBillPage.getCurrent(), productionBillPage.getSize(), productionBillPage.getTotal());
        safeProductionBillVOPage.setRecords(safeProductionBillVOList);
        return safeProductionBillVOPage;
    }

    @Override
    public Page<SafeProductionBillVO> pagingFuzzyQuery(ProductionBillQueryRequest productionBillQueryRequest) {
        String productionBillType = productionBillQueryRequest.getProductionBillType();
        if (productionBillType == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "单据类型不能为空");
        }
        List<String> billTypeList = Arrays.asList(ProductionBillConstant.PRODUCTION_TASK_ORDER, ProductionBillConstant.PRODUCTION_PICKING_ORDER,
                ProductionBillConstant.PRODUCTION_RETURN_ORDER, ProductionBillConstant.PRODUCTION_RECEIPT_ORDER, ProductionBillConstant.PRODUCTION_STOCK_RETURN_ORDER);
        int validTypeResult = ValidType.valid(billTypeList, productionBillType);
        if (validTypeResult == 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "单据类型不存在");
        }
        // 2. 进行分页查询，分情况查询，生产订单，生产出库单和生产退货单
        Page<IpmsProductionBill> page = new Page<>(productionBillQueryRequest.getCurrentPage(), productionBillQueryRequest.getPageSize());
        QueryWrapper<IpmsProductionBill> ipmsProductionBillQueryWrapper = new QueryWrapper<>();
        String fuzzyText = productionBillQueryRequest.getFuzzyText();
        if (StringUtils.isNotBlank(fuzzyText)) {
            if (StringUtils.isNotBlank(fuzzyText)) {
                ipmsProductionBillQueryWrapper.like("production_bill_code", fuzzyText)
                        .and(billType -> billType.eq("production_bill_type", productionBillType)).or()
                        .like("production_bill_date", fuzzyText)
                        .and(billType -> billType.eq("production_bill_type", productionBillType)).or()
                        .like("production_bill_settlement_date", fuzzyText)
                        .and(billType -> billType.eq("production_bill_type", productionBillType)).or()
                        .like("production_bill_remark", fuzzyText)
                        .and(billType -> billType.eq("production_bill_type", productionBillType)).or()
                        .like("production_bill_currency_type", fuzzyText)
                        .and(billType -> billType.eq("production_bill_type", productionBillType)).or()
                        .like("production_bill_exchange_rate", fuzzyText)
                        .and(billType -> billType.eq("production_bill_type", productionBillType));
            }
        } else {
            ipmsProductionBillQueryWrapper.eq("production_bill_type", productionBillType);
        }
        Page<IpmsProductionBill> productionBillPage = ipmsProductionBillMapper.selectPage(page, ipmsProductionBillQueryWrapper);
        List<SafeProductionBillVO> safeProductionBillVOList = productionBillPage.getRecords().stream().map(ipmsProductionBill -> {
            SafeProductionBillVO safeProductionBillVO = new SafeProductionBillVO();
            BeanUtils.copyProperties(ipmsProductionBill, safeProductionBillVO);
            // 设置源单相关的数据
            Long productionSourceBillId = ipmsProductionBill.getProductionSourceBillId();
            if (productionSourceBillId != null && productionSourceBillId > 0) {
                IpmsProductionBill productionBill = ipmsProductionBillMapper.selectById(productionSourceBillId);
                if (productionBill == null) {
                    throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "找不到源单，系统业务错误");
                }
                safeProductionBillVO.setProductionSourceBillCode(productionBill.getProductionBillCode());
                safeProductionBillVO.setProductionSourceBillType(productionBill.getProductionBillType());
            }
            // 查询职员信息，并设置到返回封装类中
            Long employeeId = ipmsProductionBill.getEmployeeId();
            if (employeeId != null && employeeId > 0) {
                IpmsEmployee employee = ipmsEmployeeService.getById(employeeId);
                if (employee == null) {
                    throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "找不到职员信息，系统业务错误");
                }
                safeProductionBillVO.setEmployeeId(employeeId);
                safeProductionBillVO.setEmployeeName(employee.getEmployeeName());
            }
            // 查询部门信息，并设置到返回封装类中
            Long departmentId = ipmsProductionBill.getDepartmentId();
            if (departmentId != null && departmentId > 0) {
                IpmsDepartment department = ipmsDepartmentService.getById(departmentId);
                if (department == null) {
                    throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "找不到部门信息，系统业务错误");
                }
                safeProductionBillVO.setDepartmentId(departmentId);
                safeProductionBillVO.setDepartmentName(department.getDepartmentName());
            }
            // 查询仓管信息
            Long storekeeperId = ipmsProductionBill.getStorekeeperId();
            if (storekeeperId != null && storekeeperId > 0) {
                IpmsEmployee storekeeper = ipmsEmployeeService.getById(storekeeperId);
                if (storekeeper == null) {
                    throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "找不到仓管信息，系统业务错误");
                }
                safeProductionBillVO.setStorekeeperId(storekeeperId);
                safeProductionBillVO.setStorekeeperName(storekeeper.getEmployeeName());
            }
            // 查询生产任务单商品信息，注意不是子件物料信息，所以可能不存在
            Long fatherProductId = ipmsProductionBill.getProductId();
            if (fatherProductId != null && fatherProductId > 0) {
                IpmsProduct fatherProduct = ipmsProductService.getById(fatherProductId);
                if (fatherProduct == null) {
                    throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "找不到商品信息，系统业务错误");
                }
                Long unitId = fatherProduct.getUnitId();
                IpmsUnit unit = ipmsUnitService.getById(unitId);
                SafeProductVO safeProductVO = new SafeProductVO();
                safeProductVO.setUnitName(unit.getUnitName());
                BeanUtils.copyProperties(fatherProduct, safeProductVO);
                safeProductionBillVO.setSafeProductVO(safeProductVO);
            }
            // 查询商品对于的仓库信息
            Long fatherProductWarehouseId = ipmsProductionBill.getWarehouseId();
            if (fatherProductWarehouseId != null && fatherProductWarehouseId > 0) {
                IpmsWarehouse fatherProductWarehouse = ipmsWarehouseService.getById(fatherProductWarehouseId);
                if (fatherProductWarehouse == null) {
                    throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "找不到商品对于的仓库信息，系统业务错误");
                }
                safeProductionBillVO.setWarehouseId(fatherProductWarehouseId);
                safeProductionBillVO.setWarehouseName(fatherProductWarehouse.getWarehouseName());
            }
            // 查询商品对于的仓位信息
            Long fatherProductWarehousePositionId = ipmsProductionBill.getWarehousePositionId();
            if (fatherProductWarehousePositionId != null && fatherProductWarehousePositionId > 0) {
                IpmsWarehousePosition fatherProductWarehousePosition = ipmsWarehousePositionService.getById(fatherProductWarehousePositionId);
                if (fatherProductWarehousePosition == null) {
                    throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "找不到商品对于的仓库信息，系统业务错误");
                }
                safeProductionBillVO.setWarehouseId(fatherProductWarehousePositionId);
                safeProductionBillVO.setWarehouseName(fatherProductWarehousePosition.getWarehousePositionName());
            }
            // 查询生产单据的子件材料信息，并设置到返回封装类中
            Long productionBillId = ipmsProductionBill.getProductionBillId();
            if (productionBillId != null && productionBillId > 0) {
                QueryWrapper<IpmsProductionBillProductNum> queryWrapper = new QueryWrapper<>();
                queryWrapper.eq("production_bill_id", productionBillId);
                List<IpmsProductionBillProductNum> productionBillProductList = ipmsProductionBillProductNumService.list(queryWrapper);
                List<SafeProductionBillProductNumVO> safeProductionBillProductVOList = new ArrayList<>();
                if (productionBillProductList != null && productionBillProductList.size() > 0) {
                    for (IpmsProductionBillProductNum productionBillProduct : productionBillProductList) {
                        SafeProductionBillProductNumVO safeProductionBillProductVO = new SafeProductionBillProductNumVO();
                        BeanUtils.copyProperties(productionBillProduct, safeProductionBillProductVO);
                        // 在生产商品中查询商品等信息，并设置到返回封装类中
                        QueryWrapper<IpmsProductInventory> productInventoryQueryWrapper = new QueryWrapper<>();
                        Long productId = productionBillProduct.getProductId();
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
                            safeProductionBillProductVO.setSafeProductVO(safeProductVO);
                            productInventoryQueryWrapper.eq("product_id", productId);
                        }
                        Long warehouseId = productionBillProduct.getWarehouseId();
                        if (warehouseId != null && warehouseId > 0) {
                            IpmsWarehouse warehouse = ipmsWarehouseService.getById(warehouseId);
                            if (warehouse == null) {
                                throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "找不到仓库，系统业务逻辑错误");
                            }
                            safeProductionBillProductVO.setWarehouseId(warehouseId);
                            safeProductionBillProductVO.setWarehouseName(warehouse.getWarehouseName());
                            productInventoryQueryWrapper.eq("warehouse_id", warehouseId);
                        }
                        Long warehousePositionId = productionBillProduct.getWarehousePositionId();
                        if (warehousePositionId != null && warehousePositionId > 0) {
                            IpmsWarehousePosition warehousePosition = ipmsWarehousePositionService.getById(warehousePositionId);
                            if (warehousePosition != null) {
                                safeProductionBillProductVO.setWarehousePositionId(warehousePositionId);
                                safeProductionBillProductVO.setWarehousePositionName(warehousePosition.getWarehousePositionName());
                                productInventoryQueryWrapper.eq("warehouse_position_id", warehousePositionId);
                            }
                        }
                        // 查询库存
                        IpmsProductInventory productInventory = ipmsProductInventoryService.getOne(productInventoryQueryWrapper);
                        if (productInventory != null) {
                            safeProductionBillProductVO.setAvailableInventory(productInventory.getProductInventorySurplusNum());
                        }
                        safeProductionBillProductVOList.add(safeProductionBillProductVO);
                    }
                }
                safeProductionBillVO.setSafeProductionBillProductNumVOList(safeProductionBillProductVOList);
            }
            // 单据时间格式化
            safeProductionBillVO.setCreateTime(TimeFormatUtil.dateFormatting(ipmsProductionBill.getCreateTime()));
            safeProductionBillVO.setUpdateTime(TimeFormatUtil.dateFormatting(ipmsProductionBill.getUpdateTime()));
            safeProductionBillVO.setCheckTime(TimeFormatUtil.dateFormatting(ipmsProductionBill.getCheckTime()));
            return safeProductionBillVO;
        }).collect(Collectors.toList());
        // 关键步骤
        Page<SafeProductionBillVO> safeProductionBillVOPage = new PageDTO<>(productionBillPage.getCurrent(), productionBillPage.getSize(), productionBillPage.getTotal());
        safeProductionBillVOPage.setRecords(safeProductionBillVOList);
        return safeProductionBillVOPage;
    }
}



