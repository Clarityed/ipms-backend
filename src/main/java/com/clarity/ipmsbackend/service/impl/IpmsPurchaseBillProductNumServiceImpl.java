package com.clarity.ipmsbackend.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.clarity.ipmsbackend.common.ErrorCode;
import com.clarity.ipmsbackend.constant.PurchaseBillConstant;
import com.clarity.ipmsbackend.constant.WarehouseConstant;
import com.clarity.ipmsbackend.exception.BusinessException;
import com.clarity.ipmsbackend.mapper.IpmsPurchaseBillProductNumMapper;
import com.clarity.ipmsbackend.model.dto.purchasebill.productnum.AddProductNumRequest;
import com.clarity.ipmsbackend.model.dto.purchasebill.productnum.UpdateProductNumRequest;
import com.clarity.ipmsbackend.model.entity.*;
import com.clarity.ipmsbackend.service.IpmsProductService;
import com.clarity.ipmsbackend.service.IpmsPurchaseBillProductNumService;
import com.clarity.ipmsbackend.service.IpmsWarehousePositionService;
import com.clarity.ipmsbackend.service.IpmsWarehouseService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.Date;

/**
* @author Clarity
* @description 针对表【ipms_purchase_bill_product_num(采购单据商品数量表)】的数据库操作Service实现
* @createDate 2023-03-13 16:00:04
*/
@Service
public class IpmsPurchaseBillProductNumServiceImpl extends ServiceImpl<IpmsPurchaseBillProductNumMapper, IpmsPurchaseBillProductNum>
    implements IpmsPurchaseBillProductNumService{

    @Resource
    private IpmsPurchaseBillProductNumMapper ipmsPurchaseBillProductNumMapper;

    @Resource
    private IpmsProductService ipmsProductService;

    @Resource
    private IpmsWarehouseService ipmsWarehouseService;

    @Resource
    private IpmsWarehousePositionService ipmsWarehousePositionService;

    @Override
    public long addPurchaseBillProductAndNum(AddProductNumRequest addProductNumRequest, IpmsPurchaseBill purchaseBill) {
        // 1. 商品 id，不能为空，必须存在
        Long productId = addProductNumRequest.getProductId();
        if (productId == null || productId <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "商品 id 为空或者不合法");
        }
        IpmsProduct validProduct = ipmsProductService.getById(productId);
        if (validProduct == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "商品不存在");
        }
        // 2. 仓库 id，不能为空，必须存在
        Long warehouseId = addProductNumRequest.getWarehouseId();
        if (warehouseId == null || warehouseId <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "仓库 id 为空或者不合法");
        }
        IpmsWarehouse validWarehouse = ipmsWarehouseService.getById(warehouseId);
        if (validWarehouse == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "仓库不存在");
        }
        // 3. 仓位 id，如果选择的仓库没有仓位，可以为空
        //    如果仓库开启仓位那么一定要有仓位 id，且必须存在
        Integer isWarehousePositionManagement = validWarehouse.getIsWarehousePositionManagement();
        if (isWarehousePositionManagement == null) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "开启仓位字段出现问题");
        }
        if (isWarehousePositionManagement == WarehouseConstant.OPEN_WAREHOUSE_POSITION_MANAGEMENT) {
            Long warehousePositionId = addProductNumRequest.getWarehousePositionId();
            if (warehousePositionId == null || warehousePositionId <= 0) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "仓位 id 为空或者不合法");
            }
            IpmsWarehousePosition validWarehousePosition = ipmsWarehousePositionService.getById(warehousePositionId);
            if (validWarehousePosition == null) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "仓位不存在");
            }
        }
        // 4. 商品数量，不能为空，且必须大于 0
        BigDecimal productNum = addProductNumRequest.getProductNum();
        if (productNum == null || productNum.doubleValue() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "商品数量为空或者小于等于 0");
        }
        // 5. 商品单价也是，价格合计也是
        BigDecimal unitPrice = addProductNumRequest.getUnitPrice();
        BigDecimal totalPrice = addProductNumRequest.getTotalPrice();
        if (unitPrice == null || unitPrice.doubleValue() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "商品单价为空或者小于等于 0");
        }
        if (totalPrice == null || totalPrice.doubleValue() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "价格合计为空或者小于等于 0");
        }
        // 6. 增加数据，并且设置其他固定字段。
        IpmsPurchaseBillProductNum purchaseBillProductNum = new IpmsPurchaseBillProductNum();
        BeanUtils.copyProperties(addProductNumRequest, purchaseBillProductNum);
        purchaseBillProductNum.setPurchaseBillId(purchaseBill.getPurchaseBillId());
        purchaseBillProductNum.setCreateTime(new Date());
        purchaseBillProductNum.setUpdateTime(new Date());
        String purchaseBillType = purchaseBill.getPurchaseBillType();
        Long purchaseSourceBillId = purchaseBill.getPurchaseSourceBillId();
        if (PurchaseBillConstant.PURCHASE_ORDER.equals(purchaseBillType)) {
            purchaseBillProductNum.setNeedWarehousingProductNum(productNum);
            purchaseBillProductNum.setSurplusNeedWarehousingProductNum(productNum);
        } else if (PurchaseBillConstant.PURCHASE_RECEIPT_ORDER.equals(purchaseBillType)) {
            if (purchaseSourceBillId != null && purchaseSourceBillId > 0) {
                // 查找到原单源的商品信息及数量，去修改源单源商品剩余的入库数量
                QueryWrapper<IpmsPurchaseBillProductNum> queryWrapper = new QueryWrapper<>();
                queryWrapper.eq("purchase_bill_id", purchaseSourceBillId);
                queryWrapper.eq("product_id", productId);
                IpmsPurchaseBillProductNum sourcePurchaseBillProductNum = ipmsPurchaseBillProductNumMapper.selectOne(queryWrapper);
                if (sourcePurchaseBillProductNum != null) {
                    BigDecimal surplusNeedWarehousingProductNum = sourcePurchaseBillProductNum.getSurplusNeedWarehousingProductNum();
                    if (surplusNeedWarehousingProductNum.doubleValue() < productNum.doubleValue()) {
                        throw new BusinessException(ErrorCode.PARAMS_ERROR, "采购入库单的入库商品数量超过采购订单的商品数量");
                    }
                    sourcePurchaseBillProductNum.setSurplusNeedWarehousingProductNum(surplusNeedWarehousingProductNum.subtract(productNum));
                    int updateSuperBill = ipmsPurchaseBillProductNumMapper.updateById(sourcePurchaseBillProductNum);
                    if (updateSuperBill != 1) {
                        throw new BusinessException(ErrorCode.SYSTEM_ERROR, "更新上级单据剩余商品数量失败");
                    }
                }
            }
            purchaseBillProductNum.setNeedWarehousingProductNum(productNum);
            purchaseBillProductNum.setSurplusNeedWarehousingProductNum(productNum);
        } else if (PurchaseBillConstant.PURCHASE_RETURN_ORDER.equals(purchaseBillType)) {
            if (purchaseSourceBillId != null && purchaseSourceBillId > 0) {
                // 查找到原单源的商品信息及数量，去修改源单源商品剩余的入库数量
                QueryWrapper<IpmsPurchaseBillProductNum> queryWrapper = new QueryWrapper<>();
                queryWrapper.eq("purchase_bill_id", purchaseSourceBillId);
                queryWrapper.eq("product_id", productId);
                IpmsPurchaseBillProductNum sourcePurchaseBillProductNum = ipmsPurchaseBillProductNumMapper.selectOne(queryWrapper);
                if (sourcePurchaseBillProductNum != null) {
                    BigDecimal surplusNeedWarehousingProductNum = sourcePurchaseBillProductNum.getSurplusNeedWarehousingProductNum();
                    if (surplusNeedWarehousingProductNum.doubleValue() < productNum.doubleValue()) {
                        throw new BusinessException(ErrorCode.PARAMS_ERROR, "采购退货单的退货商品数量超过采购入库单的商品数量");
                    }
                    sourcePurchaseBillProductNum.setSurplusNeedWarehousingProductNum(surplusNeedWarehousingProductNum.subtract(productNum));
                    int updateSuperBill = ipmsPurchaseBillProductNumMapper.updateById(sourcePurchaseBillProductNum);
                    if (updateSuperBill != 1) {
                        throw new BusinessException(ErrorCode.SYSTEM_ERROR, "更新上级单据剩余商品数量失败");
                    }
                }
            }
            purchaseBillProductNum.setNeedWarehousingProductNum(productNum);
            purchaseBillProductNum.setSurplusNeedWarehousingProductNum(productNum);
        } else {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "单据类型不合法");
        }
        int result = ipmsPurchaseBillProductNumMapper.insert(purchaseBillProductNum);
        if (result != 1) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR);
        }
        return purchaseBillProductNum.getPurchaseBillProductId();
    }

    @Override
    public int updatePurchaseBillProductAndNum(UpdateProductNumRequest updateProductNumRequest, IpmsPurchaseBill purchaseBill) {
        Long purchaseBillProductId = updateProductNumRequest.getPurchaseBillProductId();
        if (purchaseBillProductId == null || purchaseBillProductId < 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "单据商品库存 id 为空或者不合法");
        }
        // 判断采购单据是否存在
        IpmsPurchaseBillProductNum purchaseBillProduct = ipmsPurchaseBillProductNumMapper.selectById(purchaseBillProductId);
        if (purchaseBillProduct == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "采购单据商品不存在");
        }
        Long productId = updateProductNumRequest.getProductId();
        if (productId == null || productId < 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "采购单据商品 id 为空或者不合法");
        }
        // 判断要修改成的商品是否存在
        IpmsProduct product = ipmsProductService.getById(productId);
        if (product == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "商品不存在");
        }
        // 仓库是否为空
        Long warehouseId = updateProductNumRequest.getWarehouseId();
        Long warehousePositionId = updateProductNumRequest.getWarehousePositionId();
        if (warehouseId != null) {
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
            }
        }
        // 商品数量，单价，价格验证
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
        // 更新对象
        IpmsPurchaseBillProductNum purchaseBillProductNum = new IpmsPurchaseBillProductNum();
        BeanUtils.copyProperties(updateProductNumRequest, purchaseBillProductNum);
        purchaseBillProductNum.setUpdateTime(new Date());
        String purchaseBillType = purchaseBill.getPurchaseBillType();
        // 对于采购入库单来说源单是采购订单，对于采购退货单源单是采购入库单
        Long purchaseSourceBillId = purchaseBill.getPurchaseSourceBillId();
        QueryWrapper<IpmsPurchaseBillProductNum> purchaseBillProductNumQueryWrapper;
        if (purchaseBillType != null) {
            switch (purchaseBillType) {
                case PurchaseBillConstant.PURCHASE_ORDER:
                    // 采购订单的商品，无特殊操作
                    purchaseBillProductNum.setNeedWarehousingProductNum(productNum);
                    purchaseBillProductNum.setSurplusNeedWarehousingProductNum(productNum);
                    break;
                case PurchaseBillConstant.PURCHASE_RECEIPT_ORDER:
                    // 采购入库单的商品，如果有源单采购订单那么必须修改源单对应商品的剩余商品数量
                    if (purchaseSourceBillId != null && purchaseSourceBillId > 0) {
                        purchaseBillProductNumQueryWrapper = new QueryWrapper<>();
                        purchaseBillProductNumQueryWrapper.eq("purchase_bill_id", purchaseSourceBillId);
                        purchaseBillProductNumQueryWrapper.eq("product_id", productId);
                        IpmsPurchaseBillProductNum purchaseSourceBillProduct = ipmsPurchaseBillProductNumMapper.selectOne(purchaseBillProductNumQueryWrapper);
                        if (purchaseSourceBillProduct != null) {
                            // 查找到采购订单剩余需要入库的商品数量
                            BigDecimal sourceSurplusNeedWarehousingProductNum = purchaseSourceBillProduct.getSurplusNeedWarehousingProductNum();
                            // 查找到原来采购入库单需要入库的商品数量
                            BigDecimal oldNeedWarehousingProductNum = purchaseBillProduct.getNeedWarehousingProductNum();
                            // 现在更新的商品数量 productNum
                            // 得出现在和原来数量的差值
                            BigDecimal differenceValue = productNum.subtract(oldNeedWarehousingProductNum);
                            // 如果大于 0，说明采购入库单据的商品数量变多，采购订单的剩余商品数量应该减少
                            // 但是不能小于 0
                            if (differenceValue.doubleValue() > 0) {
                                BigDecimal reduceAfterSourceSurplusNeedWarehousingProductNum = sourceSurplusNeedWarehousingProductNum.subtract(differenceValue);
                                if (reduceAfterSourceSurplusNeedWarehousingProductNum.doubleValue() < 0) {
                                    throw new BusinessException(ErrorCode.PARAMS_ERROR, "采购订单没有更多的商品可以入库了");
                                }
                                purchaseSourceBillProduct.setSurplusNeedWarehousingProductNum(reduceAfterSourceSurplusNeedWarehousingProductNum);
                                int updateSuperBill = ipmsPurchaseBillProductNumMapper.updateById(purchaseSourceBillProduct);
                                if (updateSuperBill != 1) {
                                    throw new BusinessException(ErrorCode.SYSTEM_ERROR, "更新上级单据剩余商品数量失败");
                                }
                            }
                            if (differenceValue.doubleValue() < 0) {
                                BigDecimal addAfterSourceSurplusNeedWarehousingProductNum = sourceSurplusNeedWarehousingProductNum.add(differenceValue.abs());
                                if (addAfterSourceSurplusNeedWarehousingProductNum.doubleValue() > purchaseSourceBillProduct.getNeedWarehousingProductNum().doubleValue()) {
                                    throw new BusinessException(ErrorCode.PARAMS_ERROR, "采购订单已经达到可入库的最大商品数量");
                                }
                                purchaseSourceBillProduct.setSurplusNeedWarehousingProductNum(addAfterSourceSurplusNeedWarehousingProductNum);
                                int updateSuperBill = ipmsPurchaseBillProductNumMapper.updateById(purchaseSourceBillProduct);
                                if (updateSuperBill != 1) {
                                    throw new BusinessException(ErrorCode.SYSTEM_ERROR, "更新上级单据剩余商品数量失败");
                                }
                            }
                        }
                    }
                    purchaseBillProductNum.setNeedWarehousingProductNum(productNum);
                    purchaseBillProductNum.setSurplusNeedWarehousingProductNum(productNum);
                    break;
                case PurchaseBillConstant.PURCHASE_RETURN_ORDER:
                    // 采购退货单的商品，如果有源单采购入库单那么必须修改源单对应商品的剩余商品数量
                    if (purchaseSourceBillId != null && purchaseSourceBillId > 0) {
                        purchaseBillProductNumQueryWrapper = new QueryWrapper<>();
                        purchaseBillProductNumQueryWrapper.eq("purchase_bill_id", purchaseSourceBillId);
                        purchaseBillProductNumQueryWrapper.eq("product_id", productId);
                        IpmsPurchaseBillProductNum purchaseSourceBillProduct = ipmsPurchaseBillProductNumMapper.selectOne(purchaseBillProductNumQueryWrapper);
                        if (purchaseSourceBillProduct != null) {
                            // 查找到采购入库单剩余需要入库的商品数量
                            BigDecimal sourceSurplusNeedWarehousingProductNum = purchaseSourceBillProduct.getSurplusNeedWarehousingProductNum();
                            // 查找到原来采购退货单需要入库的商品数量
                            BigDecimal oldNeedWarehousingProductNum = purchaseBillProduct.getNeedWarehousingProductNum();
                            // 现在更新的商品数量 productNum
                            // 得出现在和原来数量的差值
                            BigDecimal differenceValue = productNum.subtract(oldNeedWarehousingProductNum);
                            // 如果大于 0，说明采购入库单据的商品数量变多，采购订单的剩余商品数量应该减少
                            // 但是不能小于 0
                            if (differenceValue.doubleValue() > 0) {
                                BigDecimal reduceAfterSourceSurplusNeedWarehousingProductNum = sourceSurplusNeedWarehousingProductNum.subtract(differenceValue);
                                if (reduceAfterSourceSurplusNeedWarehousingProductNum.doubleValue() < 0) {
                                    throw new BusinessException(ErrorCode.PARAMS_ERROR, "采购入库单没有更多的商品可以退货了");
                                }
                                purchaseSourceBillProduct.setSurplusNeedWarehousingProductNum(reduceAfterSourceSurplusNeedWarehousingProductNum);
                                int updateSuperBill = ipmsPurchaseBillProductNumMapper.updateById(purchaseSourceBillProduct);
                                if (updateSuperBill != 1) {
                                    throw new BusinessException(ErrorCode.SYSTEM_ERROR, "更新上级单据剩余商品数量失败");
                                }
                            }
                            if (differenceValue.doubleValue() < 0) {
                                BigDecimal addAfterSourceSurplusNeedWarehousingProductNum = sourceSurplusNeedWarehousingProductNum.add(differenceValue.abs());
                                if (addAfterSourceSurplusNeedWarehousingProductNum.doubleValue() > purchaseSourceBillProduct.getNeedWarehousingProductNum().doubleValue()) {
                                    throw new BusinessException(ErrorCode.PARAMS_ERROR, "采购入库单已经达到可退货的最大商品数量");
                                }
                                purchaseSourceBillProduct.setSurplusNeedWarehousingProductNum(addAfterSourceSurplusNeedWarehousingProductNum);
                                int updateSuperBill = ipmsPurchaseBillProductNumMapper.updateById(purchaseSourceBillProduct);
                                if (updateSuperBill != 1) {
                                    throw new BusinessException(ErrorCode.SYSTEM_ERROR, "更新上级单据剩余商品数量失败");
                                }
                            }
                        }
                    }
                    purchaseBillProductNum.setNeedWarehousingProductNum(productNum);
                    purchaseBillProductNum.setSurplusNeedWarehousingProductNum(productNum);
                    break;
            }
        }
        int result = ipmsPurchaseBillProductNumMapper.updateById(purchaseBillProductNum);
        if (result != 1) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "采购单据商品更新失败");
        }
        return result;
    }
}




