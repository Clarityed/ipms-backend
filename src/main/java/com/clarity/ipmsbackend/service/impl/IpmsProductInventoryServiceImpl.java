package com.clarity.ipmsbackend.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.clarity.ipmsbackend.common.ErrorCode;
import com.clarity.ipmsbackend.constant.WarehouseConstant;
import com.clarity.ipmsbackend.exception.BusinessException;
import com.clarity.ipmsbackend.model.entity.IpmsProductInventory;
import com.clarity.ipmsbackend.mapper.IpmsProductInventoryMapper;
import com.clarity.ipmsbackend.model.entity.IpmsPurchaseBillProductNum;
import com.clarity.ipmsbackend.model.entity.IpmsWarehouse;
import com.clarity.ipmsbackend.service.IpmsProductInventoryService;
import com.clarity.ipmsbackend.service.IpmsWarehouseService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Date;

/**
 * @author Clarity
 * @description 针对表【ipms_product_inventory(商品库存表)】的数据库操作Service实现
 * @createDate 2023-03-13 15:58:53
 */
@Service
public class IpmsProductInventoryServiceImpl extends ServiceImpl<IpmsProductInventoryMapper, IpmsProductInventory>
        implements IpmsProductInventoryService {

    @Resource
    private IpmsProductInventoryMapper ipmsProductInventoryMapper;

    @Resource
    private IpmsWarehouseService ipmsWarehouseService;

    @Override
    public int addProductInventory(IpmsPurchaseBillProductNum purchaseBillProductNum) {
        Long productId = purchaseBillProductNum.getProductId();
        Long warehouseId = purchaseBillProductNum.getWarehouseId();
        Long warehousePositionId = purchaseBillProductNum.getWarehousePositionId();
        BigDecimal needWarehousingProductNum = purchaseBillProductNum.getNeedWarehousingProductNum();
        BigDecimal unitPrice = purchaseBillProductNum.getUnitPrice();
        IpmsProductInventory productInventory = new IpmsProductInventory();
        QueryWrapper<IpmsProductInventory> productInventoryQueryWrapper = new QueryWrapper<>();
        productInventoryQueryWrapper.eq("product_id", productId);
        productInventoryQueryWrapper.eq("warehouse_id", warehouseId);
        if (warehousePositionId != null && warehousePositionId > 0) {
            productInventoryQueryWrapper.eq("warehouse_position_id", warehousePositionId);
        }
        IpmsProductInventory oldProductInventory = ipmsProductInventoryMapper.selectOne(productInventoryQueryWrapper);
        if (oldProductInventory != null) {
            // 这里可以判断数据库已经有这条数据了，那么我们更新这条数据就好了
            // 增加商品总成本，只会增加或者不变
            BigDecimal oldProductInventoryTotalCost = oldProductInventory.getProductInventoryTotalCost();
            BigDecimal newProductInventoryTotalCost = oldProductInventoryTotalCost.add(unitPrice.multiply(needWarehousingProductNum));
            productInventory.setProductInventoryTotalCost(newProductInventoryTotalCost);
            // 增加商品成本，会增加和减少
            BigDecimal oldProductInventoryCost = oldProductInventory.getProductInventoryCost();
            BigDecimal newProductInventoryCost = oldProductInventoryCost.add(unitPrice.multiply(needWarehousingProductNum));
            productInventory.setProductInventoryCost(newProductInventoryCost);
            // 增加商品总量，只会增加或者不变
            BigDecimal oldProductInventoryTotalNum = oldProductInventory.getProductInventoryTotalNum();
            BigDecimal newProductInventoryTotalNum = oldProductInventoryTotalNum.add(needWarehousingProductNum);
            productInventory.setProductInventoryTotalNum(newProductInventoryTotalNum);
            // 增加商品剩余数量，会增加和减少
            BigDecimal oldProductInventorySurplusNum = oldProductInventory.getProductInventorySurplusNum();
            BigDecimal newProductInventorySurplusNum = oldProductInventorySurplusNum.add(needWarehousingProductNum);
            productInventory.setProductInventorySurplusNum(newProductInventorySurplusNum);
            // 商品单位成本 = 成本 / 商品剩余数量
            BigDecimal newProductInventoryUnitCost = newProductInventoryCost.divide(newProductInventorySurplusNum, 2, RoundingMode.HALF_UP);
            productInventory.setProductInventoryUnitCost(newProductInventoryUnitCost);
            productInventory.setUpdateTime(new Date());
            productInventory.setProductInventoryId(oldProductInventory.getProductInventoryId());
            int result = ipmsProductInventoryMapper.updateById(productInventory);
            if (result != 1) {
                throw new BusinessException(ErrorCode.SYSTEM_ERROR, "增加库存数量失败");
            }
            return result;
        } else {
            productInventory = new IpmsProductInventory();
            productInventory.setWarehouseId(warehouseId);
            if (warehousePositionId != null && warehousePositionId > 0) {
                productInventory.setWarehousePositionId(warehousePositionId);
            }
            productInventory.setProductId(productId);
            productInventory.setProductInventoryTotalNum(needWarehousingProductNum);
            productInventory.setProductInventorySurplusNum(needWarehousingProductNum);
            productInventory.setProductInventoryUnitCost(unitPrice);
            BigDecimal cost = unitPrice.multiply(needWarehousingProductNum);
            productInventory.setProductInventoryCost(cost);
            productInventory.setProductInventoryTotalCost(cost);
            productInventory.setCreateTime(new Date());
            productInventory.setUpdateTime(new Date());
            int result = ipmsProductInventoryMapper.insert(productInventory);
            if (result != 1) {
                throw new BusinessException(ErrorCode.SYSTEM_ERROR, "库存插入失败");
            }
            return result;
        }
    }

    @Override
    public int reduceProductInventory(IpmsPurchaseBillProductNum purchaseBillProductNum) {
        Long productId = purchaseBillProductNum.getProductId();
        Long warehouseId = purchaseBillProductNum.getWarehouseId();
        Long warehousePositionId = purchaseBillProductNum.getWarehousePositionId();
        BigDecimal needWarehousingProductNum = purchaseBillProductNum.getNeedWarehousingProductNum();
        BigDecimal unitPrice = purchaseBillProductNum.getUnitPrice();
        QueryWrapper<IpmsProductInventory> productInventoryQueryWrapper = new QueryWrapper<>();
        productInventoryQueryWrapper.eq("product_id", productId);
        productInventoryQueryWrapper.eq("warehouse_id", warehouseId);
        if (warehousePositionId != null && warehousePositionId > 0) {
            productInventoryQueryWrapper.eq("warehouse_position_id", warehousePositionId);
        }
        IpmsProductInventory oldProductInventory = ipmsProductInventoryMapper.selectOne(productInventoryQueryWrapper);
        if (oldProductInventory == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "扣减的商品不存在");
        }
        IpmsProductInventory productInventory = new IpmsProductInventory();
        // 商品总量和商品总成本不变，这两值是记录，从系统开始使用，进入库存数量和成本
        // 目前的库存商品成本减少
        BigDecimal oldProductInventoryCost = oldProductInventory.getProductInventoryCost();
        BigDecimal newProductInventoryCost = oldProductInventoryCost.subtract(unitPrice.multiply(needWarehousingProductNum));
        productInventory.setProductInventoryCost(newProductInventoryCost);
        // 目前的库存商品数量减少
        BigDecimal oldProductInventorySurplusNum = oldProductInventory.getProductInventorySurplusNum();
        BigDecimal newProductInventorySurplusNum = oldProductInventorySurplusNum.subtract(needWarehousingProductNum);
        productInventory.setProductInventorySurplusNum(newProductInventorySurplusNum);
        if (newProductInventorySurplusNum.doubleValue() < 0) {
            IpmsWarehouse warehouse = ipmsWarehouseService.getById(warehouseId);
            Integer isNegativeInventory = warehouse.getIsNegativeInventory();
            if (WarehouseConstant.CLOSE_WAREHOUSE_POSITION_MANAGEMENT == isNegativeInventory) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "存在商品数量库存不足，请求修改商品数量，或者开启仓位支持负库存模式");
            }
        }
        // 目前的库存商品单位成本改变
        if (newProductInventorySurplusNum.doubleValue() == 0) {
            productInventory.setProductInventoryUnitCost(BigDecimal.ZERO);
        } else {
            productInventory.setProductInventoryUnitCost(newProductInventoryCost.divide(newProductInventorySurplusNum, 2, RoundingMode.HALF_UP));
        }
        productInventory.setUpdateTime(new Date());
        productInventory.setProductInventoryId(oldProductInventory.getProductInventoryId());
        int result = ipmsProductInventoryMapper.updateById(productInventory);
        if (result != 1) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "减少库存数量失败");
        }
        return result;
    }
}




