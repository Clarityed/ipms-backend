package com.clarity.ipmsbackend.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.clarity.ipmsbackend.common.ErrorCode;
import com.clarity.ipmsbackend.constant.InventoryBillConstant;
import com.clarity.ipmsbackend.constant.WarehouseConstant;
import com.clarity.ipmsbackend.exception.BusinessException;
import com.clarity.ipmsbackend.model.dto.inventorybill.otherdeliveryorder.productnum.AddOtherDeliveryOrderProductNumRequest;
import com.clarity.ipmsbackend.model.dto.inventorybill.otherdeliveryorder.productnum.UpdateOtherDeliveryOrderProductNumRequest;
import com.clarity.ipmsbackend.model.dto.inventorybill.otherreceiptorder.productnum.AddOtherReceiptOrderProductNumRequest;
import com.clarity.ipmsbackend.model.dto.inventorybill.otherreceiptorder.productnum.UpdateOtherReceiptOrderProductNumRequest;
import com.clarity.ipmsbackend.model.entity.*;
import com.clarity.ipmsbackend.mapper.IpmsInventoryBillProductNumMapper;
import com.clarity.ipmsbackend.service.IpmsInventoryBillProductNumService;
import com.clarity.ipmsbackend.service.IpmsProductService;
import com.clarity.ipmsbackend.service.IpmsWarehousePositionService;
import com.clarity.ipmsbackend.service.IpmsWarehouseService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.Date;

/**
* @author Clarity
* @description 针对表【ipms_inventory_bill_product_num(库存单据商品数量表)】的数据库操作Service实现
* @createDate 2023-03-27 22:36:45
*/
@Service
public class IpmsInventoryBillProductNumServiceImpl extends ServiceImpl<IpmsInventoryBillProductNumMapper, IpmsInventoryBillProductNum>
    implements IpmsInventoryBillProductNumService{

    @Resource
    private IpmsInventoryBillProductNumMapper ipmsInventoryBillProductNumMapper;

    @Resource
    private IpmsProductService ipmsProductService;

    @Resource
    private IpmsWarehouseService ipmsWarehouseService;

    @Resource
    private IpmsWarehousePositionService ipmsWarehousePositionService;

    @Override
    public long addOtherReceiptOrderProductAndNum(AddOtherReceiptOrderProductNumRequest addOtherReceiptOrderProductNumRequest, IpmsInventoryBill inventoryBill) {
        String inventoryBillType = inventoryBill.getInventoryBillType();
        if (!InventoryBillConstant.OTHER_RECEIPT_ORDER.equals(inventoryBillType)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "单据类型错误");
        }
        BigDecimal productNum = addOtherReceiptOrderProductNumRequest.getProductNum();
        if (productNum == null || productNum.doubleValue() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, inventoryBillType + "商品数量为空或者小于对于 0");
        }
        Long productId = addOtherReceiptOrderProductNumRequest.getProductId();
        if (productId == null || productId <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, inventoryBillType + "商品 id 为空或者不合法");
        }
        Long warehouseId = addOtherReceiptOrderProductNumRequest.getWarehouseId();
        if (warehouseId == null || warehouseId <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, inventoryBillType + "仓库 id 为空或者不合法");
        }
        IpmsProduct product = ipmsProductService.getById(productId);
        if (product == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR, inventoryBillType + "商品不存在");
        }
        IpmsWarehouse warehouse = ipmsWarehouseService.getById(warehouseId);
        if (warehouse == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR, inventoryBillType + "商品仓库不存在");
        }
        Long warehousePositionId = addOtherReceiptOrderProductNumRequest.getWarehousePositionId();
        Integer isWarehousePositionManagement = warehouse.getIsWarehousePositionManagement();
        if (isWarehousePositionManagement == WarehouseConstant.OPEN_WAREHOUSE_POSITION_MANAGEMENT) {
            if (warehousePositionId == null || warehousePositionId <= 0) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR, inventoryBillType + "仓位 id 为空或者不合法");
            }
            IpmsWarehousePosition warehousePosition = ipmsWarehousePositionService.getById(warehousePositionId);
            if (warehousePosition == null) {
                throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR, inventoryBillType + "商品仓位不存在");
            }
        } else {
            if (warehousePositionId != null) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR, inventoryBillType + "存在未开启仓位的仓库输入了仓位 id");
            }
        }
        BigDecimal unitPrice = addOtherReceiptOrderProductNumRequest.getUnitPrice();
        BigDecimal totalPrice = addOtherReceiptOrderProductNumRequest.getTotalPrice();
        if (unitPrice != null || totalPrice != null) {
            if (unitPrice == null) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR, inventoryBillType + "商品单价为空");
            }
            if (totalPrice == null) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR, inventoryBillType + "商品总价为空");
            }
            BigDecimal price = unitPrice.multiply(productNum);
            if (!(price.doubleValue() == totalPrice.doubleValue())) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR, inventoryBillType + "商品价格计算前后端不一致");
            }
        } else {
            addOtherReceiptOrderProductNumRequest.setUnitPrice(BigDecimal.ZERO);
            addOtherReceiptOrderProductNumRequest.setTotalPrice(BigDecimal.ZERO);
        }
        IpmsInventoryBillProductNum inventoryBillProductNum = new IpmsInventoryBillProductNum();
        BeanUtils.copyProperties(addOtherReceiptOrderProductNumRequest, inventoryBillProductNum);
        inventoryBillProductNum.setInventoryBillId(inventoryBill.getInventoryBillId());
        inventoryBillProductNum.setNeedExecutionProductNum(productNum);
        inventoryBillProductNum.setSurplusNeedExecutionProductNum(productNum);
        inventoryBillProductNum.setCreateTime(new Date());
        inventoryBillProductNum.setUpdateTime(new Date());
        int result = ipmsInventoryBillProductNumMapper.insert(inventoryBillProductNum);
        if (result != 1) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, inventoryBillType + "商品插入失败");
        }
        return inventoryBillProductNum.getInventoryBillProductId();
    }

    @Override
    public long addOtherDeliveryOrderProductAndNum(AddOtherDeliveryOrderProductNumRequest addOtherDeliveryOrderProductNumRequest, IpmsInventoryBill inventoryBill) {
        String inventoryBillType = inventoryBill.getInventoryBillType();
        if (!InventoryBillConstant.OTHER_DELIVERY_ORDER.equals(inventoryBillType)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "单据类型错误");
        }
        BigDecimal productNum = addOtherDeliveryOrderProductNumRequest.getProductNum();
        if (productNum == null || productNum.doubleValue() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, inventoryBillType + "商品数量为空或者小于对于 0");
        }
        Long productId = addOtherDeliveryOrderProductNumRequest.getProductId();
        if (productId == null || productId <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, inventoryBillType + "商品 id 为空或者不合法");
        }
        Long warehouseId = addOtherDeliveryOrderProductNumRequest.getWarehouseId();
        if (warehouseId == null || warehouseId <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, inventoryBillType + "仓库 id 为空或者不合法");
        }
        IpmsProduct product = ipmsProductService.getById(productId);
        if (product == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR, inventoryBillType + "商品不存在");
        }
        IpmsWarehouse warehouse = ipmsWarehouseService.getById(warehouseId);
        if (warehouse == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR, inventoryBillType + "商品仓库不存在");
        }
        Long warehousePositionId = addOtherDeliveryOrderProductNumRequest.getWarehousePositionId();
        Integer isWarehousePositionManagement = warehouse.getIsWarehousePositionManagement();
        if (isWarehousePositionManagement == WarehouseConstant.OPEN_WAREHOUSE_POSITION_MANAGEMENT) {
            if (warehousePositionId == null || warehousePositionId <= 0) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR, inventoryBillType + "仓位 id 为空或者不合法");
            }
            IpmsWarehousePosition warehousePosition = ipmsWarehousePositionService.getById(warehousePositionId);
            if (warehousePosition == null) {
                throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR, inventoryBillType + "商品仓位不存在");
            }
        } else {
            if (warehousePositionId != null) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR, inventoryBillType + "存在未开启仓位的仓库输入了仓位 id");
            }
        }
        BigDecimal unitPrice = addOtherDeliveryOrderProductNumRequest.getUnitPrice();
        BigDecimal totalPrice = addOtherDeliveryOrderProductNumRequest.getTotalPrice();
        if (unitPrice != null || totalPrice != null) {
            if (unitPrice == null) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR, inventoryBillType + "商品单价为空");
            }
            if (totalPrice == null) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR, inventoryBillType + "商品总价为空");
            }
            BigDecimal price = unitPrice.multiply(productNum);
            if (!(price.doubleValue() == totalPrice.doubleValue())) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR, inventoryBillType + "商品价格计算前后端不一致");
            }
        } else {
            addOtherDeliveryOrderProductNumRequest.setUnitPrice(BigDecimal.ZERO);
            addOtherDeliveryOrderProductNumRequest.setTotalPrice(BigDecimal.ZERO);
        }
        IpmsInventoryBillProductNum inventoryBillProductNum = new IpmsInventoryBillProductNum();
        BeanUtils.copyProperties(addOtherDeliveryOrderProductNumRequest, inventoryBillProductNum);
        inventoryBillProductNum.setInventoryBillId(inventoryBill.getInventoryBillId());
        inventoryBillProductNum.setNeedExecutionProductNum(productNum);
        inventoryBillProductNum.setSurplusNeedExecutionProductNum(productNum);
        inventoryBillProductNum.setCreateTime(new Date());
        inventoryBillProductNum.setUpdateTime(new Date());
        int result = ipmsInventoryBillProductNumMapper.insert(inventoryBillProductNum);
        if (result != 1) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, inventoryBillType + "商品插入失败");
        }
        return inventoryBillProductNum.getInventoryBillProductId();
    }

    @Override
    public int updateOtherReceiptOrderProductAndNum(UpdateOtherReceiptOrderProductNumRequest updateOtherReceiptOrderProductNumRequest, IpmsInventoryBill inventoryBill) {
        String inventoryBillType = inventoryBill.getInventoryBillType();
        Long inventoryBillProductId = updateOtherReceiptOrderProductNumRequest.getInventoryBillProductId();
        if (inventoryBillProductId == null || inventoryBillProductId <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, inventoryBillType + "商品 id 为空或者不合法");
        }
        IpmsInventoryBillProductNum inventoryBillProduct = ipmsInventoryBillProductNumMapper.selectById(inventoryBillProductId);
        if (inventoryBillProduct == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "库存单据商品不存在");
        }
        Long productId = updateOtherReceiptOrderProductNumRequest.getProductId();
        if (productId != null && productId > 0) {
            IpmsProduct product = ipmsProductService.getById(productId);
            if (product == null) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "商品不存在");
            }
        }
        if (productId != null && productId < 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "商品 id 不合法");
        }
        // 仓库是否为空
        Long warehouseId = updateOtherReceiptOrderProductNumRequest.getWarehouseId();
        Long warehousePositionId = updateOtherReceiptOrderProductNumRequest.getWarehousePositionId();
        if (warehouseId != null && warehouseId > 0) {
            IpmsWarehouse warehouse = ipmsWarehouseService.getById(warehouseId);
            // 仓位为空抛出异常
            if (warehouse == null) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "仓库不存在");
            }
            // 如果仓位不为空，判断是否有开启仓位管理
            Integer isWarehousePositionManagement = warehouse.getIsWarehousePositionManagement();
            if (isWarehousePositionManagement == WarehouseConstant.OPEN_WAREHOUSE_POSITION_MANAGEMENT) {
                // 开启仓位管理必须要有仓位 id
                if (warehousePositionId != null) {
                    IpmsWarehousePosition warehousePosition = ipmsWarehousePositionService.getById(warehousePositionId);
                    if (warehousePosition == null) {
                        throw new BusinessException(ErrorCode.PARAMS_ERROR, "仓位不存在");
                    }
                }
            } else {
                if (warehousePositionId != null) {
                    throw new BusinessException(ErrorCode.PARAMS_ERROR, inventoryBillType + "的商品仓库未开启仓位管理，无法修改仓位");
                }
            }
        } else {
            if (warehousePositionId != null) {
                IpmsWarehousePosition warehousePosition = ipmsWarehousePositionService.getById(warehousePositionId);
                if (warehousePosition == null) {
                    throw new BusinessException(ErrorCode.PARAMS_ERROR, "仓位不存在");
                }
            }
        }
        if (warehouseId != null && warehouseId < 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, inventoryBillType + "商品仓库 id 不合法");
        }
        if (warehousePositionId != null && warehousePositionId < 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, inventoryBillType +  "商品仓位 id 不合法");
        }
        BigDecimal productNum = updateOtherReceiptOrderProductNumRequest.getProductNum();
        if (productNum != null) {
            if (productNum.doubleValue() <= 0) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR, inventoryBillType + "商品数量不能等于 0");
            }
        }
        BigDecimal unitPrice = updateOtherReceiptOrderProductNumRequest.getUnitPrice();
        BigDecimal totalPrice = updateOtherReceiptOrderProductNumRequest.getTotalPrice();
        if (unitPrice != null || totalPrice != null) {
            if (unitPrice == null) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR, inventoryBillType + "商品单价为空");
            }
            if (totalPrice == null) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR, inventoryBillType + "商品总价为空");
            }
            BigDecimal tempNum;
            if (productNum != null) {
                tempNum = productNum;
            } else {
                tempNum = inventoryBillProduct.getNeedExecutionProductNum();
            }
            BigDecimal price = unitPrice.multiply(tempNum);
            if (!(price.doubleValue() == totalPrice.doubleValue())) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR, inventoryBillType + "商品价格计算前后端不一致");
            }
        } else {
            updateOtherReceiptOrderProductNumRequest.setUnitPrice(BigDecimal.ZERO);
            updateOtherReceiptOrderProductNumRequest.setTotalPrice(BigDecimal.ZERO);
        }
        IpmsInventoryBillProductNum inventoryBillProductNum = new IpmsInventoryBillProductNum();
        BeanUtils.copyProperties(updateOtherReceiptOrderProductNumRequest, inventoryBillProductNum);
        inventoryBillProductNum.setNeedExecutionProductNum(productNum);
        inventoryBillProductNum.setSurplusNeedExecutionProductNum(productNum);
        inventoryBillProductNum.setUpdateTime(new Date());
        int result = ipmsInventoryBillProductNumMapper.updateById(inventoryBillProductNum);
        if (result != 1) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, inventoryBillType + "商品更新失败");
        }
        return result;
    }

    @Override
    public int updateOtherDeliveryOrderProductAndNum(UpdateOtherDeliveryOrderProductNumRequest updateOtherDeliveryOrderProductNumRequest, IpmsInventoryBill inventoryBill) {
        String inventoryBillType = inventoryBill.getInventoryBillType();
        Long inventoryBillProductId = updateOtherDeliveryOrderProductNumRequest.getInventoryBillProductId();
        if (inventoryBillProductId == null || inventoryBillProductId <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, inventoryBillType + "商品 id 为空或者不合法");
        }
        IpmsInventoryBillProductNum inventoryBillProduct = ipmsInventoryBillProductNumMapper.selectById(inventoryBillProductId);
        if (inventoryBillProduct == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "库存单据商品不存在");
        }
        Long productId = updateOtherDeliveryOrderProductNumRequest.getProductId();
        if (productId != null && productId > 0) {
            IpmsProduct product = ipmsProductService.getById(productId);
            if (product == null) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "商品不存在");
            }
        }
        if (productId != null && productId < 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "商品 id 不合法");
        }
        // 仓库是否为空
        Long warehouseId = updateOtherDeliveryOrderProductNumRequest.getWarehouseId();
        Long warehousePositionId = updateOtherDeliveryOrderProductNumRequest.getWarehousePositionId();
        if (warehouseId != null && warehouseId > 0) {
            IpmsWarehouse warehouse = ipmsWarehouseService.getById(warehouseId);
            // 仓位为空抛出异常
            if (warehouse == null) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "仓库不存在");
            }
            // 如果仓位不为空，判断是否有开启仓位管理
            Integer isWarehousePositionManagement = warehouse.getIsWarehousePositionManagement();
            if (isWarehousePositionManagement == WarehouseConstant.OPEN_WAREHOUSE_POSITION_MANAGEMENT) {
                // 开启仓位管理必须要有仓位 id
                if (warehousePositionId != null) {
                    IpmsWarehousePosition warehousePosition = ipmsWarehousePositionService.getById(warehousePositionId);
                    if (warehousePosition == null) {
                        throw new BusinessException(ErrorCode.PARAMS_ERROR, "仓位不存在");
                    }
                }
            } else {
                if (warehousePositionId != null) {
                    throw new BusinessException(ErrorCode.PARAMS_ERROR, inventoryBillType + "的商品仓库未开启仓位管理，无法修改仓位");
                }
            }
        } else {
            if (warehousePositionId != null) {
                IpmsWarehousePosition warehousePosition = ipmsWarehousePositionService.getById(warehousePositionId);
                if (warehousePosition == null) {
                    throw new BusinessException(ErrorCode.PARAMS_ERROR, "仓位不存在");
                }
            }
        }
        if (warehouseId != null && warehouseId < 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, inventoryBillType + "商品仓库 id 不合法");
        }
        if (warehousePositionId != null && warehousePositionId < 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, inventoryBillType +  "商品仓位 id 不合法");
        }
        BigDecimal productNum = updateOtherDeliveryOrderProductNumRequest.getProductNum();
        if (productNum != null) {
            if (productNum.doubleValue() <= 0) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR, inventoryBillType + "商品数量不能等于 0");
            }
        }
        BigDecimal unitPrice = updateOtherDeliveryOrderProductNumRequest.getUnitPrice();
        BigDecimal totalPrice = updateOtherDeliveryOrderProductNumRequest.getTotalPrice();
        if (unitPrice != null || totalPrice != null) {
            if (unitPrice == null) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR, inventoryBillType + "商品单价为空");
            }
            if (totalPrice == null) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR, inventoryBillType + "商品总价为空");
            }
            BigDecimal tempNum;
            if (productNum != null) {
                tempNum = productNum;
            } else {
                tempNum = inventoryBillProduct.getNeedExecutionProductNum();
            }
            BigDecimal price = unitPrice.multiply(tempNum);
            if (!(price.doubleValue() == totalPrice.doubleValue())) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR, inventoryBillType + "商品价格计算前后端不一致");
            }
        } else {
            updateOtherDeliveryOrderProductNumRequest.setUnitPrice(BigDecimal.ZERO);
            updateOtherDeliveryOrderProductNumRequest.setTotalPrice(BigDecimal.ZERO);
        }
        IpmsInventoryBillProductNum inventoryBillProductNum = new IpmsInventoryBillProductNum();
        BeanUtils.copyProperties(updateOtherDeliveryOrderProductNumRequest, inventoryBillProductNum);
        inventoryBillProductNum.setNeedExecutionProductNum(productNum);
        inventoryBillProductNum.setSurplusNeedExecutionProductNum(productNum);
        inventoryBillProductNum.setUpdateTime(new Date());
        int result = ipmsInventoryBillProductNumMapper.updateById(inventoryBillProductNum);
        if (result != 1) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, inventoryBillType + "商品更新失败");
        }
        return result;
    }
}




