package com.clarity.ipmsbackend.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.plugins.pagination.PageDTO;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.clarity.ipmsbackend.common.Constant;
import com.clarity.ipmsbackend.common.ErrorCode;
import com.clarity.ipmsbackend.common.FuzzyQueryRequest;
import com.clarity.ipmsbackend.constant.InventoryBillConstant;
import com.clarity.ipmsbackend.exception.BusinessException;
import com.clarity.ipmsbackend.mapper.IpmsInventoryBillMapper;
import com.clarity.ipmsbackend.model.dto.inventorybill.otherdeliveryorder.AddOtherDeliveryOrderRequest;
import com.clarity.ipmsbackend.model.dto.inventorybill.otherdeliveryorder.UpdateOtherDeliveryOrderRequest;
import com.clarity.ipmsbackend.model.dto.inventorybill.otherdeliveryorder.productnum.AddOtherDeliveryOrderProductNumRequest;
import com.clarity.ipmsbackend.model.dto.inventorybill.otherdeliveryorder.productnum.UpdateOtherDeliveryOrderProductNumRequest;
import com.clarity.ipmsbackend.model.dto.inventorybill.otherreceiptorder.AddOtherReceiptOrderRequest;
import com.clarity.ipmsbackend.model.dto.inventorybill.otherreceiptorder.UpdateOtherReceiptOrderRequest;
import com.clarity.ipmsbackend.model.dto.inventorybill.otherreceiptorder.productnum.AddOtherReceiptOrderProductNumRequest;
import com.clarity.ipmsbackend.model.dto.inventorybill.otherreceiptorder.productnum.UpdateOtherReceiptOrderProductNumRequest;
import com.clarity.ipmsbackend.model.dto.inventorybill.warehousetransferorder.AddWarehouseTransferOrderRequest;
import com.clarity.ipmsbackend.model.dto.inventorybill.warehousetransferorder.UpdateWarehouseTransferOrderRequest;
import com.clarity.ipmsbackend.model.dto.inventorybill.warehousetransferorder.productnum.AddWarehouseTransferOrderProductNumRequest;
import com.clarity.ipmsbackend.model.dto.inventorybill.warehousetransferorder.productnum.UpdateWarehouseTransferOrderProductNumRequest;
import com.clarity.ipmsbackend.model.entity.*;
import com.clarity.ipmsbackend.model.vo.SafeProductVO;
import com.clarity.ipmsbackend.model.vo.SafeUserVO;
import com.clarity.ipmsbackend.model.vo.inventorybill.otherdeliveryorder.SafeOtherDeliveryOrderVO;
import com.clarity.ipmsbackend.model.vo.inventorybill.otherdeliveryorder.productnum.SafeOtherDeliveryOrderProductNumVO;
import com.clarity.ipmsbackend.model.vo.inventorybill.otherreceiptorder.SafeOtherReceiptOrderVO;
import com.clarity.ipmsbackend.model.vo.inventorybill.otherreceiptorder.productnum.SafeOtherReceiptOrderProductNumVO;
import com.clarity.ipmsbackend.model.vo.inventorybill.warehousetransferorder.SafeWarehouseTransferOrderVO;
import com.clarity.ipmsbackend.model.vo.inventorybill.warehousetransferorder.productnum.SafeWarehouseTransferOrderProductNumVO;
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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
* @author Clarity
* @description 针对表【ipms_inventory_bill(库存单据)】的数据库操作Service实现
* @createDate 2023-03-27 22:36:41
*/
@Service
@Slf4j
public class IpmsInventoryBillServiceImpl extends ServiceImpl<IpmsInventoryBillMapper, IpmsInventoryBill>
    implements IpmsInventoryBillService{

    @Resource
    private IpmsInventoryBillMapper ipmsInventoryBillMapper;

    @Resource
    private IpmsCustomerService ipmsCustomerService;

    @Resource
    private IpmsSupplierService ipmsSupplierService;

    @Resource
    private IpmsEmployeeService ipmsEmployeeService;

    @Resource
    private IpmsDepartmentService ipmsDepartmentService;

    @Resource
    private IpmsUserService ipmsUserService;

    @Resource
    private IpmsCustomerLinkmanService ipmsCustomerLinkmanService;

    @Resource
    private IpmsSupplierLinkmanService ipmsSupplierLinkmanService;

    @Resource
    private IpmsInventoryBillProductNumService ipmsInventoryBillProductNumService;

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
    public String inventoryBillCodeAutoGenerate(String inventoryBillType) {
        if (inventoryBillType == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "单据类型为空，无法生成对于的单据编码");
        }
        QueryWrapper<IpmsInventoryBill> ipmsInventoryBillQueryWrapper = new QueryWrapper<>();
        ipmsInventoryBillQueryWrapper.eq("inventory_bill_type", inventoryBillType);
        List<IpmsInventoryBill> ipmsInventoryBillList;
        String inventoryBillCode;
        String inventoryBillCodePrefix;
        String inventoryBillCodeInfix;
        String inventoryBillCodeSuffix;
        switch (inventoryBillType) {
            case InventoryBillConstant.OTHER_RECEIPT_ORDER:
                ipmsInventoryBillList = ipmsInventoryBillMapper.selectList(ipmsInventoryBillQueryWrapper);
                if (ipmsInventoryBillList.size() == 0) {
                    inventoryBillCodePrefix = "QTRK";
                    inventoryBillCodeInfix = TimeFormatUtil.dateFormat(new Date());
                    inventoryBillCodeSuffix = "0";
                } else {
                    IpmsInventoryBill lastInventoryBill = ipmsInventoryBillList.get(ipmsInventoryBillList.size() - 1);
                    inventoryBillCode = lastInventoryBill.getInventoryBillCode();
                    String[] inventoryOrderCode = SplitUtil.codeSplitByMinusSign(inventoryBillCode);
                    inventoryBillCodePrefix = inventoryOrderCode[0];
                    inventoryBillCodeInfix = inventoryOrderCode[1];
                    inventoryBillCodeSuffix = inventoryOrderCode[2];
                }
                break;
            case InventoryBillConstant.OTHER_DELIVERY_ORDER:
                ipmsInventoryBillList = ipmsInventoryBillMapper.selectList(ipmsInventoryBillQueryWrapper);
                if (ipmsInventoryBillList.size() == 0) {
                    inventoryBillCodePrefix = "QTCK";
                    inventoryBillCodeInfix = TimeFormatUtil.dateFormat(new Date());
                    inventoryBillCodeSuffix = "0";
                } else {
                    IpmsInventoryBill lastInventoryBill = ipmsInventoryBillList.get(ipmsInventoryBillList.size() - 1);
                    inventoryBillCode = lastInventoryBill.getInventoryBillCode();
                    String[] inventoryOrderCode = SplitUtil.codeSplitByMinusSign(inventoryBillCode);
                    inventoryBillCodePrefix = inventoryOrderCode[0];
                    inventoryBillCodeInfix = inventoryOrderCode[1];
                    inventoryBillCodeSuffix = inventoryOrderCode[2];
                }
                break;
            case InventoryBillConstant.WAREHOUSE_TRANSFER_ORDER:
                ipmsInventoryBillList = ipmsInventoryBillMapper.selectList(ipmsInventoryBillQueryWrapper);
                if (ipmsInventoryBillList.size() == 0) {
                    inventoryBillCodePrefix = "YCD";
                    inventoryBillCodeInfix = TimeFormatUtil.dateFormat(new Date());
                    inventoryBillCodeSuffix = "0";
                } else {
                    IpmsInventoryBill lastInventoryBill = ipmsInventoryBillList.get(ipmsInventoryBillList.size() - 1);
                    inventoryBillCode = lastInventoryBill.getInventoryBillCode();
                    String[] inventoryOrderCode = SplitUtil.codeSplitByMinusSign(inventoryBillCode);
                    inventoryBillCodePrefix = inventoryOrderCode[0];
                    inventoryBillCodeInfix = inventoryOrderCode[1];
                    inventoryBillCodeSuffix = inventoryOrderCode[2];
                }
                break;
            case InventoryBillConstant.TRANSFER_ISSUE_ORDER:
                ipmsInventoryBillList = ipmsInventoryBillMapper.selectList(ipmsInventoryBillQueryWrapper);
                if (ipmsInventoryBillList.size() == 0) {
                    inventoryBillCodePrefix = "DBCK";
                    inventoryBillCodeInfix = TimeFormatUtil.dateFormat(new Date());
                    inventoryBillCodeSuffix = "0";
                } else {
                    IpmsInventoryBill lastInventoryBill = ipmsInventoryBillList.get(ipmsInventoryBillList.size() - 1);
                    inventoryBillCode = lastInventoryBill.getInventoryBillCode();
                    String[] inventoryOrderCode = SplitUtil.codeSplitByMinusSign(inventoryBillCode);
                    inventoryBillCodePrefix = inventoryOrderCode[0];
                    inventoryBillCodeInfix = inventoryOrderCode[1];
                    inventoryBillCodeSuffix = inventoryOrderCode[2];
                }
                break;
            case InventoryBillConstant.TRANSFER_RECEIPT_ORDER:
                ipmsInventoryBillList = ipmsInventoryBillMapper.selectList(ipmsInventoryBillQueryWrapper);
                if (ipmsInventoryBillList.size() == 0) {
                    inventoryBillCodePrefix = "DBRK";
                    inventoryBillCodeInfix = TimeFormatUtil.dateFormat(new Date());
                    inventoryBillCodeSuffix = "0";
                } else {
                    IpmsInventoryBill lastInventoryBill = ipmsInventoryBillList.get(ipmsInventoryBillList.size() - 1);
                    inventoryBillCode = lastInventoryBill.getInventoryBillCode();
                    String[] inventoryOrderCode = SplitUtil.codeSplitByMinusSign(inventoryBillCode);
                    inventoryBillCodePrefix = inventoryOrderCode[0];
                    inventoryBillCodeInfix = inventoryOrderCode[1];
                    inventoryBillCodeSuffix = inventoryOrderCode[2];
                }
                break;
            default:
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "单据类型不存在");
        }
        String nextInventoryBillCode = null;
        try {
            String todayDateFormat = TimeFormatUtil.dateFormat(new Date());
            if (!inventoryBillCodeInfix.equals(todayDateFormat)) {
                nextInventoryBillCode = CodeAutoGenerator.generatorCode(inventoryBillCodePrefix, todayDateFormat, "0");
            } else {
                nextInventoryBillCode = CodeAutoGenerator.generatorCode(inventoryBillCodePrefix, inventoryBillCodeInfix, inventoryBillCodeSuffix);
            }
        } catch (Exception e) {
            log.info("编码自动生成器异常");
        }
        return nextInventoryBillCode;
    }

    @Override
    @Transactional
    public int addOtherReceiptOrder(AddOtherReceiptOrderRequest addOtherReceiptOrderRequest, HttpServletRequest request) {
        String inventoryBillType = addOtherReceiptOrderRequest.getInventoryBillType();
        this.validInventoryBillType(inventoryBillType);
        if (!inventoryBillType.equals(InventoryBillConstant.OTHER_RECEIPT_ORDER)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "该接口只能操作其他入库单");
        }
        String inventoryBillDate = addOtherReceiptOrderRequest.getInventoryBillDate();
        if (inventoryBillDate == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, inventoryBillType + "单据日期为空");
        }
        String inventoryBillBusinessType = addOtherReceiptOrderRequest.getInventoryBillBusinessType();
        if (inventoryBillBusinessType == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, inventoryBillType + "业务类型为空");
        }
        List<AddOtherReceiptOrderProductNumRequest> addOtherReceiptOrderProductNumRequestList = addOtherReceiptOrderRequest.getAddOtherReceiptOrderProductNumRequestList();
        if (addOtherReceiptOrderProductNumRequestList == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, inventoryBillType + "商品为空");
        }
        if (addOtherReceiptOrderProductNumRequestList.size() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, inventoryBillType + "至少存在一个商品");
        }
        String inventoryBillCode = addOtherReceiptOrderRequest.getInventoryBillCode();
        QueryWrapper<IpmsInventoryBill> ipmsInventoryBillQueryWrapper = new QueryWrapper<>();
        ipmsInventoryBillQueryWrapper.eq("inventory_bill_code", inventoryBillCode);
        IpmsInventoryBill ipmsInventoryBill = ipmsInventoryBillMapper.selectOne(ipmsInventoryBillQueryWrapper);
        if (ipmsInventoryBill != null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, inventoryBillType + "编号重复");
        }
        Long customerId = addOtherReceiptOrderRequest.getCustomerId();
        Long customerLinkmanId = addOtherReceiptOrderRequest.getCustomerLinkmanId();
        Long supplierId = addOtherReceiptOrderRequest.getSupplierId();
        Long supplierLinkmanId = addOtherReceiptOrderRequest.getSupplierLinkmanId();
        Long employeeId = addOtherReceiptOrderRequest.getEmployeeId();
        Long departmentId = addOtherReceiptOrderRequest.getDepartmentId();
        if (customerId != null && customerId > 0) {
            IpmsCustomer customer = ipmsCustomerService.getById(customerId);
            if (customer == null) {
                throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, inventoryBillType + "的客户不存在");
            }
        }
        if (customerLinkmanId != null && customerLinkmanId > 0) {
            IpmsCustomerLinkman customerLinkman = ipmsCustomerLinkmanService.getById(customerLinkmanId);
            if (customerLinkman == null) {
                throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, inventoryBillType + "的客户联系人不存在");
            }
            if (!customerLinkman.getCustomerId().equals(customerId)) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR, inventoryBillType + "的客户和联系人信息不对应");
            }
        }
        if (supplierId != null && supplierId > 0) {
            IpmsSupplier supplier = ipmsSupplierService.getById(supplierId);
            if (supplier == null) {
                throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, inventoryBillType + "的供应商不存在");
            }
        }
        if (supplierLinkmanId != null && supplierLinkmanId > 0) {
            IpmsSupplierLinkman supplierLinkman = ipmsSupplierLinkmanService.getById(supplierLinkmanId);
            if (supplierLinkman == null) {
                throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, inventoryBillType + "的供应商联系人不存在");
            }
            if (!supplierLinkman.getSupplierId().equals(supplierId)) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR, inventoryBillType + "的供应商和联系人信息不对应");
            }
        }
        if (employeeId != null && employeeId > 0) {
            IpmsEmployee employee = ipmsEmployeeService.getById(employeeId);
            if (employee == null) {
                throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, inventoryBillType + "的职员不存在");
            }
        }
        if (departmentId != null && departmentId > 0) {
            IpmsDepartment department = ipmsDepartmentService.getById(departmentId);
            if (department == null) {
                throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, inventoryBillType + "的部门不存在");
            }
        }
        IpmsInventoryBill inventoryBill = new IpmsInventoryBill();
        BeanUtils.copyProperties(addOtherReceiptOrderRequest, inventoryBill);
        SafeUserVO loginUser = ipmsUserService.getLoginUser(request);
        inventoryBill.setFounder(loginUser.getUserName());
        inventoryBill.setCreateTime(new Date());
        inventoryBill.setUpdateTime(new Date());
        int result = ipmsInventoryBillMapper.insert(inventoryBill);
        if (result != 1) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, inventoryBillType + "插入数据失败");
        }
        for (AddOtherReceiptOrderProductNumRequest addOtherReceiptOrderProductNumRequest : addOtherReceiptOrderProductNumRequestList) {
            long addProductResult = ipmsInventoryBillProductNumService.addOtherReceiptOrderProductAndNum(addOtherReceiptOrderProductNumRequest, inventoryBill);
            if (addProductResult < 0) {
                throw new BusinessException(ErrorCode.SYSTEM_ERROR, inventoryBillType + "商品插入失败");
            }
        }
        return result;
    }

    @Override
    @Transactional
    public int addOtherDeliveryOrder(AddOtherDeliveryOrderRequest addOtherDeliveryOrderRequest, HttpServletRequest request) {
        String inventoryBillType = addOtherDeliveryOrderRequest.getInventoryBillType();
        this.validInventoryBillType(inventoryBillType);
        if (!inventoryBillType.equals(InventoryBillConstant.OTHER_DELIVERY_ORDER)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "该接口只能操作其他出库单");
        }
        String inventoryBillDate = addOtherDeliveryOrderRequest.getInventoryBillDate();
        if (inventoryBillDate == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, inventoryBillType + "单据日期为空");
        }
        String inventoryBillBusinessType = addOtherDeliveryOrderRequest.getInventoryBillBusinessType();
        if (inventoryBillBusinessType == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, inventoryBillType + "业务类型为空");
        }
        List<AddOtherDeliveryOrderProductNumRequest> addOtherDeliveryOrderProductNumRequestList = addOtherDeliveryOrderRequest.getAddOtherDeliveryOrderProductNumRequestList();
        if (addOtherDeliveryOrderProductNumRequestList == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, inventoryBillType + "商品为空");
        }
        if (addOtherDeliveryOrderProductNumRequestList.size() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, inventoryBillType + "至少存在一个商品");
        }
        String inventoryBillCode = addOtherDeliveryOrderRequest.getInventoryBillCode();
        QueryWrapper<IpmsInventoryBill> ipmsInventoryBillQueryWrapper = new QueryWrapper<>();
        ipmsInventoryBillQueryWrapper.eq("inventory_bill_code", inventoryBillCode);
        IpmsInventoryBill ipmsInventoryBill = ipmsInventoryBillMapper.selectOne(ipmsInventoryBillQueryWrapper);
        if (ipmsInventoryBill != null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, inventoryBillType + "编号重复");
        }
        Long customerId = addOtherDeliveryOrderRequest.getCustomerId();
        Long customerLinkmanId = addOtherDeliveryOrderRequest.getCustomerLinkmanId();
        Long employeeId = addOtherDeliveryOrderRequest.getEmployeeId();
        Long departmentId = addOtherDeliveryOrderRequest.getDepartmentId();
        if (customerId != null && customerId > 0) {
            IpmsCustomer customer = ipmsCustomerService.getById(customerId);
            if (customer == null) {
                throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, inventoryBillType + "的客户不存在");
            }
        }
        if (customerLinkmanId != null && customerLinkmanId > 0) {
            IpmsCustomerLinkman customerLinkman = ipmsCustomerLinkmanService.getById(customerLinkmanId);
            if (customerLinkman == null) {
                throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, inventoryBillType + "的客户联系人不存在");
            }
            if (!customerLinkman.getCustomerId().equals(customerId)) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR, inventoryBillType + "的客户和联系人信息不对应");
            }
        }
        if (employeeId != null && employeeId > 0) {
            IpmsEmployee employee = ipmsEmployeeService.getById(employeeId);
            if (employee == null) {
                throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, inventoryBillType + "的职员不存在");
            }
        }
        if (departmentId != null && departmentId > 0) {
            IpmsDepartment department = ipmsDepartmentService.getById(departmentId);
            if (department == null) {
                throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, inventoryBillType + "的部门不存在");
            }
        }
        IpmsInventoryBill inventoryBill = new IpmsInventoryBill();
        BeanUtils.copyProperties(addOtherDeliveryOrderRequest, inventoryBill);
        SafeUserVO loginUser = ipmsUserService.getLoginUser(request);
        inventoryBill.setFounder(loginUser.getUserName());
        inventoryBill.setCreateTime(new Date());
        inventoryBill.setUpdateTime(new Date());
        int result = ipmsInventoryBillMapper.insert(inventoryBill);
        if (result != 1) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, inventoryBillType + "插入数据失败");
        }
        for (AddOtherDeliveryOrderProductNumRequest addOtherDeliveryOrderProductNumRequest : addOtherDeliveryOrderProductNumRequestList) {
            long addProductResult = ipmsInventoryBillProductNumService.addOtherDeliveryOrderProductAndNum(addOtherDeliveryOrderProductNumRequest, inventoryBill);
            if (addProductResult < 0) {
                throw new BusinessException(ErrorCode.SYSTEM_ERROR, inventoryBillType + "商品插入失败");
            }
        }
        return result;
    }

    @Override
    public int addWarehouseTransferOrder(AddWarehouseTransferOrderRequest addWarehouseTransferOrderRequest, HttpServletRequest request) {
        String inventoryBillType = addWarehouseTransferOrderRequest.getInventoryBillType();
        this.validInventoryBillType(inventoryBillType);
        if (!inventoryBillType.equals(InventoryBillConstant.WAREHOUSE_TRANSFER_ORDER)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "该接口只能操作移仓单");
        }
        String inventoryBillDate = addWarehouseTransferOrderRequest.getInventoryBillDate();
        if (inventoryBillDate == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, inventoryBillType + "单据日期为空");
        }
        List<AddWarehouseTransferOrderProductNumRequest> addWarehouseTransferOrderProductNumRequestList = addWarehouseTransferOrderRequest.getAddWarehouseTransferOrderProductNumRequestList();
        if (addWarehouseTransferOrderProductNumRequestList == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, inventoryBillType + "商品为空");
        }
        if (addWarehouseTransferOrderProductNumRequestList.size() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, inventoryBillType + "至少存在一个商品");
        }
        String inventoryBillCode = addWarehouseTransferOrderRequest.getInventoryBillCode();
        QueryWrapper<IpmsInventoryBill> ipmsInventoryBillQueryWrapper = new QueryWrapper<>();
        ipmsInventoryBillQueryWrapper.eq("inventory_bill_code", inventoryBillCode);
        IpmsInventoryBill ipmsInventoryBill = ipmsInventoryBillMapper.selectOne(ipmsInventoryBillQueryWrapper);
        if (ipmsInventoryBill != null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, inventoryBillType + "编号重复");
        }
        Long employeeId = addWarehouseTransferOrderRequest.getEmployeeId();
        Long departmentId = addWarehouseTransferOrderRequest.getDepartmentId();
        Long transferDepartmentId = addWarehouseTransferOrderRequest.getTransferDepartmentId();
        if (employeeId != null && employeeId > 0) {
            IpmsEmployee employee = ipmsEmployeeService.getById(employeeId);
            if (employee == null) {
                throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, inventoryBillType + "的经办人不存在");
            }
        }
        if (departmentId != null && departmentId > 0) {
            IpmsDepartment department = ipmsDepartmentService.getById(departmentId);
            if (department == null) {
                throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, inventoryBillType + "的调出部门不存在");
            }
        }
        if (transferDepartmentId != null && transferDepartmentId > 0) {
            IpmsDepartment transferDepartment = ipmsDepartmentService.getById(transferDepartmentId);
            if (transferDepartment == null) {
                throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, inventoryBillType + "的调入部门不存在");
            }
        }
        IpmsInventoryBill inventoryBill = new IpmsInventoryBill();
        BeanUtils.copyProperties(addWarehouseTransferOrderRequest, inventoryBill);
        SafeUserVO loginUser = ipmsUserService.getLoginUser(request);
        inventoryBill.setFounder(loginUser.getUserName());
        inventoryBill.setCreateTime(new Date());
        inventoryBill.setUpdateTime(new Date());
        int result = ipmsInventoryBillMapper.insert(inventoryBill);
        if (result != 1) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, inventoryBillType + "插入数据失败");
        }
        for (AddWarehouseTransferOrderProductNumRequest addWarehouseTransferOrderProductNumRequest : addWarehouseTransferOrderProductNumRequestList) {
            long addProductResult = ipmsInventoryBillProductNumService.addWarehouseTransferOrderProductAndNum(addWarehouseTransferOrderProductNumRequest, inventoryBill);
            if (addProductResult < 0) {
                throw new BusinessException(ErrorCode.SYSTEM_ERROR, inventoryBillType + "商品插入失败");
            }
        }
        return result;
    }

    @Override
    @Transactional
    public int checkInventoryBill(long inventoryBillId, HttpServletRequest request) {
        if (inventoryBillId <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "单据 id 不合法");
        }
        IpmsInventoryBill inventoryBill = ipmsInventoryBillMapper.selectById(inventoryBillId);
        if (inventoryBill == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }
        Integer checkState = inventoryBill.getCheckState();
        if (Constant.CHECKED == checkState) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "单据已审核");
        }
        String inventoryBillType = inventoryBill.getInventoryBillType();
        IpmsInventoryBill checkingInventoryBill = new IpmsInventoryBill();
        checkingInventoryBill.setInventoryBillId(inventoryBill.getInventoryBillId());
        checkingInventoryBill.setCheckState(Constant.CHECKED);
        QueryWrapper<IpmsInventoryBillProductNum> inventoryBillProductNumQueryWrapper;
        if (InventoryBillConstant.OTHER_RECEIPT_ORDER.equals(inventoryBillType)) {
            // 其他入库单审核后要调用增加库存的方法
            inventoryBillProductNumQueryWrapper = new QueryWrapper<>();
            inventoryBillProductNumQueryWrapper.eq("inventory_bill_id", inventoryBillId);
            List<IpmsInventoryBillProductNum> ipmsInventoryBillProductNumList = ipmsInventoryBillProductNumService.list(inventoryBillProductNumQueryWrapper);
            for (IpmsInventoryBillProductNum inventoryBillProductNum : ipmsInventoryBillProductNumList) {
                int result = ipmsProductInventoryService.addProductInventory(inventoryBillProductNum);
                if (result != 1) {
                    throw new BusinessException(ErrorCode.SYSTEM_ERROR, "调用增加库存的方法失败");
                }
            }
        } else if (InventoryBillConstant.OTHER_DELIVERY_ORDER.equals(inventoryBillType)) {
            // 其他出库单审核后要调用减少库存的方法
            inventoryBillProductNumQueryWrapper = new QueryWrapper<>();
            inventoryBillProductNumQueryWrapper.eq("inventory_bill_id", inventoryBillId);
            List<IpmsInventoryBillProductNum> ipmsInventoryBillProductNumList = ipmsInventoryBillProductNumService.list(inventoryBillProductNumQueryWrapper);
            for (IpmsInventoryBillProductNum inventoryBillProductNum : ipmsInventoryBillProductNumList) {
                int result = ipmsProductInventoryService.reduceProductInventory(inventoryBillProductNum);
                if (result != 1) {
                    throw new BusinessException(ErrorCode.SYSTEM_ERROR, "调用减少库存的方法失败");
                }
            }
        } else if (InventoryBillConstant.WAREHOUSE_TRANSFER_ORDER.equals(inventoryBillType)) {
            // 移仓单审核扣减调出仓库的商品库存，增加调入仓库的商品库存
            inventoryBillProductNumQueryWrapper = new QueryWrapper<>();
            inventoryBillProductNumQueryWrapper.eq("inventory_bill_id", inventoryBillId);
            List<IpmsInventoryBillProductNum> ipmsInventoryBillProductNumList = ipmsInventoryBillProductNumService.list(inventoryBillProductNumQueryWrapper);
            for (IpmsInventoryBillProductNum inventoryBillProductNum : ipmsInventoryBillProductNumList) {
                int transferOutResult = ipmsProductInventoryService.reduceProductInventoryByTransfer(inventoryBillProductNum, Constant.CHECK_OPERATION);
                int transferIntoResult = ipmsProductInventoryService.addProductInventoryByTransfer(inventoryBillProductNum, Constant.CHECK_OPERATION);
                if (transferIntoResult != 1 || transferOutResult != 1) {
                    throw new BusinessException(ErrorCode.SYSTEM_ERROR, "移仓单审核失败");
                }
            }
        }
        SafeUserVO loginUser = ipmsUserService.getLoginUser(request);
        checkingInventoryBill.setChecker(loginUser.getUserName());
        checkingInventoryBill.setCheckTime(new Date());
        int result = ipmsInventoryBillMapper.updateById(checkingInventoryBill);
        if (result != 1) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR);
        }
        return result;
    }

    @Override
    @Transactional
    public int reverseCheckInventoryBill(long inventoryBillId, HttpServletRequest request) {
        if (inventoryBillId <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "单据 id 不合法");
        }
        IpmsInventoryBill inventoryBill = ipmsInventoryBillMapper.selectById(inventoryBillId);
        if (inventoryBill == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }
        if (Constant.UNCHECKED == inventoryBill.getCheckState()) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "单据未审核");
        }
        String inventoryBillType = inventoryBill.getInventoryBillType();
        IpmsInventoryBill unCheckingInventoryBill = new IpmsInventoryBill();
        unCheckingInventoryBill.setInventoryBillId(inventoryBill.getInventoryBillId());
        unCheckingInventoryBill.setCheckState(Constant.UNCHECKED);
        unCheckingInventoryBill.setChecker(null);
        unCheckingInventoryBill.setCheckTime(null);
        // 其他入库单
        QueryWrapper<IpmsInventoryBill> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("inventory_source_bill_id", inventoryBillId);
        List<IpmsInventoryBill> isAsSourceBill = ipmsInventoryBillMapper.selectList(queryWrapper);
        if (isAsSourceBill != null && isAsSourceBill.size() > 0) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "单据已经作为其他单据的单源使用");
        }
        if (InventoryBillConstant.OTHER_RECEIPT_ORDER.equals(inventoryBillType)) {
            // 调用减少库存的方法
            QueryWrapper<IpmsInventoryBillProductNum> inventoryBillProductNumQueryWrapper = new QueryWrapper<>();
            inventoryBillProductNumQueryWrapper.eq("inventory_bill_id", inventoryBillId);
            List<IpmsInventoryBillProductNum> ipmsInventoryBillProductNumList = ipmsInventoryBillProductNumService.list(inventoryBillProductNumQueryWrapper);
            for (IpmsInventoryBillProductNum inventoryBillProductNum : ipmsInventoryBillProductNumList) {
                int result = ipmsProductInventoryService.reduceProductInventory(inventoryBillProductNum);
                if (result != 1) {
                    throw new BusinessException(ErrorCode.SYSTEM_ERROR, "调用减少库存的方法失败");
                }
            }
        } else if (InventoryBillConstant.OTHER_DELIVERY_ORDER.equals(inventoryBillType)) {
            // 调用增加库存的方法
            QueryWrapper<IpmsInventoryBillProductNum> inventoryBillProductNumQueryWrapper = new QueryWrapper<>();
            inventoryBillProductNumQueryWrapper.eq("inventory_bill_id", inventoryBillId);
            List<IpmsInventoryBillProductNum> ipmsInventoryBillProductNumList = ipmsInventoryBillProductNumService.list(inventoryBillProductNumQueryWrapper);
            for (IpmsInventoryBillProductNum inventoryBillProductNum : ipmsInventoryBillProductNumList) {
                int result = ipmsProductInventoryService.addProductInventory(inventoryBillProductNum);
                if (result != 1) {
                    throw new BusinessException(ErrorCode.SYSTEM_ERROR, "调用增加库存的方法失败");
                }
            }
        } else if (InventoryBillConstant.WAREHOUSE_TRANSFER_ORDER.equals(inventoryBillType)) {
            // 与审核互逆
            QueryWrapper<IpmsInventoryBillProductNum> inventoryBillProductNumQueryWrapper = new QueryWrapper<>();
            inventoryBillProductNumQueryWrapper.eq("inventory_bill_id", inventoryBillId);
            List<IpmsInventoryBillProductNum> ipmsInventoryBillProductNumList = ipmsInventoryBillProductNumService.list(inventoryBillProductNumQueryWrapper);
            for (IpmsInventoryBillProductNum inventoryBillProductNum : ipmsInventoryBillProductNumList) {
                int transferOutResult = ipmsProductInventoryService.reduceProductInventoryByTransfer(inventoryBillProductNum, Constant.UNCHECKED_OPERATION);
                int transferIntoResult = ipmsProductInventoryService.addProductInventoryByTransfer(inventoryBillProductNum, Constant.UNCHECKED_OPERATION);
                if (transferIntoResult != 1 || transferOutResult != 1) {
                    throw new BusinessException(ErrorCode.SYSTEM_ERROR, "移仓单反审核失败");
                }
            }
        }
        int result = ipmsInventoryBillMapper.updateById(unCheckingInventoryBill);
        if (result != 1) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR);
        }
        return result;
    }

    @Override
    @Transactional
    public int deleteInventoryBillById(long id) {
        // 1. 判断 id 是否合法
        if (id <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "id 不合法");
        }
        // 2. 审核后的单据无法删除
        IpmsInventoryBill oldInventoryBill = ipmsInventoryBillMapper.selectById(id);
        if (oldInventoryBill == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }
        if (oldInventoryBill.getCheckState().equals(Constant.CHECKED)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "单据已经审核无法删除");
        }
        // 3. 按照单据类型分类别删除
        // String inventoryBillType = oldInventoryBill.getInventoryBillType();
        // Long inventorySourceBillId = oldInventoryBill.getInventorySourceBillId();
        QueryWrapper<IpmsInventoryBillProductNum> inventoryBillProductNumQueryWrapper;
        // 其他入库单删除无特殊操作
        // 其他出库单删除无特殊操作
        // 移仓单删除无特殊操作
        int result = ipmsInventoryBillMapper.deleteById(id);
        if (result != 1) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "删除失败");
        }
        inventoryBillProductNumQueryWrapper = new QueryWrapper<>();
        inventoryBillProductNumQueryWrapper.eq("inventory_bill_id", id);
        boolean remove = ipmsInventoryBillProductNumService.remove(inventoryBillProductNumQueryWrapper);
        if (!remove) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "关联删除失败");
        }
        return result;
    }

    @Override
    @Transactional
    public int updateOtherReceiptOrder(UpdateOtherReceiptOrderRequest updateOtherReceiptOrderRequest, HttpServletRequest request) {
        Long inventoryBillId = updateOtherReceiptOrderRequest.getInventoryBillId();
        if (inventoryBillId == null || inventoryBillId <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "其他入库单 id 为空或者不合法");
        }
        IpmsInventoryBill oldInventoryBill = ipmsInventoryBillMapper.selectById(inventoryBillId);
        if (oldInventoryBill == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "其他入库单不存在");
        }
        Integer checkState = oldInventoryBill.getCheckState();
        String inventoryBillType = oldInventoryBill.getInventoryBillType();
        if (Constant.CHECKED == checkState) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, inventoryBillType + "单据已审核，无法修改");
        }
        String inventoryBillCode = updateOtherReceiptOrderRequest.getInventoryBillCode();
        if (inventoryBillCode != null) {
            if (!inventoryBillCode.equals(oldInventoryBill.getInventoryBillCode())) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "单据编号不支持修改");
            }
        }
        Long supplierId = updateOtherReceiptOrderRequest.getSupplierId();
        Long supplierLinkmanId = updateOtherReceiptOrderRequest.getSupplierLinkmanId();
        if (supplierId != null && supplierId > 0) {
            IpmsSupplier supplier = ipmsSupplierService.getById(supplierId);
            if (supplier == null) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR, inventoryBillType + "供应商不存在");
            }
            if (supplierLinkmanId != null && supplierLinkmanId > 0) {
                IpmsSupplierLinkman supplierLinkman = ipmsSupplierLinkmanService.getById(supplierLinkmanId);
                if (supplierLinkman == null) {
                    throw new BusinessException(ErrorCode.PARAMS_ERROR, inventoryBillType + "供应商联系人不存在");
                }
            }
        } else {
            if (supplierLinkmanId != null && supplierLinkmanId > 0) {
                IpmsSupplierLinkman supplierLinkman = ipmsSupplierLinkmanService.getById(supplierLinkmanId);
                if (supplierLinkman == null) {
                    throw new BusinessException(ErrorCode.PARAMS_ERROR, inventoryBillType + "供应商联系人不存在");
                }
            }
        }
        Long customerId = updateOtherReceiptOrderRequest.getCustomerId();
        Long customerLinkmanId = updateOtherReceiptOrderRequest.getCustomerLinkmanId();
        if (customerId != null && customerId > 0) {
            IpmsCustomer customer = ipmsCustomerService.getById(customerId);
            if (customer == null) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR, inventoryBillType + "客户不存在");
            }
            if (customerLinkmanId != null && customerLinkmanId > 0) {
                IpmsCustomerLinkman customerLinkman = ipmsCustomerLinkmanService.getById(customerLinkmanId);
                if (customerLinkman == null) {
                    throw new BusinessException(ErrorCode.PARAMS_ERROR, inventoryBillType + "客户联系人不存在");
                }
            }
        } else {
            if (customerLinkmanId != null && customerLinkmanId > 0) {
                IpmsCustomerLinkman customerLinkman = ipmsCustomerLinkmanService.getById(customerLinkmanId);
                if (customerLinkman == null) {
                    throw new BusinessException(ErrorCode.PARAMS_ERROR, inventoryBillType + "客户联系人不存在");
                }
            }
        }
        Long employeeId = updateOtherReceiptOrderRequest.getEmployeeId();
        Long departmentId = updateOtherReceiptOrderRequest.getDepartmentId();
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
        IpmsInventoryBill newInventoryBill = new IpmsInventoryBill();
        BeanUtils.copyProperties(updateOtherReceiptOrderRequest, newInventoryBill);
        SafeUserVO loginUser = ipmsUserService.getLoginUser(request);
        newInventoryBill.setUpdateTime(new Date());
        newInventoryBill.setModifier(loginUser.getUserName());
        int result = ipmsInventoryBillMapper.updateById(newInventoryBill);
        if (result != 1) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, inventoryBillType + "修改数据失败");
        }
        List<UpdateOtherReceiptOrderProductNumRequest> updateOtherReceiptOrderProductNumRequestList = updateOtherReceiptOrderRequest.getUpdateOtherReceiptOrderProductNumRequestList();
        if (updateOtherReceiptOrderProductNumRequestList != null && updateOtherReceiptOrderProductNumRequestList.size() > 0) {
            List<Long> updateAndInsertInventoryBillProductList = new ArrayList<>();
            for (UpdateOtherReceiptOrderProductNumRequest updateOtherReceiptOrderProductNumRequest : updateOtherReceiptOrderProductNumRequestList) {
                Long inventoryBillProductId = updateOtherReceiptOrderProductNumRequest.getInventoryBillProductId();
                // 如果存在 inventoryBillProductId，那么肯定是要更新的数据，进行更新，并且存入列表中
                if (inventoryBillProductId != null && inventoryBillProductId > 0) {
                    updateAndInsertInventoryBillProductList.add(inventoryBillProductId);
                    int updateResult = ipmsInventoryBillProductNumService.updateOtherReceiptOrderProductAndNum(updateOtherReceiptOrderProductNumRequest, oldInventoryBill);
                    if (updateResult != 1) {
                        throw new BusinessException(ErrorCode.SYSTEM_ERROR, "更新其他入库单商品失败");
                    }
                } else {
                    // 否则，就是插入新的数据
                    AddOtherReceiptOrderProductNumRequest addOtherReceiptOrderProductNumRequest = new AddOtherReceiptOrderProductNumRequest();
                    BeanUtils.copyProperties(updateOtherReceiptOrderProductNumRequest, addOtherReceiptOrderProductNumRequest);
                    long insertInventoryBillProductId = ipmsInventoryBillProductNumService.addOtherReceiptOrderProductAndNum(addOtherReceiptOrderProductNumRequest, oldInventoryBill);
                    if (insertInventoryBillProductId < 0) {
                        throw new BusinessException(ErrorCode.SYSTEM_ERROR, "增加其他入库单商品失败");
                    }
                    updateAndInsertInventoryBillProductList.add(insertInventoryBillProductId);
                }
            }
            // 如果更新其他入库单商品的 id 不在这个列表内，那么删除其他入库单商品
            QueryWrapper<IpmsInventoryBillProductNum> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("inventory_bill_id", inventoryBillId);
            queryWrapper.notIn("inventory_bill_product_id", updateAndInsertInventoryBillProductList);
            ipmsInventoryBillProductNumService.remove(queryWrapper);
        }
        return result;
    }

    @Override
    public int updateOtherDeliveryOrder(UpdateOtherDeliveryOrderRequest updateOtherDeliveryOrderRequest, HttpServletRequest request) {
        Long inventoryBillId = updateOtherDeliveryOrderRequest.getInventoryBillId();
        if (inventoryBillId == null || inventoryBillId <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "其他出库单 id 为空或者不合法");
        }
        IpmsInventoryBill oldInventoryBill = ipmsInventoryBillMapper.selectById(inventoryBillId);
        if (oldInventoryBill == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "其他出库单不存在");
        }
        Integer checkState = oldInventoryBill.getCheckState();
        String inventoryBillType = oldInventoryBill.getInventoryBillType();
        if (Constant.CHECKED == checkState) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, inventoryBillType + "单据已审核，无法修改");
        }
        String inventoryBillCode = updateOtherDeliveryOrderRequest.getInventoryBillCode();
        if (inventoryBillCode != null) {
            if (!inventoryBillCode.equals(oldInventoryBill.getInventoryBillCode())) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "单据编号不支持修改");
            }
        }
        Long customerId = updateOtherDeliveryOrderRequest.getCustomerId();
        Long customerLinkmanId = updateOtherDeliveryOrderRequest.getCustomerLinkmanId();
        if (customerId != null && customerId > 0) {
            IpmsCustomer customer = ipmsCustomerService.getById(customerId);
            if (customer == null) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR, inventoryBillType + "客户不存在");
            }
            if (customerLinkmanId != null && customerLinkmanId > 0) {
                IpmsCustomerLinkman customerLinkman = ipmsCustomerLinkmanService.getById(customerLinkmanId);
                if (customerLinkman == null) {
                    throw new BusinessException(ErrorCode.PARAMS_ERROR, inventoryBillType + "客户联系人不存在");
                }
            }
        } else {
            if (customerLinkmanId != null && customerLinkmanId > 0) {
                IpmsCustomerLinkman customerLinkman = ipmsCustomerLinkmanService.getById(customerLinkmanId);
                if (customerLinkman == null) {
                    throw new BusinessException(ErrorCode.PARAMS_ERROR, inventoryBillType + "客户联系人不存在");
                }
            }
        }
        Long employeeId = updateOtherDeliveryOrderRequest.getEmployeeId();
        Long departmentId = updateOtherDeliveryOrderRequest.getDepartmentId();
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
        IpmsInventoryBill newInventoryBill = new IpmsInventoryBill();
        BeanUtils.copyProperties(updateOtherDeliveryOrderRequest, newInventoryBill);
        SafeUserVO loginUser = ipmsUserService.getLoginUser(request);
        newInventoryBill.setUpdateTime(new Date());
        newInventoryBill.setModifier(loginUser.getUserName());
        int result = ipmsInventoryBillMapper.updateById(newInventoryBill);
        if (result != 1) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, inventoryBillType + "修改数据失败");
        }
        List<UpdateOtherDeliveryOrderProductNumRequest> updateOtherDeliveryOrderProductNumRequestList = updateOtherDeliveryOrderRequest.getUpdateOtherDeliveryOrderProductNumRequestList();
        if (updateOtherDeliveryOrderProductNumRequestList != null && updateOtherDeliveryOrderProductNumRequestList.size() > 0) {
            List<Long> updateAndInsertInventoryBillProductList = new ArrayList<>();
            for (UpdateOtherDeliveryOrderProductNumRequest updateOtherDeliveryOrderProductNumRequest : updateOtherDeliveryOrderProductNumRequestList) {
                Long inventoryBillProductId = updateOtherDeliveryOrderProductNumRequest.getInventoryBillProductId();
                // 如果存在 inventoryBillProductId，那么肯定是要更新的数据，进行更新，并且存入列表中
                if (inventoryBillProductId != null && inventoryBillProductId > 0) {
                    updateAndInsertInventoryBillProductList.add(inventoryBillProductId);
                    int updateResult = ipmsInventoryBillProductNumService.updateOtherDeliveryOrderProductAndNum(updateOtherDeliveryOrderProductNumRequest, oldInventoryBill);
                    if (updateResult != 1) {
                        throw new BusinessException(ErrorCode.SYSTEM_ERROR, "更新其他入库单商品失败");
                    }
                } else {
                    // 否则，就是插入新的数据
                    AddOtherDeliveryOrderProductNumRequest addOtherDeliveryOrderProductNumRequest = new AddOtherDeliveryOrderProductNumRequest();
                    BeanUtils.copyProperties(updateOtherDeliveryOrderProductNumRequest, addOtherDeliveryOrderProductNumRequest);
                    long insertInventoryBillProductId = ipmsInventoryBillProductNumService.addOtherDeliveryOrderProductAndNum(addOtherDeliveryOrderProductNumRequest, oldInventoryBill);
                    if (insertInventoryBillProductId < 0) {
                        throw new BusinessException(ErrorCode.SYSTEM_ERROR, "增加其他入库单商品失败");
                    }
                    updateAndInsertInventoryBillProductList.add(insertInventoryBillProductId);
                }
            }
            // 如果更新其他入库单商品的 id 不在这个列表内，那么删除其他入库单商品
            QueryWrapper<IpmsInventoryBillProductNum> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("inventory_bill_id", inventoryBillId);
            queryWrapper.notIn("inventory_bill_product_id", updateAndInsertInventoryBillProductList);
            ipmsInventoryBillProductNumService.remove(queryWrapper);
        }
        return result;
    }

    @Override
    public int updateWarehouseTransferOrder(UpdateWarehouseTransferOrderRequest updateWarehouseTransferOrderRequest, HttpServletRequest request) {
        Long inventoryBillId = updateWarehouseTransferOrderRequest.getInventoryBillId();
        if (inventoryBillId == null || inventoryBillId <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "移仓单 id 为空或者不合法");
        }
        IpmsInventoryBill oldInventoryBill = ipmsInventoryBillMapper.selectById(inventoryBillId);
        if (oldInventoryBill == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "移仓单不存在");
        }
        Integer checkState = oldInventoryBill.getCheckState();
        String inventoryBillType = oldInventoryBill.getInventoryBillType();
        if (Constant.CHECKED == checkState) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, inventoryBillType + "单据已审核，无法修改");
        }
        String inventoryBillCode = updateWarehouseTransferOrderRequest.getInventoryBillCode();
        if (inventoryBillCode != null) {
            if (!inventoryBillCode.equals(oldInventoryBill.getInventoryBillCode())) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "单据编号不支持修改");
            }
        }
        Long employeeId = updateWarehouseTransferOrderRequest.getEmployeeId();
        Long departmentId = updateWarehouseTransferOrderRequest.getDepartmentId();
        Long transferDepartmentId = updateWarehouseTransferOrderRequest.getTransferDepartmentId();
        if (employeeId != null && employeeId > 0) {
            IpmsEmployee employee = ipmsEmployeeService.getById(employeeId);
            if (employee == null) {
                throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, inventoryBillType + "经办人不存在");
            }
        }
        if (departmentId != null && departmentId > 0) {
            IpmsDepartment department = ipmsDepartmentService.getById(departmentId);
            if (department == null) {
                throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, inventoryBillType + "调出部门不存在");
            }
        }
        if (transferDepartmentId != null && transferDepartmentId > 0) {
            IpmsDepartment transferDepartment = ipmsDepartmentService.getById(transferDepartmentId);
            if (transferDepartment == null) {
                throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, inventoryBillType + "调入部门不存在");
            }
        }
        IpmsInventoryBill newInventoryBill = new IpmsInventoryBill();
        BeanUtils.copyProperties(updateWarehouseTransferOrderRequest, newInventoryBill);
        SafeUserVO loginUser = ipmsUserService.getLoginUser(request);
        newInventoryBill.setUpdateTime(new Date());
        newInventoryBill.setModifier(loginUser.getUserName());
        int result = ipmsInventoryBillMapper.updateById(newInventoryBill);
        if (result != 1) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, inventoryBillType + "修改数据失败");
        }
        List<UpdateWarehouseTransferOrderProductNumRequest> updateWarehouseTransferOrderProductNumRequestList = updateWarehouseTransferOrderRequest.getUpdateWarehouseTransferOrderProductNumRequestList();
        if (updateWarehouseTransferOrderProductNumRequestList != null && updateWarehouseTransferOrderProductNumRequestList.size() > 0) {
            List<Long> updateAndInsertInventoryBillProductList = new ArrayList<>();
            for (UpdateWarehouseTransferOrderProductNumRequest updateWarehouseTransferOrderProductNumRequest : updateWarehouseTransferOrderProductNumRequestList) {
                Long inventoryBillProductId = updateWarehouseTransferOrderProductNumRequest.getInventoryBillProductId();
                // 如果存在 inventoryBillProductId，那么肯定是要更新的数据，进行更新，并且存入列表中
                if (inventoryBillProductId != null && inventoryBillProductId > 0) {
                    updateAndInsertInventoryBillProductList.add(inventoryBillProductId);
                    int updateResult = ipmsInventoryBillProductNumService.updateWarehouseTransferOrderProductAndNum(updateWarehouseTransferOrderProductNumRequest, oldInventoryBill);
                    if (updateResult != 1) {
                        throw new BusinessException(ErrorCode.SYSTEM_ERROR, "更新移仓单商品失败");
                    }
                } else {
                    // 否则，就是插入新的数据
                    AddWarehouseTransferOrderProductNumRequest addWarehouseTransferOrderProductNumRequest = new AddWarehouseTransferOrderProductNumRequest();
                    BeanUtils.copyProperties(updateWarehouseTransferOrderProductNumRequest, addWarehouseTransferOrderProductNumRequest);
                    long insertInventoryBillProductId = ipmsInventoryBillProductNumService.addWarehouseTransferOrderProductAndNum(addWarehouseTransferOrderProductNumRequest, oldInventoryBill);
                    if (insertInventoryBillProductId < 0) {
                        throw new BusinessException(ErrorCode.SYSTEM_ERROR, "增加移仓单商品失败");
                    }
                    updateAndInsertInventoryBillProductList.add(insertInventoryBillProductId);
                }
            }
            // 如果更新移仓单商品的 id 不在这个列表内，那么删除移仓单商品
            QueryWrapper<IpmsInventoryBillProductNum> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("inventory_bill_id", inventoryBillId);
            queryWrapper.notIn("inventory_bill_product_id", updateAndInsertInventoryBillProductList);
            ipmsInventoryBillProductNumService.remove(queryWrapper);
        }
        return result;
    }

    @Override
    public Page<SafeOtherReceiptOrderVO> pagingFuzzyQueryOtherReceiptOrder(FuzzyQueryRequest fuzzyQueryRequest, HttpServletRequest request) {
        if (fuzzyQueryRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        // 2. 进行分页查询，分情况查询，销售订单，销售出库单和销售退货单
        Page<IpmsInventoryBill> page = new Page<>(fuzzyQueryRequest.getCurrentPage(), fuzzyQueryRequest.getPageSize());
        QueryWrapper<IpmsInventoryBill> ipmsInventoryBillQueryWrapper = new QueryWrapper<>();
        String fuzzyText = fuzzyQueryRequest.getFuzzyText();
        if (fuzzyText != null) {
            ipmsInventoryBillQueryWrapper.like("inventory_bill_code", fuzzyText)
                    .and(billType -> billType.eq("inventory_bill_type", InventoryBillConstant.OTHER_RECEIPT_ORDER)).or()
                    .like("inventory_bill_date", fuzzyText)
                    .and(billType -> billType.eq("inventory_bill_type", InventoryBillConstant.OTHER_RECEIPT_ORDER)).or()
                    .like("inventory_bill_business_type", fuzzyText)
                    .and(billType -> billType.eq("inventory_bill_type", InventoryBillConstant.OTHER_RECEIPT_ORDER)).or()
                    .like("inventory_bill_remark", fuzzyText)
                    .and(billType -> billType.eq("inventory_bill_type", InventoryBillConstant.OTHER_RECEIPT_ORDER));
        } else {
            ipmsInventoryBillQueryWrapper.eq("inventory_bill_type", InventoryBillConstant.OTHER_RECEIPT_ORDER);
        }
        Page<IpmsInventoryBill> inventoryBillPage = ipmsInventoryBillMapper.selectPage(page, ipmsInventoryBillQueryWrapper);
        List<SafeOtherReceiptOrderVO> safeOtherReceiptOrderVOList = inventoryBillPage.getRecords().stream().map(ipmsInventoryBill -> {
            SafeOtherReceiptOrderVO safeOtherReceiptOrderVO = new SafeOtherReceiptOrderVO();
            BeanUtils.copyProperties(ipmsInventoryBill, safeOtherReceiptOrderVO);
            // 查询客户信息，并设置到返回封装类中
            Long customerId = ipmsInventoryBill.getCustomerId();
            if (customerId != null && customerId > 0) {
                IpmsCustomer customer = ipmsCustomerService.getById(customerId);
                if (customer == null) {
                    throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "找不到客户信息，系统业务错误");
                }
                safeOtherReceiptOrderVO.setCustomerId(customerId);
                safeOtherReceiptOrderVO.setCustomerName(customer.getCustomerName());
            }
            Long customerLinkmanId = ipmsInventoryBill.getCustomerLinkmanId();
            if (customerLinkmanId != null && customerLinkmanId > 0) {
                IpmsCustomerLinkman customerLinkman = ipmsCustomerLinkmanService.getById(customerLinkmanId);
                if (customerLinkman == null) {
                    throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "找不到客户联系人信息，系统业务错误");
                }
                safeOtherReceiptOrderVO.setCustomerLinkmanId(customerLinkmanId);
                safeOtherReceiptOrderVO.setCustomerLinkmanAdminDivision(customerLinkman.getLinkmanAdminDivision());
                safeOtherReceiptOrderVO.setCustomerLinkmanDetailAddress(customerLinkman.getLinkmanDetailAddress());
            }
            // 查询供应商信息，并设置到返回封装类中
            Long supplierId = ipmsInventoryBill.getSupplierId();
            if (supplierId != null && supplierId > 0) {
                IpmsSupplier supplier = ipmsSupplierService.getById(supplierId);
                if (supplier == null) {
                    throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "找不到供应商信息，系统业务错误");
                }
                safeOtherReceiptOrderVO.setSupplierId(supplierId);
                safeOtherReceiptOrderVO.setSupplierName(supplier.getSupplierName());
            }
            Long supplierLinkmanId = ipmsInventoryBill.getSupplierLinkmanId();
            if (supplierLinkmanId != null && supplierLinkmanId > 0) {
                IpmsSupplierLinkman supplierLinkman = ipmsSupplierLinkmanService.getById(supplierLinkmanId);
                if (supplierLinkman == null) {
                    throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "找不到供应商联系人信息，系统业务错误");
                }
                safeOtherReceiptOrderVO.setSupplierLinkmanId(supplierLinkmanId);
                safeOtherReceiptOrderVO.setSupplierLinkmanAdminDivision(supplierLinkman.getLinkmanAdminDivision());
                safeOtherReceiptOrderVO.setSupplierLinkmanAdminDivision(supplierLinkman.getLinkmanDetailAddress());
            }
            // 查询职员信息，并设置到返回封装类中
            Long employeeId = ipmsInventoryBill.getEmployeeId();
            if (employeeId != null && employeeId > 0) {
                IpmsEmployee employee = ipmsEmployeeService.getById(employeeId);
                if (employee == null) {
                    throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "找不到职员信息，系统业务错误");
                }
                safeOtherReceiptOrderVO.setEmployeeId(employeeId);
                safeOtherReceiptOrderVO.setEmployeeName(employee.getEmployeeName());
            }
            // 查询部门信息，并设置到返回封装类中
            Long departmentId = ipmsInventoryBill.getDepartmentId();
            if (departmentId != null && departmentId > 0) {
                IpmsDepartment department = ipmsDepartmentService.getById(departmentId);
                if (department == null) {
                    throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "找不到部门信息，系统业务错误");
                }
                safeOtherReceiptOrderVO.setDepartmentId(departmentId);
                safeOtherReceiptOrderVO.setDepartmentName(department.getDepartmentName());
            }
            // 查询其他入库单的商品信息，并设置到返回封装类中
            Long inventoryBillId = ipmsInventoryBill.getInventoryBillId();
            if (inventoryBillId != null && inventoryBillId > 0) {
                QueryWrapper<IpmsInventoryBillProductNum> inventoryBillProductNumQueryWrapper = new QueryWrapper<>();
                inventoryBillProductNumQueryWrapper.eq("inventory_bill_id", inventoryBillId);
                List<IpmsInventoryBillProductNum> inventoryBillProductList = ipmsInventoryBillProductNumService.list(inventoryBillProductNumQueryWrapper);
                List<SafeOtherReceiptOrderProductNumVO> safeOtherReceiptOrderProductVOList = new ArrayList<>();
                if (inventoryBillProductList != null && inventoryBillProductList.size() > 0) {
                    for (IpmsInventoryBillProductNum inventoryBillProduct : inventoryBillProductList) {
                        SafeOtherReceiptOrderProductNumVO safeInventoryBillProductVO = new SafeOtherReceiptOrderProductNumVO();
                        BeanUtils.copyProperties(inventoryBillProduct, safeInventoryBillProductVO);
                        // 在销售商品中查询商品等信息，并设置到返回封装类中
                        QueryWrapper<IpmsProductInventory> productInventoryQueryWrapper = new QueryWrapper<>();
                        Long productId = inventoryBillProduct.getProductId();
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
                            safeInventoryBillProductVO.setSafeProductVO(safeProductVO);
                            productInventoryQueryWrapper.eq("product_id", productId);
                        }
                        Long warehouseId = inventoryBillProduct.getWarehouseId();
                        if (warehouseId != null && warehouseId > 0) {
                            IpmsWarehouse warehouse = ipmsWarehouseService.getById(warehouseId);
                            if (warehouse == null) {
                                throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "找不到仓库，系统业务逻辑错误");
                            }
                            safeInventoryBillProductVO.setWarehouseId(warehouseId);
                            safeInventoryBillProductVO.setWarehouseName(warehouse.getWarehouseName());
                            productInventoryQueryWrapper.eq("warehouse_id", warehouseId);
                        }
                        Long warehousePositionId = inventoryBillProduct.getWarehousePositionId();
                        if (warehousePositionId != null && warehousePositionId > 0) {
                            IpmsWarehousePosition warehousePosition = ipmsWarehousePositionService.getById(warehousePositionId);
                            if (warehousePosition != null) {
                                safeInventoryBillProductVO.setWarehousePositionId(warehousePositionId);
                                safeInventoryBillProductVO.setWarehousePositionName(warehousePosition.getWarehousePositionName());
                                productInventoryQueryWrapper.eq("warehouse_position_id", warehousePositionId);
                            }
                        }
                        // 查询库存
                        IpmsProductInventory productInventory = ipmsProductInventoryService.getOne(productInventoryQueryWrapper);
                        if (productInventory != null) {
                            safeInventoryBillProductVO.setAvailableInventory(productInventory.getProductInventorySurplusNum());
                        }
                        safeOtherReceiptOrderProductVOList.add(safeInventoryBillProductVO);
                    }
                }
                safeOtherReceiptOrderVO.setSafeOtherReceiptOrderProductNumVOList(safeOtherReceiptOrderProductVOList);
            }
            // 单据时间格式化
            safeOtherReceiptOrderVO.setCreateTime(TimeFormatUtil.dateFormatting(ipmsInventoryBill.getCreateTime()));
            safeOtherReceiptOrderVO.setUpdateTime(TimeFormatUtil.dateFormatting(ipmsInventoryBill.getUpdateTime()));
            safeOtherReceiptOrderVO.setCheckTime(TimeFormatUtil.dateFormatting(ipmsInventoryBill.getCheckTime()));
            return safeOtherReceiptOrderVO;
        }).collect(Collectors.toList());
        // 关键步骤
        Page<SafeOtherReceiptOrderVO> safeOtherReceiptOrderVOPage = new PageDTO<>(inventoryBillPage.getCurrent(), inventoryBillPage.getSize(), inventoryBillPage.getTotal());
        safeOtherReceiptOrderVOPage.setRecords(safeOtherReceiptOrderVOList);
        return safeOtherReceiptOrderVOPage;
    }

    @Override
    public Page<SafeOtherDeliveryOrderVO> pagingFuzzyQueryOtherDeliveryOrder(FuzzyQueryRequest fuzzyQueryRequest, HttpServletRequest request) {
        if (fuzzyQueryRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        // 2. 进行分页查询，分情况查询，销售订单，销售出库单和销售退货单
        Page<IpmsInventoryBill> page = new Page<>(fuzzyQueryRequest.getCurrentPage(), fuzzyQueryRequest.getPageSize());
        QueryWrapper<IpmsInventoryBill> ipmsInventoryBillQueryWrapper = new QueryWrapper<>();
        String fuzzyText = fuzzyQueryRequest.getFuzzyText();
        if (fuzzyText != null) {
            ipmsInventoryBillQueryWrapper.like("inventory_bill_code", fuzzyText)
                    .and(billType -> billType.eq("inventory_bill_type", InventoryBillConstant.OTHER_DELIVERY_ORDER)).or()
                    .like("inventory_bill_date", fuzzyText)
                    .and(billType -> billType.eq("inventory_bill_type", InventoryBillConstant.OTHER_DELIVERY_ORDER)).or()
                    .like("inventory_bill_business_type", fuzzyText)
                    .and(billType -> billType.eq("inventory_bill_type", InventoryBillConstant.OTHER_DELIVERY_ORDER)).or()
                    .like("inventory_bill_remark", fuzzyText)
                    .and(billType -> billType.eq("inventory_bill_type", InventoryBillConstant.OTHER_DELIVERY_ORDER));
        } else {
            ipmsInventoryBillQueryWrapper.eq("inventory_bill_type", InventoryBillConstant.OTHER_DELIVERY_ORDER);
        }
        Page<IpmsInventoryBill> inventoryBillPage = ipmsInventoryBillMapper.selectPage(page, ipmsInventoryBillQueryWrapper);
        List<SafeOtherDeliveryOrderVO> safeOtherDeliveryOrderVOList = inventoryBillPage.getRecords().stream().map(ipmsInventoryBill -> {
            SafeOtherDeliveryOrderVO safeOtherDeliveryOrderVO = new SafeOtherDeliveryOrderVO();
            BeanUtils.copyProperties(ipmsInventoryBill, safeOtherDeliveryOrderVO);
            // 查询客户信息，并设置到返回封装类中
            Long customerId = ipmsInventoryBill.getCustomerId();
            if (customerId != null && customerId > 0) {
                IpmsCustomer customer = ipmsCustomerService.getById(customerId);
                if (customer == null) {
                    throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "找不到客户信息，系统业务错误");
                }
                safeOtherDeliveryOrderVO.setCustomerId(customerId);
                safeOtherDeliveryOrderVO.setCustomerName(customer.getCustomerName());
            }
            Long customerLinkmanId = ipmsInventoryBill.getCustomerLinkmanId();
            if (customerLinkmanId != null && customerLinkmanId > 0) {
                IpmsCustomerLinkman customerLinkman = ipmsCustomerLinkmanService.getById(customerLinkmanId);
                if (customerLinkman == null) {
                    throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "找不到客户联系人信息，系统业务错误");
                }
                safeOtherDeliveryOrderVO.setCustomerLinkmanId(customerLinkmanId);
                safeOtherDeliveryOrderVO.setCustomerLinkmanAdminDivision(customerLinkman.getLinkmanAdminDivision());
                safeOtherDeliveryOrderVO.setCustomerLinkmanDetailAddress(customerLinkman.getLinkmanDetailAddress());
            }
            // 查询职员信息，并设置到返回封装类中
            Long employeeId = ipmsInventoryBill.getEmployeeId();
            if (employeeId != null && employeeId > 0) {
                IpmsEmployee employee = ipmsEmployeeService.getById(employeeId);
                if (employee == null) {
                    throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "找不到职员信息，系统业务错误");
                }
                safeOtherDeliveryOrderVO.setEmployeeId(employeeId);
                safeOtherDeliveryOrderVO.setEmployeeName(employee.getEmployeeName());
            }
            // 查询部门信息，并设置到返回封装类中
            Long departmentId = ipmsInventoryBill.getDepartmentId();
            if (departmentId != null && departmentId > 0) {
                IpmsDepartment department = ipmsDepartmentService.getById(departmentId);
                if (department == null) {
                    throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "找不到部门信息，系统业务错误");
                }
                safeOtherDeliveryOrderVO.setDepartmentId(departmentId);
                safeOtherDeliveryOrderVO.setDepartmentName(department.getDepartmentName());
            }
            // 查询其他入库单的商品信息，并设置到返回封装类中
            Long inventoryBillId = ipmsInventoryBill.getInventoryBillId();
            if (inventoryBillId != null && inventoryBillId > 0) {
                QueryWrapper<IpmsInventoryBillProductNum> inventoryBillProductNumQueryWrapper = new QueryWrapper<>();
                inventoryBillProductNumQueryWrapper.eq("inventory_bill_id", inventoryBillId);
                List<IpmsInventoryBillProductNum> inventoryBillProductList = ipmsInventoryBillProductNumService.list(inventoryBillProductNumQueryWrapper);
                List<SafeOtherDeliveryOrderProductNumVO> safeOtherDeliveryOrderProductVOList = new ArrayList<>();
                if (inventoryBillProductList != null && inventoryBillProductList.size() > 0) {
                    for (IpmsInventoryBillProductNum inventoryBillProduct : inventoryBillProductList) {
                        SafeOtherDeliveryOrderProductNumVO safeInventoryBillProductVO = new SafeOtherDeliveryOrderProductNumVO();
                        BeanUtils.copyProperties(inventoryBillProduct, safeInventoryBillProductVO);
                        // 在销售商品中查询商品等信息，并设置到返回封装类中
                        QueryWrapper<IpmsProductInventory> productInventoryQueryWrapper = new QueryWrapper<>();
                        Long productId = inventoryBillProduct.getProductId();
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
                            safeInventoryBillProductVO.setSafeProductVO(safeProductVO);
                            productInventoryQueryWrapper.eq("product_id", productId);
                        }
                        Long warehouseId = inventoryBillProduct.getWarehouseId();
                        if (warehouseId != null && warehouseId > 0) {
                            IpmsWarehouse warehouse = ipmsWarehouseService.getById(warehouseId);
                            if (warehouse == null) {
                                throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "找不到仓库，系统业务逻辑错误");
                            }
                            safeInventoryBillProductVO.setWarehouseId(warehouseId);
                            safeInventoryBillProductVO.setWarehouseName(warehouse.getWarehouseName());
                            productInventoryQueryWrapper.eq("warehouse_id", warehouseId);
                        }
                        Long warehousePositionId = inventoryBillProduct.getWarehousePositionId();
                        if (warehousePositionId != null && warehousePositionId > 0) {
                            IpmsWarehousePosition warehousePosition = ipmsWarehousePositionService.getById(warehousePositionId);
                            if (warehousePosition != null) {
                                safeInventoryBillProductVO.setWarehousePositionId(warehousePositionId);
                                safeInventoryBillProductVO.setWarehousePositionName(warehousePosition.getWarehousePositionName());
                                productInventoryQueryWrapper.eq("warehouse_position_id", warehousePositionId);
                            }
                        }
                        // 查询库存
                        IpmsProductInventory productInventory = ipmsProductInventoryService.getOne(productInventoryQueryWrapper);
                        if (productInventory != null) {
                            safeInventoryBillProductVO.setAvailableInventory(productInventory.getProductInventorySurplusNum());
                        }
                        safeOtherDeliveryOrderProductVOList.add(safeInventoryBillProductVO);
                    }
                }
                safeOtherDeliveryOrderVO.setSafeOtherDeliveryOrderProductNumVOList(safeOtherDeliveryOrderProductVOList);
            }
            // 单据时间格式化
            safeOtherDeliveryOrderVO.setCreateTime(TimeFormatUtil.dateFormatting(ipmsInventoryBill.getCreateTime()));
            safeOtherDeliveryOrderVO.setUpdateTime(TimeFormatUtil.dateFormatting(ipmsInventoryBill.getUpdateTime()));
            safeOtherDeliveryOrderVO.setCheckTime(TimeFormatUtil.dateFormatting(ipmsInventoryBill.getCheckTime()));
            return safeOtherDeliveryOrderVO;
        }).collect(Collectors.toList());
        // 关键步骤
        Page<SafeOtherDeliveryOrderVO> safeOtherDeliveryOrderVOPage = new PageDTO<>(inventoryBillPage.getCurrent(), inventoryBillPage.getSize(), inventoryBillPage.getTotal());
        safeOtherDeliveryOrderVOPage.setRecords(safeOtherDeliveryOrderVOList);
        return safeOtherDeliveryOrderVOPage;
    }

    @Override
    public Page<SafeWarehouseTransferOrderVO> pagingFuzzyQueryWarehouseTransferOrder(FuzzyQueryRequest fuzzyQueryRequest, HttpServletRequest request) {
        if (fuzzyQueryRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Page<IpmsInventoryBill> page = new Page<>(fuzzyQueryRequest.getCurrentPage(), fuzzyQueryRequest.getPageSize());
        QueryWrapper<IpmsInventoryBill> ipmsInventoryBillQueryWrapper = new QueryWrapper<>();
        String fuzzyText = fuzzyQueryRequest.getFuzzyText();
        if (fuzzyText != null) {
            ipmsInventoryBillQueryWrapper.like("inventory_bill_code", fuzzyText)
                    .and(billType -> billType.eq("inventory_bill_type", InventoryBillConstant.WAREHOUSE_TRANSFER_ORDER)).or()
                    .like("inventory_bill_date", fuzzyText)
                    .and(billType -> billType.eq("inventory_bill_type", InventoryBillConstant.WAREHOUSE_TRANSFER_ORDER)).or()
                    .like("inventory_bill_business_type", fuzzyText)
                    .and(billType -> billType.eq("inventory_bill_type", InventoryBillConstant.WAREHOUSE_TRANSFER_ORDER)).or()
                    .like("inventory_bill_remark", fuzzyText)
                    .and(billType -> billType.eq("inventory_bill_type", InventoryBillConstant.WAREHOUSE_TRANSFER_ORDER));
        } else {
            ipmsInventoryBillQueryWrapper.eq("inventory_bill_type", InventoryBillConstant.WAREHOUSE_TRANSFER_ORDER);
        }
        Page<IpmsInventoryBill> inventoryBillPage = ipmsInventoryBillMapper.selectPage(page, ipmsInventoryBillQueryWrapper);
        List<SafeWarehouseTransferOrderVO> safeWarehouseTransferOrderVOList = inventoryBillPage.getRecords().stream().map(ipmsInventoryBill -> {
            SafeWarehouseTransferOrderVO safeWarehouseTransferOrderVO = new SafeWarehouseTransferOrderVO();
            BeanUtils.copyProperties(ipmsInventoryBill, safeWarehouseTransferOrderVO);
            // 查询职员信息，并设置到返回封装类中
            Long employeeId = ipmsInventoryBill.getEmployeeId();
            if (employeeId != null && employeeId > 0) {
                IpmsEmployee employee = ipmsEmployeeService.getById(employeeId);
                if (employee == null) {
                    throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "找不到经办人信息，系统业务错误");
                }
                safeWarehouseTransferOrderVO.setEmployeeId(employeeId);
                safeWarehouseTransferOrderVO.setEmployeeName(employee.getEmployeeName());
            }
            // 查询部门信息，并设置到返回封装类中
            Long departmentId = ipmsInventoryBill.getDepartmentId();
            if (departmentId != null && departmentId > 0) {
                IpmsDepartment department = ipmsDepartmentService.getById(departmentId);
                if (department == null) {
                    throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "找不到调出部门信息，系统业务错误");
                }
                safeWarehouseTransferOrderVO.setDepartmentId(departmentId);
                safeWarehouseTransferOrderVO.setDepartmentName(department.getDepartmentName());
            }
            Long transferDepartmentId = ipmsInventoryBill.getTransferDepartmentId();
            if (transferDepartmentId != null && transferDepartmentId > 0) {
                IpmsDepartment transferDepartment = ipmsDepartmentService.getById(transferDepartmentId);
                if (transferDepartment == null) {
                    throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "找不到调入部门信息，系统业务错误");
                }
                safeWarehouseTransferOrderVO.setTransferDepartmentId(transferDepartmentId);
                safeWarehouseTransferOrderVO.setTransferDepartmentName(transferDepartment.getDepartmentName());
            }
            // 查询其他入库单的商品信息，并设置到返回封装类中
            Long inventoryBillId = ipmsInventoryBill.getInventoryBillId();
            if (inventoryBillId != null && inventoryBillId > 0) {
                QueryWrapper<IpmsInventoryBillProductNum> inventoryBillProductNumQueryWrapper = new QueryWrapper<>();
                inventoryBillProductNumQueryWrapper.eq("inventory_bill_id", inventoryBillId);
                List<IpmsInventoryBillProductNum> inventoryBillProductList = ipmsInventoryBillProductNumService.list(inventoryBillProductNumQueryWrapper);
                List<SafeWarehouseTransferOrderProductNumVO> safeWarehouseTransferOrderProductVOList = new ArrayList<>();
                if (inventoryBillProductList != null && inventoryBillProductList.size() > 0) {
                    for (IpmsInventoryBillProductNum inventoryBillProduct : inventoryBillProductList) {
                        SafeWarehouseTransferOrderProductNumVO safeInventoryBillProductVO = new SafeWarehouseTransferOrderProductNumVO();
                        BeanUtils.copyProperties(inventoryBillProduct, safeInventoryBillProductVO);
                        // 在销售商品中查询商品等信息，并设置到返回封装类中
                        QueryWrapper<IpmsProductInventory> transferOutProductInventoryQueryWrapper = new QueryWrapper<>();
                        QueryWrapper<IpmsProductInventory> transferIntoProductInventoryQueryWrapper = new QueryWrapper<>();
                        Long productId = inventoryBillProduct.getProductId();
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
                            safeInventoryBillProductVO.setSafeProductVO(safeProductVO);
                            transferOutProductInventoryQueryWrapper.eq("product_id", productId);
                            transferIntoProductInventoryQueryWrapper.eq("product_id", productId);
                        }
                        Long warehouseId = inventoryBillProduct.getWarehouseId();
                        if (warehouseId != null && warehouseId > 0) {
                            IpmsWarehouse warehouse = ipmsWarehouseService.getById(warehouseId);
                            if (warehouse == null) {
                                throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "找不到调出仓库，系统业务逻辑错误");
                            }
                            safeInventoryBillProductVO.setWarehouseId(warehouseId);
                            safeInventoryBillProductVO.setWarehouseName(warehouse.getWarehouseName());
                            transferOutProductInventoryQueryWrapper.eq("warehouse_id", warehouseId);
                        }
                        Long transferWarehouseId = inventoryBillProduct.getTransferWarehouseId();
                        if (transferWarehouseId != null && transferWarehouseId > 0) {
                            IpmsWarehouse transferWarehouse = ipmsWarehouseService.getById(transferWarehouseId);
                            if (transferWarehouse == null) {
                                throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "找不到调入仓库，系统业务逻辑错误");
                            }
                            safeInventoryBillProductVO.setTransferWarehouseId(transferWarehouseId);
                            safeInventoryBillProductVO.setTransferWarehouseName(transferWarehouse.getWarehouseName());
                            transferIntoProductInventoryQueryWrapper.eq("warehouse_id", transferWarehouseId);
                        }
                        Long warehousePositionId = inventoryBillProduct.getWarehousePositionId();
                        if (warehousePositionId != null && warehousePositionId > 0) {
                            IpmsWarehousePosition warehousePosition = ipmsWarehousePositionService.getById(warehousePositionId);
                            if (warehousePosition != null) {
                                safeInventoryBillProductVO.setWarehousePositionId(warehousePositionId);
                                safeInventoryBillProductVO.setWarehousePositionName(warehousePosition.getWarehousePositionName());
                                transferOutProductInventoryQueryWrapper.eq("warehouse_position_id", warehousePositionId);
                            }
                        }
                        Long transferWarehousePositionId = inventoryBillProduct.getTransferWarehousePositionId();
                        if (transferWarehousePositionId != null && transferWarehousePositionId > 0) {
                            IpmsWarehousePosition transferWarehousePosition = ipmsWarehousePositionService.getById(transferWarehousePositionId);
                            if (transferWarehousePosition != null) {
                                safeInventoryBillProductVO.setTransferWarehousePositionId(transferWarehousePositionId);
                                safeInventoryBillProductVO.setTransferWarehousePositionName(transferWarehousePosition.getWarehousePositionName());
                                transferIntoProductInventoryQueryWrapper.eq("warehouse_position_id", transferWarehousePositionId);
                            }
                        }
                        // 查询库存
                        IpmsProductInventory transferOutProductInventory = ipmsProductInventoryService.getOne(transferOutProductInventoryQueryWrapper);
                        IpmsProductInventory transferIntoProductInventory = ipmsProductInventoryService.getOne(transferIntoProductInventoryQueryWrapper);
                        if (transferOutProductInventory != null) {
                            safeInventoryBillProductVO.setTransferOutAvailableInventory(transferOutProductInventory.getProductInventorySurplusNum());
                        }
                        if (transferIntoProductInventory != null) {
                            safeInventoryBillProductVO.setTransferIntoAvailableInventory(transferIntoProductInventory.getProductInventorySurplusNum());
                        }
                        safeWarehouseTransferOrderProductVOList.add(safeInventoryBillProductVO);
                    }
                }
                safeWarehouseTransferOrderVO.setSafeWarehouseTransferOrderProductNumVOList(safeWarehouseTransferOrderProductVOList);
            }
            // 单据时间格式化
            safeWarehouseTransferOrderVO.setCreateTime(TimeFormatUtil.dateFormatting(ipmsInventoryBill.getCreateTime()));
            safeWarehouseTransferOrderVO.setUpdateTime(TimeFormatUtil.dateFormatting(ipmsInventoryBill.getUpdateTime()));
            safeWarehouseTransferOrderVO.setCheckTime(TimeFormatUtil.dateFormatting(ipmsInventoryBill.getCheckTime()));
            return safeWarehouseTransferOrderVO;
        }).collect(Collectors.toList());
        // 关键步骤
        Page<SafeWarehouseTransferOrderVO> safeWarehouseTransferOrderVOPage = new PageDTO<>(inventoryBillPage.getCurrent(), inventoryBillPage.getSize(), inventoryBillPage.getTotal());
        safeWarehouseTransferOrderVOPage.setRecords(safeWarehouseTransferOrderVOList);
        return safeWarehouseTransferOrderVOPage;
    }

    /**
     * 验证库存单据类型是否存在
     *
     * @param inventoryBillType 库存单据类型
     */
    private void validInventoryBillType(String inventoryBillType) {
        List<String> billTypeList = Arrays.asList(InventoryBillConstant.OTHER_RECEIPT_ORDER, InventoryBillConstant.OTHER_DELIVERY_ORDER,
                InventoryBillConstant.WAREHOUSE_TRANSFER_ORDER, InventoryBillConstant.TRANSFER_ISSUE_ORDER, InventoryBillConstant.TRANSFER_RECEIPT_ORDER);
        int validTypeResult = ValidType.valid(billTypeList, inventoryBillType);
        if (validTypeResult == 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "单据类型不存在");
        }
    }
}




