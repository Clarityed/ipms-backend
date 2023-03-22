package com.clarity.ipmsbackend.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.clarity.ipmsbackend.common.ErrorCode;
import com.clarity.ipmsbackend.constant.SaleBillConstant;
import com.clarity.ipmsbackend.constant.WarehouseConstant;
import com.clarity.ipmsbackend.exception.BusinessException;
import com.clarity.ipmsbackend.mapper.IpmsSaleBillProductNumMapper;
import com.clarity.ipmsbackend.model.dto.salebill.productnum.AddSaleProductNumRequest;
import com.clarity.ipmsbackend.model.dto.salebill.productnum.UpdateSaleProductNumRequest;
import com.clarity.ipmsbackend.model.entity.*;
import com.clarity.ipmsbackend.service.IpmsProductService;
import com.clarity.ipmsbackend.service.IpmsSaleBillProductNumService;
import com.clarity.ipmsbackend.service.IpmsWarehousePositionService;
import com.clarity.ipmsbackend.service.IpmsWarehouseService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.Date;

/**
* @author Clarity
* @description 针对表【ipms_sale_bill_product_num(销售单据商品数量表)】的数据库操作Service实现
* @createDate 2023-03-21 10:28:13
*/
@Service
public class IpmsSaleBillProductNumServiceImpl extends ServiceImpl<IpmsSaleBillProductNumMapper, IpmsSaleBillProductNum>
    implements IpmsSaleBillProductNumService{

    @Resource
    private IpmsSaleBillProductNumMapper ipmsSaleBillProductNumMapper;

    @Resource
    private IpmsProductService ipmsProductService;

    @Resource
    private IpmsWarehouseService ipmsWarehouseService;

    @Resource
    private IpmsWarehousePositionService ipmsWarehousePositionService;

    @Override
    public long addSaleBillProductAndNum(AddSaleProductNumRequest addSaleProductNumRequest, IpmsSaleBill saleBill) {
        // 1. 商品 id，不能为空，必须存在
        Long productId = addSaleProductNumRequest.getProductId();
        if (productId == null || productId <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "商品 id 为空或者不合法");
        }
        IpmsProduct validProduct = ipmsProductService.getById(productId);
        if (validProduct == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "商品不存在");
        }
        // 2. 仓库 id，不能为空，必须存在
        Long warehouseId = addSaleProductNumRequest.getWarehouseId();
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
            Long warehousePositionId = addSaleProductNumRequest.getWarehousePositionId();
            if (warehousePositionId == null || warehousePositionId <= 0) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "仓位 id 为空或者不合法");
            }
            IpmsWarehousePosition validWarehousePosition = ipmsWarehousePositionService.getById(warehousePositionId);
            if (validWarehousePosition == null) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "仓位不存在");
            }
        }
        // 4. 商品数量，不能为空，且必须大于 0
        BigDecimal productNum = addSaleProductNumRequest.getProductNum();
        if (productNum == null || productNum.doubleValue() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "商品数量为空或者小于等于 0");
        }
        // 5. 商品单价也是，价格合计也是
        BigDecimal unitPrice = addSaleProductNumRequest.getUnitPrice();
        BigDecimal totalPrice = addSaleProductNumRequest.getTotalPrice();
        if (unitPrice == null || unitPrice.doubleValue() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "商品单价为空或者小于等于 0");
        }
        if (totalPrice == null || totalPrice.doubleValue() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "价格合计为空或者小于等于 0");
        }
        // 6. 增加数据，并且设置其他固定字段。
        IpmsSaleBillProductNum saleBillProductNum = new IpmsSaleBillProductNum();
        BeanUtils.copyProperties(addSaleProductNumRequest, saleBillProductNum);
        saleBillProductNum.setSaleBillId(saleBill.getSaleBillId());
        saleBillProductNum.setCreateTime(new Date());
        saleBillProductNum.setUpdateTime(new Date());
        String saleBillType = saleBill.getSaleBillType();
        Long saleSourceBillId = saleBill.getSaleSourceBillId();
        if (SaleBillConstant.SALE_ORDER.equals(saleBillType)) {
            saleBillProductNum.setNeedDeliveryProductNum(productNum);
            saleBillProductNum.setSurplusNeedDeliveryProductNum(productNum);
        } else if (SaleBillConstant.SALE_DELIVERY_ORDER.equals(saleBillType)) {
            if (saleSourceBillId != null && saleSourceBillId > 0) {
                // 查找到原单源的商品信息及数量，去修改源单源商品剩余的出库数量
                QueryWrapper<IpmsSaleBillProductNum> queryWrapper = new QueryWrapper<>();
                queryWrapper.eq("sale_bill_id", saleSourceBillId);
                queryWrapper.eq("product_id", productId);
                IpmsSaleBillProductNum sourceSaleBillProductNum = ipmsSaleBillProductNumMapper.selectOne(queryWrapper);
                if (sourceSaleBillProductNum != null) {
                    BigDecimal surplusNeedDeliveryProductNum = sourceSaleBillProductNum.getSurplusNeedDeliveryProductNum();
                    if (surplusNeedDeliveryProductNum.doubleValue() < productNum.doubleValue()) {
                        throw new BusinessException(ErrorCode.PARAMS_ERROR, "销售出库单的出库商品数量超过销售订单的商品数量");
                    }
                    sourceSaleBillProductNum.setSurplusNeedDeliveryProductNum(surplusNeedDeliveryProductNum.subtract(productNum));
                    int updateSuperBill = ipmsSaleBillProductNumMapper.updateById(sourceSaleBillProductNum);
                    if (updateSuperBill != 1) {
                        throw new BusinessException(ErrorCode.SYSTEM_ERROR, "更新上级单据剩余商品数量失败");
                    }
                }
            }
            saleBillProductNum.setNeedDeliveryProductNum(productNum);
            saleBillProductNum.setSurplusNeedDeliveryProductNum(productNum);
        } else if (SaleBillConstant.SALE_RETURN_ORDER.equals(saleBillType)) {
            if (saleSourceBillId != null && saleSourceBillId > 0) {
                // 查找到原单源的商品信息及数量，去修改源单源商品剩余的出库数量
                QueryWrapper<IpmsSaleBillProductNum> queryWrapper = new QueryWrapper<>();
                queryWrapper.eq("sale_bill_id", saleSourceBillId);
                queryWrapper.eq("product_id", productId);
                IpmsSaleBillProductNum sourceSaleBillProductNum = ipmsSaleBillProductNumMapper.selectOne(queryWrapper);
                if (sourceSaleBillProductNum != null) {
                    BigDecimal surplusNeedDeliveryProductNum = sourceSaleBillProductNum.getSurplusNeedDeliveryProductNum();
                    if (surplusNeedDeliveryProductNum.doubleValue() < productNum.doubleValue()) {
                        throw new BusinessException(ErrorCode.PARAMS_ERROR, "销售退货单的退货商品数量超过销售出库单的商品数量");
                    }
                    sourceSaleBillProductNum.setSurplusNeedDeliveryProductNum(surplusNeedDeliveryProductNum.subtract(productNum));
                    int updateSuperBill = ipmsSaleBillProductNumMapper.updateById(sourceSaleBillProductNum);
                    if (updateSuperBill != 1) {
                        throw new BusinessException(ErrorCode.SYSTEM_ERROR, "更新上级单据剩余商品数量失败");
                    }
                }
            }
            saleBillProductNum.setNeedDeliveryProductNum(productNum);
            saleBillProductNum.setSurplusNeedDeliveryProductNum(productNum);
        } else {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "单据类型不合法");
        }
        int result = ipmsSaleBillProductNumMapper.insert(saleBillProductNum);
        if (result != 1) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR);
        }
        return saleBillProductNum.getSaleBillProductId();
    }

    @Override
    public int updateSaleBillProductAndNum(UpdateSaleProductNumRequest updateSaleProductNumRequest, IpmsSaleBill saleBill) {
        Long saleBillProductId = updateSaleProductNumRequest.getSaleBillProductId();
        if (saleBillProductId == null || saleBillProductId < 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "单据商品库存 id 为空或者不合法");
        }
        // 判断销售单据是否存在
        IpmsSaleBillProductNum saleBillProduct = ipmsSaleBillProductNumMapper.selectById(saleBillProductId);
        if (saleBillProduct == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "销售单据商品不存在");
        }
        Long productId = updateSaleProductNumRequest.getProductId();
        if (productId == null || productId < 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "销售单据商品 id 为空或者不合法");
        }
        // 判断要修改成的商品是否存在
        IpmsProduct product = ipmsProductService.getById(productId);
        if (product == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "商品不存在");
        }
        // 仓库是否为空
        Long warehouseId = updateSaleProductNumRequest.getWarehouseId();
        Long warehousePositionId = updateSaleProductNumRequest.getWarehousePositionId();
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
        // 更新对象
        IpmsSaleBillProductNum saleBillProductNum = new IpmsSaleBillProductNum();
        BeanUtils.copyProperties(updateSaleProductNumRequest, saleBillProductNum);
        saleBillProductNum.setUpdateTime(new Date());
        String saleBillType = saleBill.getSaleBillType();
        // 对于销售出库单来说源单是销售订单，对于销售退货单源单是销售出库单
        Long saleSourceBillId = saleBill.getSaleSourceBillId();
        QueryWrapper<IpmsSaleBillProductNum> saleBillProductNumQueryWrapper;
        if (saleBillType != null) {
            switch (saleBillType) {
                case SaleBillConstant.SALE_ORDER:
                    // 销售订单的商品，无特殊操作
                    saleBillProductNum.setNeedDeliveryProductNum(productNum);
                    saleBillProductNum.setSurplusNeedDeliveryProductNum(productNum);
                    break;
                case SaleBillConstant.SALE_DELIVERY_ORDER:
                    // 销售出库单的商品，如果有源单销售订单那么必须修改源单对应商品的剩余商品数量
                    if (saleSourceBillId != null && saleSourceBillId > 0) {
                        saleBillProductNumQueryWrapper = new QueryWrapper<>();
                        saleBillProductNumQueryWrapper.eq("sale_bill_id", saleSourceBillId);
                        saleBillProductNumQueryWrapper.eq("product_id", productId);
                        IpmsSaleBillProductNum saleSourceBillProduct = ipmsSaleBillProductNumMapper.selectOne(saleBillProductNumQueryWrapper);
                        if (saleSourceBillProduct != null) {
                            // 查找到销售订单剩余需要出库的商品数量
                            BigDecimal sourceSurplusNeedDeliveryProductNum = saleSourceBillProduct.getSurplusNeedDeliveryProductNum();
                            // 查找到原来销售出库单需要出库的商品数量
                            BigDecimal oldNeedDeliveryProductNum = saleBillProduct.getNeedDeliveryProductNum();
                            // 现在更新的商品数量 productNum
                            // 得出现在和原来数量的差值
                            BigDecimal differenceValue = productNum.subtract(oldNeedDeliveryProductNum);
                            // 如果大于 0，说明销售出库单据的商品数量变多，销售订单的剩余商品数量应该减少
                            // 但是不能小于 0
                            if (differenceValue.doubleValue() > 0) {
                                BigDecimal reduceAfterSourceSurplusNeedDeliveryProductNum = sourceSurplusNeedDeliveryProductNum.subtract(differenceValue);
                                if (reduceAfterSourceSurplusNeedDeliveryProductNum.doubleValue() < 0) {
                                    throw new BusinessException(ErrorCode.PARAMS_ERROR, "销售订单没有更多的商品可以出库了");
                                }
                                saleSourceBillProduct.setSurplusNeedDeliveryProductNum(reduceAfterSourceSurplusNeedDeliveryProductNum);
                                int updateSuperBill = ipmsSaleBillProductNumMapper.updateById(saleSourceBillProduct);
                                if (updateSuperBill != 1) {
                                    throw new BusinessException(ErrorCode.SYSTEM_ERROR, "更新上级单据剩余商品数量失败");
                                }
                            }
                            if (differenceValue.doubleValue() < 0) {
                                BigDecimal addAfterSourceSurplusNeedDeliveryProductNum = sourceSurplusNeedDeliveryProductNum.add(differenceValue.abs());
                                if (addAfterSourceSurplusNeedDeliveryProductNum.doubleValue() > saleSourceBillProduct.getNeedDeliveryProductNum().doubleValue()) {
                                    throw new BusinessException(ErrorCode.PARAMS_ERROR, "销售订单已经达到可出库的最大商品数量");
                                }
                                saleSourceBillProduct.setSurplusNeedDeliveryProductNum(addAfterSourceSurplusNeedDeliveryProductNum);
                                int updateSuperBill = ipmsSaleBillProductNumMapper.updateById(saleSourceBillProduct);
                                if (updateSuperBill != 1) {
                                    throw new BusinessException(ErrorCode.SYSTEM_ERROR, "更新上级单据剩余商品数量失败");
                                }
                            }
                        }
                    }
                    saleBillProductNum.setNeedDeliveryProductNum(productNum);
                    saleBillProductNum.setSurplusNeedDeliveryProductNum(productNum);
                    break;
                case SaleBillConstant.SALE_RETURN_ORDER:
                    // 销售退货单的商品，如果有源单销售出库单那么必须修改源单对应商品的剩余商品数量
                    if (saleSourceBillId != null && saleSourceBillId > 0) {
                        saleBillProductNumQueryWrapper = new QueryWrapper<>();
                        saleBillProductNumQueryWrapper.eq("sale_bill_id", saleSourceBillId);
                        saleBillProductNumQueryWrapper.eq("product_id", productId);
                        IpmsSaleBillProductNum saleSourceBillProduct = ipmsSaleBillProductNumMapper.selectOne(saleBillProductNumQueryWrapper);
                        if (saleSourceBillProduct != null) {
                            // 查找到销售出库单剩余需要出库的商品数量
                            BigDecimal sourceSurplusNeedDeliveryProductNum = saleSourceBillProduct.getSurplusNeedDeliveryProductNum();
                            // 查找到原来销售退货单需要出库的商品数量
                            BigDecimal oldNeedDeliveryProductNum = saleBillProduct.getNeedDeliveryProductNum();
                            // 现在更新的商品数量 productNum
                            // 得出现在和原来数量的差值
                            BigDecimal differenceValue = productNum.subtract(oldNeedDeliveryProductNum);
                            // 如果大于 0，说明销售出库单据的商品数量变多，销售订单的剩余商品数量应该减少
                            // 但是不能小于 0
                            if (differenceValue.doubleValue() > 0) {
                                BigDecimal reduceAfterSourceSurplusNeedDeliveryProductNum = sourceSurplusNeedDeliveryProductNum.subtract(differenceValue);
                                if (reduceAfterSourceSurplusNeedDeliveryProductNum.doubleValue() < 0) {
                                    throw new BusinessException(ErrorCode.PARAMS_ERROR, "销售出库单没有更多的商品可以退货了");
                                }
                                saleSourceBillProduct.setSurplusNeedDeliveryProductNum(reduceAfterSourceSurplusNeedDeliveryProductNum);
                                int updateSuperBill = ipmsSaleBillProductNumMapper.updateById(saleSourceBillProduct);
                                if (updateSuperBill != 1) {
                                    throw new BusinessException(ErrorCode.SYSTEM_ERROR, "更新上级单据剩余商品数量失败");
                                }
                            }
                            if (differenceValue.doubleValue() < 0) {
                                BigDecimal addAfterSourceSurplusNeedDeliveryProductNum = sourceSurplusNeedDeliveryProductNum.add(differenceValue.abs());
                                if (addAfterSourceSurplusNeedDeliveryProductNum.doubleValue() > saleSourceBillProduct.getNeedDeliveryProductNum().doubleValue()) {
                                    throw new BusinessException(ErrorCode.PARAMS_ERROR, "销售出库单已经达到可退货的最大商品数量");
                                }
                                saleSourceBillProduct.setSurplusNeedDeliveryProductNum(addAfterSourceSurplusNeedDeliveryProductNum);
                                int updateSuperBill = ipmsSaleBillProductNumMapper.updateById(saleSourceBillProduct);
                                if (updateSuperBill != 1) {
                                    throw new BusinessException(ErrorCode.SYSTEM_ERROR, "更新上级单据剩余商品数量失败");
                                }
                            }
                        }
                    }
                    saleBillProductNum.setNeedDeliveryProductNum(productNum);
                    saleBillProductNum.setSurplusNeedDeliveryProductNum(productNum);
                    break;
            }
        }
        int result = ipmsSaleBillProductNumMapper.updateById(saleBillProductNum);
        if (result != 1) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "销售单据商品更新失败");
        }
        return result;
    }
}




