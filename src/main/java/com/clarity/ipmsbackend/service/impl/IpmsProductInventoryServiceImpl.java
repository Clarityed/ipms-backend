package com.clarity.ipmsbackend.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.clarity.ipmsbackend.common.Constant;
import com.clarity.ipmsbackend.common.ErrorCode;
import com.clarity.ipmsbackend.constant.WarehouseConstant;
import com.clarity.ipmsbackend.exception.BusinessException;
import com.clarity.ipmsbackend.mapper.IpmsProductInventoryMapper;
import com.clarity.ipmsbackend.model.entity.*;
import com.clarity.ipmsbackend.model.vo.SafeUserVO;
import com.clarity.ipmsbackend.service.*;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Date;
import java.util.List;

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

    @Resource
    private IpmsProductService ipmsProductService;

    @Resource
    private IpmsWarehousePositionService ipmsWarehousePositionService;

    @Resource
    private IpmsUserService ipmsUserService;

    @Override
    public BigDecimal addProductInventory(IpmsPurchaseBillProductNum purchaseBillProductNum, BigDecimal purchaseBillExchangeRate) {
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
            // 增加商品成本，会增加和减少
            BigDecimal oldProductInventoryCost = oldProductInventory.getProductInventoryCost();
            BigDecimal currentPurchaseProductCost = unitPrice.multiply(needWarehousingProductNum);
            // BigDecimal newProductInventoryCost = oldProductInventoryCost.add(currentPurchaseProductCost.multiply(purchaseBillExchangeRate));
            BigDecimal newProductInventoryCost = oldProductInventoryCost.add(currentPurchaseProductCost);
            productInventory.setProductInventoryCost(newProductInventoryCost);
            // 增加商品剩余数量，会增加和减少
            BigDecimal oldProductInventorySurplusNum = oldProductInventory.getProductInventorySurplusNum();
            BigDecimal newProductInventorySurplusNum = oldProductInventorySurplusNum.add(needWarehousingProductNum);
            productInventory.setProductInventorySurplusNum(newProductInventorySurplusNum);
            // 商品单位成本 = 成本 / 商品剩余数量
            BigDecimal newProductInventoryUnitCost = newProductInventoryCost.divide(newProductInventorySurplusNum, 2, RoundingMode.HALF_UP);
            productInventory.setProductInventoryUnitCost(newProductInventoryUnitCost);
            productInventory.setUpdateTime(new Date());
            productInventory.setProductInventoryId(oldProductInventory.getProductInventoryId());
            // 不会为 0 的商品平均单位成本
            productInventory.setProductUnitCost(newProductInventoryUnitCost);
            int result = ipmsProductInventoryMapper.updateById(productInventory);
            if (result != 1) {
                throw new BusinessException(ErrorCode.SYSTEM_ERROR, "增加库存数量失败");
            }
            return currentPurchaseProductCost;
        } else {
            productInventory = new IpmsProductInventory();
            productInventory.setWarehouseId(warehouseId);
            if (warehousePositionId != null && warehousePositionId > 0) {
                productInventory.setWarehousePositionId(warehousePositionId);
            }
            productInventory.setProductId(productId);
            productInventory.setProductInventorySurplusNum(needWarehousingProductNum);
            productInventory.setProductInventoryUnitCost(unitPrice);
            BigDecimal cost = unitPrice.multiply(needWarehousingProductNum);
            // productInventory.setProductInventoryCost(cost.multiply(purchaseBillExchangeRate));
            productInventory.setProductInventoryCost(cost);
            productInventory.setCreateTime(new Date());
            productInventory.setUpdateTime(new Date());
            // 不会为 0 的商品平均单位成本
            productInventory.setProductUnitCost(cost);
            int result = ipmsProductInventoryMapper.insert(productInventory);
            if (result != 1) {
                throw new BusinessException(ErrorCode.SYSTEM_ERROR, "库存插入失败");
            }
            return cost;
        }
    }

    @Override
    public BigDecimal reduceProductInventory(IpmsPurchaseBillProductNum purchaseBillProductNum, BigDecimal purchaseBillExchangeRate) {
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
        BigDecimal currentPurchaseProductCost = unitPrice.multiply(needWarehousingProductNum);
        // BigDecimal newProductInventoryCost = oldProductInventoryCost.subtract(currentPurchaseProductCost.multiply(purchaseBillExchangeRate));
        BigDecimal newProductInventoryCost = oldProductInventoryCost.subtract(currentPurchaseProductCost);
        productInventory.setProductInventoryCost(newProductInventoryCost);
        // 不会为 0 的商品平均单位成本

        // 目前的库存商品数量减少
        BigDecimal oldProductInventorySurplusNum = oldProductInventory.getProductInventorySurplusNum();
        BigDecimal newProductInventorySurplusNum = oldProductInventorySurplusNum.subtract(needWarehousingProductNum);
        productInventory.setProductInventorySurplusNum(newProductInventorySurplusNum);
        if (newProductInventorySurplusNum.doubleValue() < 0) {
            IpmsWarehouse warehouse = ipmsWarehouseService.getById(warehouseId);
            Integer isNegativeInventory = warehouse.getIsNegativeInventory();
            if (WarehouseConstant.CLOSE_NEGATIVE_INVENTORY == isNegativeInventory) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "存在商品数量库存不足，请求修改商品数量，或者开启仓位支持负库存模式");
            }
        }
        // 目前的库存商品单位成本改变
        if (newProductInventorySurplusNum.doubleValue() == 0) {
            productInventory.setProductInventoryUnitCost(BigDecimal.ZERO);
            productInventory.setProductUnitCost(BigDecimal.ZERO);
        } else {
            productInventory.setProductInventoryUnitCost(newProductInventoryCost.divide(newProductInventorySurplusNum, 2, RoundingMode.HALF_UP));
            productInventory.setProductUnitCost(newProductInventoryCost.divide(newProductInventorySurplusNum, 2, RoundingMode.HALF_UP));
        }
        productInventory.setUpdateTime(new Date());
        productInventory.setProductInventoryId(oldProductInventory.getProductInventoryId());
        int result = ipmsProductInventoryMapper.updateById(productInventory);
        if (result != 1) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "减少库存数量失败");
        }
        return currentPurchaseProductCost;
    }

    @Override
    public BigDecimal addProductInventory(IpmsSaleBillProductNum saleBillProductNum, BigDecimal saleBillExchangeRate) {
        Long productId = saleBillProductNum.getProductId();
        Long warehouseId = saleBillProductNum.getWarehouseId();
        Long warehousePositionId = saleBillProductNum.getWarehousePositionId();
        BigDecimal needDeliveryProductNum = saleBillProductNum.getNeedDeliveryProductNum();
        BigDecimal unitPrice = saleBillProductNum.getUnitPrice();
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
            // 增加商品成本，会增加和减少
            BigDecimal oldProductInventoryCost = oldProductInventory.getProductInventoryCost();
            BigDecimal currentSaleProductCost = unitPrice.multiply(needDeliveryProductNum);
            // BigDecimal newProductInventoryCost = oldProductInventoryCost.add(currentSaleProductCost.multiply(saleBillExchangeRate));
            BigDecimal newProductInventoryCost = oldProductInventoryCost.add(currentSaleProductCost);
            productInventory.setProductInventoryCost(newProductInventoryCost);
            // 增加商品剩余数量，会增加和减少
            BigDecimal oldProductInventorySurplusNum = oldProductInventory.getProductInventorySurplusNum();
            BigDecimal newProductInventorySurplusNum = oldProductInventorySurplusNum.add(needDeliveryProductNum);
            productInventory.setProductInventorySurplusNum(newProductInventorySurplusNum);
            // 商品单位成本 = 成本 / 商品剩余数量
            BigDecimal newProductInventoryUnitCost = newProductInventoryCost.divide(newProductInventorySurplusNum, 2, RoundingMode.HALF_UP);
            productInventory.setProductInventoryUnitCost(newProductInventoryUnitCost);
            productInventory.setUpdateTime(new Date());
            productInventory.setProductInventoryId(oldProductInventory.getProductInventoryId());
            // 不会为 0 的商品平均单位成本
            productInventory.setProductUnitCost(newProductInventoryUnitCost);
            int result = ipmsProductInventoryMapper.updateById(productInventory);
            if (result != 1) {
                throw new BusinessException(ErrorCode.SYSTEM_ERROR, "增加库存数量失败");
            }
            return currentSaleProductCost;
        } else {
            productInventory = new IpmsProductInventory();
            productInventory.setWarehouseId(warehouseId);
            if (warehousePositionId != null && warehousePositionId > 0) {
                productInventory.setWarehousePositionId(warehousePositionId);
            }
            productInventory.setProductId(productId);
            productInventory.setProductInventorySurplusNum(needDeliveryProductNum);
            productInventory.setProductInventoryUnitCost(unitPrice);
            BigDecimal cost = unitPrice.multiply(needDeliveryProductNum);
            // productInventory.setProductInventoryCost(cost.multiply(saleBillExchangeRate));
            productInventory.setProductInventoryCost(cost);
            productInventory.setCreateTime(new Date());
            productInventory.setUpdateTime(new Date());
            productInventory.setProductUnitCost(cost);
            int result = ipmsProductInventoryMapper.insert(productInventory);
            if (result != 1) {
                throw new BusinessException(ErrorCode.SYSTEM_ERROR, "库存插入失败");
            }
            return cost;
        }
    }

    @Override
    public BigDecimal reduceProductInventory(IpmsSaleBillProductNum saleBillProductNum, BigDecimal saleBillExchangeRate) {
        Long productId = saleBillProductNum.getProductId();
        Long warehouseId = saleBillProductNum.getWarehouseId();
        Long warehousePositionId = saleBillProductNum.getWarehousePositionId();
        BigDecimal needDeliveryProductNum = saleBillProductNum.getNeedDeliveryProductNum();
        BigDecimal unitPrice = saleBillProductNum.getUnitPrice();
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
        BigDecimal currentSaleProductCost = unitPrice.multiply(needDeliveryProductNum);
        // BigDecimal newProductInventoryCost = oldProductInventoryCost.subtract(currentSaleProductCost.multiply(saleBillExchangeRate));
        BigDecimal newProductInventoryCost = oldProductInventoryCost.subtract(currentSaleProductCost);
        productInventory.setProductInventoryCost(newProductInventoryCost);
        // 目前的库存商品数量减少
        BigDecimal oldProductInventorySurplusNum = oldProductInventory.getProductInventorySurplusNum();
        BigDecimal newProductInventorySurplusNum = oldProductInventorySurplusNum.subtract(needDeliveryProductNum);
        productInventory.setProductInventorySurplusNum(newProductInventorySurplusNum);
        if (newProductInventorySurplusNum.doubleValue() < 0) {
            IpmsWarehouse warehouse = ipmsWarehouseService.getById(warehouseId);
            Integer isNegativeInventory = warehouse.getIsNegativeInventory();
            if (WarehouseConstant.OPEN_NEGATIVE_INVENTORY == isNegativeInventory) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "存在商品数量库存不足，请求修改商品数量，或者开启仓位支持负库存模式");
            }
        }
        // 目前的库存商品单位成本改变
        if (newProductInventorySurplusNum.doubleValue() == 0) {
            productInventory.setProductInventoryUnitCost(BigDecimal.ZERO);
            productInventory.setProductUnitCost(BigDecimal.ZERO);
        } else {
            productInventory.setProductInventoryUnitCost(newProductInventoryCost.divide(newProductInventorySurplusNum, 2, RoundingMode.HALF_UP));
            productInventory.setProductUnitCost(newProductInventoryCost.divide(newProductInventorySurplusNum, 2, RoundingMode.HALF_UP));
        }
        productInventory.setUpdateTime(new Date());
        productInventory.setProductInventoryId(oldProductInventory.getProductInventoryId());
        int result = ipmsProductInventoryMapper.updateById(productInventory);
        if (result != 1) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "减少库存数量失败");
        }
        return currentSaleProductCost;
    }

    @Override
    public int addProductInventory(IpmsProductionBillProductNum productionBillProductNum, List<IpmsProductionBillProductNum> taskOrderProductList) {
        Long productId = productionBillProductNum.getProductId();
        Long warehouseId = productionBillProductNum.getWarehouseId();
        Long warehousePositionId = productionBillProductNum.getWarehousePositionId();
        BigDecimal needExecutionProductNum = productionBillProductNum.getNeedExecutionProductNum();
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
            // 增加商品成本，会增加和减少
            BigDecimal oldProductInventoryCost = oldProductInventory.getProductInventoryCost();
            BigDecimal productInventoryUnitCost = oldProductInventory.getProductInventoryUnitCost();
            BigDecimal currentPurchaseProductCost = productInventoryUnitCost.multiply(needExecutionProductNum);
            // BigDecimal newProductInventoryCost = oldProductInventoryCost.add(currentPurchaseProductCost.multiply(purchaseBillExchangeRate));
            BigDecimal newProductInventoryCost = oldProductInventoryCost.add(currentPurchaseProductCost);
            productInventory.setProductInventoryCost(newProductInventoryCost);
            // 增加商品剩余数量，会增加和减少
            BigDecimal oldProductInventorySurplusNum = oldProductInventory.getProductInventorySurplusNum();
            BigDecimal newProductInventorySurplusNum = oldProductInventorySurplusNum.add(needExecutionProductNum);
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
            // 这里的增加只存在新的生产任务单生产的产品
            double tempUnitPrice = 0;
            for (IpmsProductionBillProductNum taskOrderProduct : taskOrderProductList) {
                BigDecimal productMaterialMole = taskOrderProduct.getProductMaterialMole();
                productInventoryQueryWrapper = new QueryWrapper<>();
                productInventoryQueryWrapper.eq("product_id", taskOrderProduct.getProductId());
                productInventoryQueryWrapper.eq("warehouse_id", taskOrderProduct.getWarehouseId());
                if (taskOrderProduct.getWarehousePositionId() != null && taskOrderProduct.getWarehousePositionId() > 0) {
                    productInventoryQueryWrapper.eq("warehouse_position_id", taskOrderProduct.getWarehousePositionId());
                }
                IpmsProductInventory taskOrderProductInventory = ipmsProductInventoryMapper.selectOne(productInventoryQueryWrapper);
                BigDecimal productInventoryUnitCost = taskOrderProductInventory.getProductInventoryUnitCost();
                tempUnitPrice += productMaterialMole.multiply(productInventoryUnitCost).doubleValue();
            }
            productInventory = new IpmsProductInventory();
            productInventory.setWarehouseId(warehouseId);
            if (warehousePositionId != null && warehousePositionId > 0) {
                productInventory.setWarehousePositionId(warehousePositionId);
            }
            productInventory.setProductId(productId);
            productInventory.setProductInventorySurplusNum(needExecutionProductNum);
            BigDecimal unitPrice = new BigDecimal(String.valueOf(tempUnitPrice));
            productInventory.setProductInventoryUnitCost(unitPrice);
            BigDecimal cost = unitPrice.multiply(needExecutionProductNum);
            // productInventory.setProductInventoryCost(cost.multiply(purchaseBillExchangeRate));
            productInventory.setProductInventoryCost(cost);
            productInventory.setProductUnitCost(unitPrice);
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
    public int reduceProductInventory(IpmsProductionBillProductNum productionBillProductNum) {
        Long productId = productionBillProductNum.getProductId();
        Long warehouseId = productionBillProductNum.getWarehouseId();
        Long warehousePositionId = productionBillProductNum.getWarehousePositionId();
        BigDecimal needExecutionProductNum = productionBillProductNum.getNeedExecutionProductNum();
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
        BigDecimal productUnitCost = oldProductInventory.getProductUnitCost();
        BigDecimal currentSaleProductCost = productUnitCost.multiply(needExecutionProductNum);
        // BigDecimal newProductInventoryCost = oldProductInventoryCost.subtract(currentSaleProductCost.multiply(saleBillExchangeRate));
        BigDecimal newProductInventoryCost = oldProductInventoryCost.subtract(currentSaleProductCost);
        productInventory.setProductInventoryCost(newProductInventoryCost);
        // 目前的库存商品数量减少
        BigDecimal oldProductInventorySurplusNum = oldProductInventory.getProductInventorySurplusNum();
        BigDecimal newProductInventorySurplusNum = oldProductInventorySurplusNum.subtract(needExecutionProductNum);
        productInventory.setProductInventorySurplusNum(newProductInventorySurplusNum);
        if (newProductInventorySurplusNum.doubleValue() < 0) {
            IpmsWarehouse warehouse = ipmsWarehouseService.getById(warehouseId);
            Integer isNegativeInventory = warehouse.getIsNegativeInventory();
            if (WarehouseConstant.CLOSE_NEGATIVE_INVENTORY == isNegativeInventory) {
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

    @Override
    public int addProductInventory(IpmsInventoryBillProductNum inventoryBillProductNum) {
        Long productId = inventoryBillProductNum.getProductId();
        Long warehouseId = inventoryBillProductNum.getWarehouseId();
        Long warehousePositionId = inventoryBillProductNum.getWarehousePositionId();
        BigDecimal needExecutionProductNum = inventoryBillProductNum.getNeedExecutionProductNum();
        BigDecimal unitPrice = inventoryBillProductNum.getUnitPrice();
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
            // 增加商品成本，会增加和减少
            BigDecimal oldProductInventoryCost = oldProductInventory.getProductInventoryCost();
            BigDecimal currentPurchaseProductCost = unitPrice.multiply(needExecutionProductNum);
            // BigDecimal newProductInventoryCost = oldProductInventoryCost.add(currentPurchaseProductCost.multiply(inventoryBillExchangeRate));
            BigDecimal newProductInventoryCost = oldProductInventoryCost.add(currentPurchaseProductCost);
            productInventory.setProductInventoryCost(newProductInventoryCost);
            // 增加商品剩余数量，会增加和减少
            BigDecimal oldProductInventorySurplusNum = oldProductInventory.getProductInventorySurplusNum();
            BigDecimal newProductInventorySurplusNum = oldProductInventorySurplusNum.add(needExecutionProductNum);
            productInventory.setProductInventorySurplusNum(newProductInventorySurplusNum);
            // 商品单位成本 = 成本 / 商品剩余数量
            BigDecimal newProductInventoryUnitCost = newProductInventoryCost.divide(newProductInventorySurplusNum, 2, RoundingMode.HALF_UP);
            productInventory.setProductInventoryUnitCost(newProductInventoryUnitCost);
            productInventory.setUpdateTime(new Date());
            productInventory.setProductInventoryId(oldProductInventory.getProductInventoryId());
            // 不会为 0 的商品平均单位成本
            productInventory.setProductUnitCost(newProductInventoryUnitCost);
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
            productInventory.setProductInventorySurplusNum(needExecutionProductNum);
            productInventory.setProductInventoryUnitCost(unitPrice);
            BigDecimal cost = unitPrice.multiply(needExecutionProductNum);
            // productInventory.setProductInventoryCost(cost.multiply(inventoryBillExchangeRate));
            productInventory.setProductInventoryCost(cost);
            productInventory.setCreateTime(new Date());
            productInventory.setUpdateTime(new Date());
            // 不会为 0 的商品平均单位成本
            productInventory.setProductUnitCost(cost);
            int result = ipmsProductInventoryMapper.insert(productInventory);
            if (result != 1) {
                throw new BusinessException(ErrorCode.SYSTEM_ERROR, "库存插入失败");
            }
            return result;
        }
    }

    @Override
    public int reduceProductInventory(IpmsInventoryBillProductNum inventoryBillProductNum) {
        Long productId = inventoryBillProductNum.getProductId();
        Long warehouseId = inventoryBillProductNum.getWarehouseId();
        Long warehousePositionId = inventoryBillProductNum.getWarehousePositionId();
        BigDecimal needExecutionProductNum = inventoryBillProductNum.getNeedExecutionProductNum();
        BigDecimal unitPrice = inventoryBillProductNum.getUnitPrice();
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
        BigDecimal currentInventoryProductCost = unitPrice.multiply(needExecutionProductNum);
        // BigDecimal newProductInventoryCost = oldProductInventoryCost.subtract(currentInventoryProductCost.multiply(inventoryBillExchangeRate));
        BigDecimal newProductInventoryCost = oldProductInventoryCost.subtract(currentInventoryProductCost);
        productInventory.setProductInventoryCost(newProductInventoryCost);
        // 不会为 0 的商品平均单位成本
        productInventory.setProductUnitCost(newProductInventoryCost);
        // 目前的库存商品数量减少
        BigDecimal oldProductInventorySurplusNum = oldProductInventory.getProductInventorySurplusNum();
        BigDecimal newProductInventorySurplusNum = oldProductInventorySurplusNum.subtract(needExecutionProductNum);
        productInventory.setProductInventorySurplusNum(newProductInventorySurplusNum);
        if (newProductInventorySurplusNum.doubleValue() < 0) {
            IpmsWarehouse warehouse = ipmsWarehouseService.getById(warehouseId);
            Integer isNegativeInventory = warehouse.getIsNegativeInventory();
            if (WarehouseConstant.CLOSE_NEGATIVE_INVENTORY == isNegativeInventory) {
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

    @Override
    public int addProductInventoryByTransfer(IpmsInventoryBillProductNum inventoryBillProductNum, String checkType) {
        QueryWrapper<IpmsProductInventory> productInventoryQueryWrapper = new QueryWrapper<>();
        Long transferWarehouseId = inventoryBillProductNum.getTransferWarehouseId(); // 调入仓库 id
        Long transferWarehousePositionId = inventoryBillProductNum.getTransferWarehousePositionId(); // 调入仓位 id
        Long productId = inventoryBillProductNum.getProductId(); // 调人商品 id
        BigDecimal needExecutionProductNum = inventoryBillProductNum.getNeedExecutionProductNum(); // 商品数量
        productInventoryQueryWrapper.eq("product_id", productId);
        productInventoryQueryWrapper.eq("warehouse_id", transferWarehouseId);
        if (transferWarehousePositionId != null && transferWarehousePositionId > 0) {
            productInventoryQueryWrapper.eq("warehouse_position_id", transferWarehousePositionId);
        }
        IpmsWarehouse warehouse = ipmsWarehouseService.getById(transferWarehouseId);
        Integer isNegativeInventory = warehouse.getIsNegativeInventory();
        IpmsProductInventory oldProductInventory = ipmsProductInventoryMapper.selectOne(productInventoryQueryWrapper);
        IpmsProductInventory productInventory;
        if (Constant.CHECK_OPERATION.equals(checkType)) {
            productInventory = new IpmsProductInventory();
            if (oldProductInventory != null) {
                BigDecimal oldProductInventoryCost = oldProductInventory.getProductInventoryCost(); // 原来库存商品成本
                BigDecimal oldProductInventorySurplusNum = oldProductInventory.getProductInventorySurplusNum(); // 原来库存商品数量
                BigDecimal newProductInventorySurplusNum = oldProductInventorySurplusNum.add(needExecutionProductNum); // 新的商品库存数量
                BigDecimal newProductInventoryUnitCost = oldProductInventoryCost.divide(newProductInventorySurplusNum, 2, RoundingMode.HALF_UP);
                productInventory.setProductInventoryId(oldProductInventory.getProductInventoryId());
                productInventory.setProductInventorySurplusNum(newProductInventorySurplusNum);
                productInventory.setProductInventoryUnitCost(newProductInventoryUnitCost);
                productInventory.setProductUnitCost(newProductInventoryUnitCost);
                int result = ipmsProductInventoryMapper.updateById(productInventory);
                if (result != 1) {
                    throw new BusinessException(ErrorCode.SYSTEM_ERROR, "减少库存数量失败");
                }
                return result;
            } else {
                productInventory.setWarehouseId(transferWarehouseId);
                if (transferWarehousePositionId != null && transferWarehousePositionId > 0) {
                    productInventory.setWarehousePositionId(transferWarehousePositionId);
                }
                productInventory.setProductId(productId);
                productInventory.setProductInventorySurplusNum(needExecutionProductNum);
                productInventory.setProductInventoryCost(BigDecimal.ZERO);
                productInventory.setProductUnitCost(BigDecimal.ZERO);
                productInventory.setProductInventoryUnitCost(BigDecimal.ZERO);
                productInventory.setCreateTime(new Date());
                productInventory.setUpdateTime(new Date());
                int result = ipmsProductInventoryMapper.insert(productInventory);
                if (result != 1) {
                    throw new BusinessException(ErrorCode.SYSTEM_ERROR, "库存插入失败");
                }
                return result;
            }
        } else if (Constant.UNCHECKED_OPERATION.equals(checkType)) {
            if (isNegativeInventory == WarehouseConstant.CLOSE_NEGATIVE_INVENTORY) {
                // 关闭负库存转移商品方式
                if (oldProductInventory == null) {
                    throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "扣减的商品不存在，并且由于该仓库没有开启负库存，所以无法扣减商品");
                }
                // 移仓单：调用商品时库存成本不受到影响，只有数量和单价会被影响改变。
                productInventory = new IpmsProductInventory();
                BigDecimal oldProductInventoryCost = oldProductInventory.getProductInventoryCost(); // 原来库存商品成本
                BigDecimal oldProductInventorySurplusNum = oldProductInventory.getProductInventorySurplusNum(); // 原来库存商品数量
                BigDecimal newProductInventorySurplusNum = oldProductInventorySurplusNum.subtract(needExecutionProductNum); // 新的商品库存数量
                if (newProductInventorySurplusNum.doubleValue() == 0) {
                    productInventory.setProductInventoryId(oldProductInventory.getProductInventoryId());
                    productInventory.setProductInventorySurplusNum(newProductInventorySurplusNum);
                    productInventory.setProductInventoryUnitCost(BigDecimal.ZERO);
                    productInventory.setProductUnitCost(BigDecimal.ZERO);
                } else {
                    BigDecimal newProductInventoryUnitCost = oldProductInventoryCost.divide(newProductInventorySurplusNum, 2, RoundingMode.HALF_UP);
                    productInventory.setProductInventoryId(oldProductInventory.getProductInventoryId());
                    productInventory.setProductInventorySurplusNum(newProductInventorySurplusNum);
                    productInventory.setProductInventoryUnitCost(newProductInventoryUnitCost);
                    productInventory.setProductUnitCost(newProductInventoryUnitCost);
                }
                if (newProductInventorySurplusNum.doubleValue() < 0) {
                    throw new BusinessException(ErrorCode.PARAMS_ERROR, "库存未开启负库存管理功能，无法继续扣减库存");
                }
                int result = ipmsProductInventoryMapper.updateById(productInventory);
                if (result != 1) {
                    throw new BusinessException(ErrorCode.SYSTEM_ERROR, "减少库存数量失败");
                }
                return result;
            } else {
                // 开启负库存转移商品方式
                productInventory = new IpmsProductInventory();
                if (oldProductInventory != null) {
                    // 移仓单：调用商品时库存成本不受到影响，只有数量和单价会被影响改变。
                    BigDecimal oldProductInventoryCost = oldProductInventory.getProductInventoryCost(); // 原来库存商品成本
                    BigDecimal oldProductInventorySurplusNum = oldProductInventory.getProductInventorySurplusNum(); // 原来库存商品数量
                    BigDecimal newProductInventorySurplusNum = oldProductInventorySurplusNum.subtract(needExecutionProductNum); // 新的商品库存数量
                    BigDecimal newProductInventoryUnitCost = oldProductInventoryCost.divide(newProductInventorySurplusNum, 2, RoundingMode.HALF_UP);
                    productInventory.setProductInventoryId(oldProductInventory.getProductInventoryId());
                    productInventory.setProductInventorySurplusNum(newProductInventorySurplusNum);
                    productInventory.setProductInventoryUnitCost(newProductInventoryUnitCost);
                    productInventory.setProductUnitCost(newProductInventoryUnitCost);
                    int result = ipmsProductInventoryMapper.updateById(productInventory);
                    if (result != 1) {
                        throw new BusinessException(ErrorCode.SYSTEM_ERROR, "减少库存数量失败");
                    }
                    return result;
                } else {
                    productInventory.setWarehouseId(transferWarehouseId);
                    if (transferWarehousePositionId != null && transferWarehousePositionId > 0) {
                        productInventory.setWarehousePositionId(transferWarehousePositionId);
                    }
                    productInventory.setProductId(productId);
                    productInventory.setProductInventorySurplusNum(needExecutionProductNum);
                    productInventory.setProductInventoryCost(BigDecimal.ZERO);
                    productInventory.setProductUnitCost(BigDecimal.ZERO);
                    productInventory.setProductInventoryUnitCost(BigDecimal.ZERO);
                    productInventory.setCreateTime(new Date());
                    productInventory.setUpdateTime(new Date());
                    int result = ipmsProductInventoryMapper.insert(productInventory);
                    if (result != 1) {
                        throw new BusinessException(ErrorCode.SYSTEM_ERROR, "库存插入失败");
                    }
                    return result;
                }
            }
        }
        return 1;
    }

    @Override
    public int reduceProductInventoryByTransfer(IpmsInventoryBillProductNum inventoryBillProductNum, String checkType) {
        QueryWrapper<IpmsProductInventory> productInventoryQueryWrapper = new QueryWrapper<>();
        Long warehouseId = inventoryBillProductNum.getWarehouseId(); // 调出仓库 id
        Long warehousePositionId = inventoryBillProductNum.getWarehousePositionId(); // 调出仓位 id
        Long productId = inventoryBillProductNum.getProductId(); // 调出商品 id
        BigDecimal needExecutionProductNum = inventoryBillProductNum.getNeedExecutionProductNum(); // 商品数量
        productInventoryQueryWrapper.eq("product_id", productId);
        productInventoryQueryWrapper.eq("warehouse_id", warehouseId);
        if (warehousePositionId != null && warehousePositionId > 0) {
            productInventoryQueryWrapper.eq("warehouse_position_id", warehousePositionId);
        }
        IpmsWarehouse warehouse = ipmsWarehouseService.getById(warehouseId);
        Integer isNegativeInventory = warehouse.getIsNegativeInventory();
        IpmsProductInventory oldProductInventory = ipmsProductInventoryMapper.selectOne(productInventoryQueryWrapper);
        IpmsProductInventory productInventory;
        if (Constant.CHECK_OPERATION.equals(checkType)) {
            if (isNegativeInventory == WarehouseConstant.CLOSE_NEGATIVE_INVENTORY) {
                // 关闭负库存转移商品方式
                if (oldProductInventory == null) {
                    throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "扣减的商品不存在，并且由于该仓库没有开启负库存，所以无法扣减商品");
                }
                // 移仓单：调用商品时库存成本不受到影响，只有数量和单价会被影响改变。
                productInventory = new IpmsProductInventory();
                BigDecimal oldProductInventoryCost = oldProductInventory.getProductInventoryCost(); // 原来库存商品成本
                BigDecimal oldProductInventorySurplusNum = oldProductInventory.getProductInventorySurplusNum(); // 原来库存商品数量
                BigDecimal newProductInventorySurplusNum = oldProductInventorySurplusNum.subtract(needExecutionProductNum); // 新的商品库存数量
                if (newProductInventorySurplusNum.doubleValue() == 0) {
                    productInventory.setProductInventoryId(oldProductInventory.getProductInventoryId());
                    productInventory.setProductInventorySurplusNum(newProductInventorySurplusNum);
                    productInventory.setProductInventoryUnitCost(BigDecimal.ZERO);
                    productInventory.setProductUnitCost(BigDecimal.ZERO);
                } else {
                    BigDecimal newProductInventoryUnitCost = oldProductInventoryCost.divide(newProductInventorySurplusNum, 2, RoundingMode.HALF_UP);
                    productInventory.setProductInventoryId(oldProductInventory.getProductInventoryId());
                    productInventory.setProductInventorySurplusNum(newProductInventorySurplusNum);
                    productInventory.setProductInventoryUnitCost(newProductInventoryUnitCost);
                    productInventory.setProductUnitCost(newProductInventoryUnitCost);
                }
                if (newProductInventorySurplusNum.doubleValue() < 0) {
                    throw new BusinessException(ErrorCode.PARAMS_ERROR, "库存未开启负库存管理功能，无法继续扣减库存");
                }
                int result = ipmsProductInventoryMapper.updateById(productInventory);
                if (result != 1) {
                    throw new BusinessException(ErrorCode.SYSTEM_ERROR, "减少库存数量失败");
                }
                return result;
            } else {
                // 开启负库存转移商品方式
                productInventory = new IpmsProductInventory();
                if (oldProductInventory != null) {
                    // 移仓单：调用商品时库存成本不受到影响，只有数量和单价会被影响改变。
                    BigDecimal oldProductInventoryCost = oldProductInventory.getProductInventoryCost(); // 原来库存商品成本
                    BigDecimal oldProductInventorySurplusNum = oldProductInventory.getProductInventorySurplusNum(); // 原来库存商品数量
                    BigDecimal newProductInventorySurplusNum = oldProductInventorySurplusNum.subtract(needExecutionProductNum); // 新的商品库存数量
                    BigDecimal newProductInventoryUnitCost = oldProductInventoryCost.divide(newProductInventorySurplusNum, 2, RoundingMode.HALF_UP);
                    productInventory.setProductInventoryId(oldProductInventory.getProductInventoryId());
                    productInventory.setProductInventorySurplusNum(newProductInventorySurplusNum);
                    productInventory.setProductInventoryUnitCost(newProductInventoryUnitCost);
                    productInventory.setProductUnitCost(newProductInventoryUnitCost);
                    int result = ipmsProductInventoryMapper.updateById(productInventory);
                    if (result != 1) {
                        throw new BusinessException(ErrorCode.SYSTEM_ERROR, "减少库存数量失败");
                    }
                    return result;
                } else {
                    productInventory.setWarehouseId(warehouseId);
                    if (warehousePositionId != null && warehousePositionId > 0) {
                        productInventory.setWarehousePositionId(warehousePositionId);
                    }
                    productInventory.setProductId(productId);
                    productInventory.setProductInventorySurplusNum(needExecutionProductNum);
                    productInventory.setProductInventoryCost(BigDecimal.ZERO);
                    productInventory.setProductUnitCost(BigDecimal.ZERO);
                    productInventory.setProductInventoryUnitCost(BigDecimal.ZERO);
                    productInventory.setCreateTime(new Date());
                    productInventory.setUpdateTime(new Date());
                    int result = ipmsProductInventoryMapper.insert(productInventory);
                    if (result != 1) {
                        throw new BusinessException(ErrorCode.SYSTEM_ERROR, "库存插入失败");
                    }
                    return result;
                }
            }
        } else if (Constant.UNCHECKED_OPERATION.equals(checkType)) {
            productInventory = new IpmsProductInventory();
            if (oldProductInventory != null) {
                BigDecimal oldProductInventoryCost = oldProductInventory.getProductInventoryCost(); // 原来库存商品成本
                BigDecimal oldProductInventorySurplusNum = oldProductInventory.getProductInventorySurplusNum(); // 原来库存商品数量
                BigDecimal newProductInventorySurplusNum = oldProductInventorySurplusNum.add(needExecutionProductNum); // 新的商品库存数量
                BigDecimal newProductInventoryUnitCost = oldProductInventoryCost.divide(newProductInventorySurplusNum, 2, RoundingMode.HALF_UP);
                productInventory.setProductInventoryId(oldProductInventory.getProductInventoryId());
                productInventory.setProductInventorySurplusNum(newProductInventorySurplusNum);
                productInventory.setProductInventoryUnitCost(newProductInventoryUnitCost);
                productInventory.setProductUnitCost(newProductInventoryUnitCost);
                int result = ipmsProductInventoryMapper.updateById(productInventory);
                if (result != 1) {
                    throw new BusinessException(ErrorCode.SYSTEM_ERROR, "减少库存数量失败");
                }
                return result;
            } else {
                productInventory.setWarehouseId(warehouseId);
                if (warehousePositionId != null && warehousePositionId > 0) {
                    productInventory.setWarehousePositionId(warehousePositionId);
                }
                productInventory.setProductId(productId);
                productInventory.setProductInventorySurplusNum(needExecutionProductNum);
                productInventory.setProductInventoryCost(BigDecimal.ZERO);
                productInventory.setProductUnitCost(BigDecimal.ZERO);
                productInventory.setProductInventoryUnitCost(BigDecimal.ZERO);
                productInventory.setCreateTime(new Date());
                productInventory.setUpdateTime(new Date());
                int result = ipmsProductInventoryMapper.insert(productInventory);
                if (result != 1) {
                    throw new BusinessException(ErrorCode.SYSTEM_ERROR, "库存插入失败");
                }
                return result;
            }
        }
        return 1;
    }

    @Override
    public int deleteProductInventoryRecord(long id) {
        if (id <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "商品库存 id 不合法");
        }
        IpmsProductInventory productInventory = ipmsProductInventoryMapper.selectById(id);
        if (productInventory == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }
        if (productInventory.getProductInventorySurplusNum().doubleValue() != 0) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "删除失败，因为只能删除库存为 0 的商品库存记录");
        }
        int result = ipmsProductInventoryMapper.deleteById(id);
        if (result != 1) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "删除商品库存记录失败");
        }
        return result;
    }

    @Override
    public BigDecimal getAvailableInventoryOfProduct(long productId, long warehouseId, Long warehousePositionId, HttpServletRequest request) {
        SafeUserVO loginUser = ipmsUserService.getLoginUser(request);
        if (loginUser == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR);
        }
        if (productId < 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "商品 id 不合法");
        }
        if (warehouseId < 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "仓库 id 不合法");
        }
        IpmsProduct product = ipmsProductService.getById(productId);
        if (product == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "商品不存在");
        }
        IpmsWarehouse warehouse = ipmsWarehouseService.getById(warehouseId);
        if (warehouse == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "仓库不存在");
        }
        if (warehouse.getIsWarehousePositionManagement() == WarehouseConstant.OPEN_WAREHOUSE_POSITION_MANAGEMENT) {
            if (warehousePositionId == null) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "仓库已开启仓位管理，必须要有仓位 id");
            }
            if (warehousePositionId < 0) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "仓库 id 不合法");
            }
            IpmsWarehousePosition warehousePosition = ipmsWarehousePositionService.getById(warehousePositionId);
            if (warehousePosition == null) {
                throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "仓位不存在");
            }
        }
        QueryWrapper<IpmsProductInventory> productInventoryQueryWrapper = new QueryWrapper<>();
        productInventoryQueryWrapper.eq("product_id", productId);
        productInventoryQueryWrapper.eq("warehouse_id", warehouseId);
        if (warehousePositionId != null && warehousePositionId > 0) {
            productInventoryQueryWrapper.eq("warehouse_position_id", warehousePositionId);
        }
        IpmsProductInventory productInventory = ipmsProductInventoryMapper.selectOne(productInventoryQueryWrapper);
        if (productInventory == null) {
            return BigDecimal.ZERO;
        }
        return productInventory.getProductInventorySurplusNum();
    }
}




