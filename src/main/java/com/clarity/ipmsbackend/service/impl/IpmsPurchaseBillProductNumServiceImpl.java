package com.clarity.ipmsbackend.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.clarity.ipmsbackend.common.ErrorCode;
import com.clarity.ipmsbackend.constant.PurchaseBillConstant;
import com.clarity.ipmsbackend.constant.WarehouseConstant;
import com.clarity.ipmsbackend.exception.BusinessException;
import com.clarity.ipmsbackend.model.dto.purchasebill.productnum.AddProductNumRequest;
import com.clarity.ipmsbackend.model.entity.*;
import com.clarity.ipmsbackend.mapper.IpmsPurchaseBillProductNumMapper;
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
    public int addPurchaseBillProductAndNum(AddProductNumRequest addProductNumRequest, IpmsPurchaseBill purchaseBill) {
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
            purchaseBillProductNum.setNeedWarehousingProductNum(productNum);
            purchaseBillProductNum.setSurplusNeedWarehousingProductNum(productNum);
        } else if (PurchaseBillConstant.PURCHASE_RETURN_ORDER.equals(purchaseBillType)) {
            if (purchaseSourceBillId != null && purchaseSourceBillId > 0) {
                // 查找到原单源的商品信息及数量，去修改源单源商品剩余的入库数量
                QueryWrapper<IpmsPurchaseBillProductNum> queryWrapper = new QueryWrapper<>();
                queryWrapper.eq("purchase_bill_id", purchaseSourceBillId);
                queryWrapper.eq("product_id", productId);
                IpmsPurchaseBillProductNum sourcePurchaseBillProductNum = ipmsPurchaseBillProductNumMapper.selectOne(queryWrapper);
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
            purchaseBillProductNum.setNeedWarehousingProductNum(productNum);
            purchaseBillProductNum.setSurplusNeedWarehousingProductNum(productNum);
        } else {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "单据类型不合法");
        }
        int result = ipmsPurchaseBillProductNumMapper.insert(purchaseBillProductNum);
        if (result != 1) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR);
        }
        return result;
    }
}




