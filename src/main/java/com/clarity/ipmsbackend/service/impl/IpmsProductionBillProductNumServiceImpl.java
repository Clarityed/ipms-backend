package com.clarity.ipmsbackend.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.clarity.ipmsbackend.common.ErrorCode;
import com.clarity.ipmsbackend.constant.ProductionBillConstant;
import com.clarity.ipmsbackend.constant.WarehouseConstant;
import com.clarity.ipmsbackend.exception.BusinessException;
import com.clarity.ipmsbackend.mapper.IpmsProductionBillProductNumMapper;
import com.clarity.ipmsbackend.model.dto.productionbill.productnum.AddProductionProductNumRequest;
import com.clarity.ipmsbackend.model.dto.productionbill.productnum.UpdateProductionProductNumRequest;
import com.clarity.ipmsbackend.model.entity.*;
import com.clarity.ipmsbackend.service.*;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.Date;

/**
 * @author Clarity
 * @description 针对表【ipms_production_bill_product_num(生产单据商品数量表)】的数据库操作Service实现
 * @createDate 2023-03-23 14:42:26
 */
@Service
public class IpmsProductionBillProductNumServiceImpl extends ServiceImpl<IpmsProductionBillProductNumMapper, IpmsProductionBillProductNum>
        implements IpmsProductionBillProductNumService {

    @Resource
    private IpmsProductionBillProductNumMapper ipmsProductionBillProductNumMapper;

    @Resource
    private IpmsProductService ipmsProductService;

    @Resource
    private IpmsWarehouseService ipmsWarehouseService;

    @Resource
    private IpmsWarehousePositionService ipmsWarehousePositionService;

    @Resource
    private IpmsProductionBillService ipmsProductionBillService;

    @Override
    public long addProductionBillProductAndNum(AddProductionProductNumRequest addProductionProductNumRequest, IpmsProductionBill productionBill) {
        // 1. 商品 id，不能为空，必须存在
        Long productId = addProductionProductNumRequest.getProductId();
        if (productId == null || productId <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "商品 id 为空或者不合法");
        }
        IpmsProduct validProduct = ipmsProductService.getById(productId);
        if (validProduct == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "商品不存在");
        }
        // 2. 仓库 id，不能为空，必须存在
        Long warehouseId = addProductionProductNumRequest.getWarehouseId();
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
        Long warehousePositionId = addProductionProductNumRequest.getWarehousePositionId();
        if (isWarehousePositionManagement == WarehouseConstant.OPEN_WAREHOUSE_POSITION_MANAGEMENT) {
            if (warehousePositionId == null || warehousePositionId <= 0) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "仓位 id 为空或者不合法");
            }
            IpmsWarehousePosition validWarehousePosition = ipmsWarehousePositionService.getById(warehousePositionId);
            if (validWarehousePosition == null) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "仓位不存在");
            }
        } else {
            if (warehousePositionId != null) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "该仓库未开启仓位管理，不应该传递仓位 id");
            }
        }
        // 4. 商品数量，不能为空，且必须大于 0
        BigDecimal productNum = addProductionProductNumRequest.getProductNum();
        if (productNum == null || productNum.doubleValue() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "商品数量为空或者小于等于 0");
        }
        // 6. 增加数据，并且设置其他固定字段。
        IpmsProductionBillProductNum productionBillProductNum = new IpmsProductionBillProductNum();
        BeanUtils.copyProperties(addProductionProductNumRequest, productionBillProductNum);
        productionBillProductNum.setProductionBillId(productionBill.getProductionBillId());
        productionBillProductNum.setCreateTime(new Date());
        productionBillProductNum.setUpdateTime(new Date());
        String productionBillType = productionBill.getProductionBillType();
        Long productionSourceBillId = productionBill.getProductionSourceBillId();
        if (ProductionBillConstant.PRODUCTION_TASK_ORDER.equals(productionBillType)) {
            productionBillProductNum.setNeedExecutionProductNum(productNum);
            productionBillProductNum.setSurplusNeedExecutionProductNum(productNum);
            BigDecimal productMaterialMole = addProductionProductNumRequest.getProductMaterialMole();
            if (productMaterialMole == null || productMaterialMole.doubleValue() <= 0) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "商品材料分子为空或者数量小于等于 0");
            }
            productionBillProductNum.setProductMaterialMole(productMaterialMole);
        } else if (ProductionBillConstant.PRODUCTION_PICKING_ORDER.equals(productionBillType)) {
            if (productionSourceBillId != null && productionSourceBillId > 0) {
                // 查找到原单源的商品信息及数量，去修改源单源商品剩余的入库数量
                QueryWrapper<IpmsProductionBillProductNum> queryWrapper = new QueryWrapper<>();
                queryWrapper.eq("production_bill_id", productionSourceBillId);
                queryWrapper.eq("product_id", productId);
                IpmsProductionBillProductNum sourceProductionBillProductNum = ipmsProductionBillProductNumMapper.selectOne(queryWrapper);
                if (sourceProductionBillProductNum != null) {
                    BigDecimal surplusNeedExecutionProductNum = sourceProductionBillProductNum.getSurplusNeedExecutionProductNum();
                    if (surplusNeedExecutionProductNum.doubleValue() < productNum.doubleValue()) {
                        throw new BusinessException(ErrorCode.PARAMS_ERROR, "生产领料单的领料商品数量超过生产任务单的商品数量");
                    }
                    sourceProductionBillProductNum.setSurplusNeedExecutionProductNum(surplusNeedExecutionProductNum.subtract(productNum));
                    int updateSuperBill = ipmsProductionBillProductNumMapper.updateById(sourceProductionBillProductNum);
                    if (updateSuperBill != 1) {
                        throw new BusinessException(ErrorCode.SYSTEM_ERROR, "更新上级单据剩余商品数量失败");
                    }
                }
            }
            productionBillProductNum.setNeedExecutionProductNum(productNum);
            productionBillProductNum.setSurplusNeedExecutionProductNum(productNum);
        } else if (ProductionBillConstant.PRODUCTION_RETURN_ORDER.equals(productionBillType)) {
            if (productionSourceBillId != null && productionSourceBillId > 0) {
                // 查找到原单源的商品信息及数量，去修改源单源商品剩余的入库数量
                QueryWrapper<IpmsProductionBillProductNum> queryWrapper = new QueryWrapper<>();
                queryWrapper.eq("production_bill_id", productionSourceBillId);
                queryWrapper.eq("product_id", productId);
                IpmsProductionBillProductNum sourceProductionBillProductNum = ipmsProductionBillProductNumMapper.selectOne(queryWrapper);
                if (sourceProductionBillProductNum != null) {
                    BigDecimal surplusNeedExecutionProductNum = sourceProductionBillProductNum.getSurplusNeedExecutionProductNum();
                    if (surplusNeedExecutionProductNum.doubleValue() < productNum.doubleValue()) {
                        throw new BusinessException(ErrorCode.PARAMS_ERROR, "生产退料单的退料商品数量超过生产领料单的商品数量");
                    }
                    sourceProductionBillProductNum.setSurplusNeedExecutionProductNum(surplusNeedExecutionProductNum.subtract(productNum));
                    int updateSuperBill = ipmsProductionBillProductNumMapper.updateById(sourceProductionBillProductNum);
                    if (updateSuperBill != 1) {
                        throw new BusinessException(ErrorCode.SYSTEM_ERROR, "更新上级单据剩余商品数量失败");
                    }
                }
            }
            productionBillProductNum.setNeedExecutionProductNum(productNum);
            productionBillProductNum.setSurplusNeedExecutionProductNum(productNum);
        } else if (ProductionBillConstant.PRODUCTION_RECEIPT_ORDER.equals(productionBillType)) {
            if (productionSourceBillId != null && productionSourceBillId > 0) {
                // 查找到原单源的商品信息及数量，去修改源单源商品剩余的入库数量
                IpmsProductionBill sourceProductionBill = ipmsProductionBillService.getById(productionSourceBillId);
                if (productId.equals(sourceProductionBill.getProductId()) && productionSourceBillId.equals(sourceProductionBill.getProductionBillId())) {
                    BigDecimal surplusNeedWarehousingProductNum = sourceProductionBill.getSurplusNeedWarehousingProductNum();
                    if (surplusNeedWarehousingProductNum.doubleValue() < productNum.doubleValue()) {
                        throw new BusinessException(ErrorCode.PARAMS_ERROR, "生产入库单的入库商品数量超过生产任务单的商品数量");
                    }
                    sourceProductionBill.setSurplusNeedWarehousingProductNum(surplusNeedWarehousingProductNum.subtract(productNum));
                    // 循环依赖出现
                    boolean updateSuperBill = ipmsProductionBillService.updateById(sourceProductionBill);
                    if (!updateSuperBill) {
                        throw new BusinessException(ErrorCode.SYSTEM_ERROR, "更新上级单据剩余商品数量失败");
                    }
                }
            }
            productionBillProductNum.setNeedExecutionProductNum(productNum);
            productionBillProductNum.setSurplusNeedExecutionProductNum(productNum);
        } else if (ProductionBillConstant.PRODUCTION_STOCK_RETURN_ORDER.equals(productionBillType)) {
            if (productionSourceBillId != null && productionSourceBillId > 0) {
                // 查找到原单源的商品信息及数量，去修改源单源商品剩余的入库数量
                QueryWrapper<IpmsProductionBillProductNum> queryWrapper = new QueryWrapper<>();
                queryWrapper.eq("production_bill_id", productionSourceBillId);
                queryWrapper.eq("product_id", productId);
                IpmsProductionBillProductNum sourceProductionBillProductNum = ipmsProductionBillProductNumMapper.selectOne(queryWrapper);
                if (sourceProductionBillProductNum != null) {
                    BigDecimal surplusNeedExecutionProductNum = sourceProductionBillProductNum.getSurplusNeedExecutionProductNum();
                    if (surplusNeedExecutionProductNum.doubleValue() < productNum.doubleValue()) {
                        throw new BusinessException(ErrorCode.PARAMS_ERROR, "生产退库单的退库商品数量超过生产入库单的商品数量");
                    }
                    sourceProductionBillProductNum.setSurplusNeedExecutionProductNum(surplusNeedExecutionProductNum.subtract(productNum));
                    int updateSuperBill = ipmsProductionBillProductNumMapper.updateById(sourceProductionBillProductNum);
                    if (updateSuperBill != 1) {
                        throw new BusinessException(ErrorCode.SYSTEM_ERROR, "更新上级单据剩余商品数量失败");
                    }
                }
            }
            productionBillProductNum.setNeedExecutionProductNum(productNum);
            productionBillProductNum.setSurplusNeedExecutionProductNum(productNum);
        } else {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "单据类型不合法");
        }
        int result = ipmsProductionBillProductNumMapper.insert(productionBillProductNum);
        if (result != 1) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR);
        }
        return productionBillProductNum.getProductionBillProductId();
    }

    @Override
    public int updateProductionBillProductAndNum(UpdateProductionProductNumRequest updateProductionProductNumRequest, IpmsProductionBill productionBill) {
        Long productionBillProductId = updateProductionProductNumRequest.getProductionBillProductId();
        if (productionBillProductId == null || productionBillProductId < 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "单据商品库存 id 为空或者不合法");
        }
        // 判断生产单据是否存在
        IpmsProductionBillProductNum productionBillProduct = ipmsProductionBillProductNumMapper.selectById(productionBillProductId);
        if (productionBillProduct == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "生产单据商品不存在");
        }
        Long productId = updateProductionProductNumRequest.getProductId();
        if (productId == null || productId < 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "生产单据商品 id 为空或者不合法");
        }
        // 判断要修改成的商品是否存在
        IpmsProduct product = ipmsProductService.getById(productId);
        if (product == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "商品不存在");
        }
        // 仓库是否为空
        Long warehouseId = updateProductionProductNumRequest.getWarehouseId();
        Long warehousePositionId = updateProductionProductNumRequest.getWarehousePositionId();
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
        BigDecimal productNum = updateProductionProductNumRequest.getProductNum();
        if (productNum == null || productNum.doubleValue() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "商品数量为空");
        }
        // 更新对象
        IpmsProductionBillProductNum productionBillProductNum = new IpmsProductionBillProductNum();
        BeanUtils.copyProperties(updateProductionProductNumRequest, productionBillProductNum);
        productionBillProductNum.setUpdateTime(new Date());
        String productionBillType = productionBill.getProductionBillType();
        // 对于生产领料单来说源单是生产任务单，对于生产退货单源单是生产领料单
        Long productionSourceBillId = productionBill.getProductionSourceBillId();
        QueryWrapper<IpmsProductionBillProductNum> productionBillProductNumQueryWrapper;
        if (productionBillType != null) {
            switch (productionBillType) {
                case ProductionBillConstant.PRODUCTION_TASK_ORDER:
                    // 生产任务单的商品，无特殊操作
                    BigDecimal productMaterialMole = updateProductionProductNumRequest.getProductMaterialMole();
                    if (productMaterialMole != null) {
                        if (productMaterialMole.doubleValue() <= 0) {
                            throw new BusinessException(ErrorCode.PARAMS_ERROR, "商品材料分子小于等于 0");
                        }
                    }
                    productionBillProductNum.setNeedExecutionProductNum(productNum);
                    productionBillProductNum.setSurplusNeedExecutionProductNum(productNum);
                    break;
                case ProductionBillConstant.PRODUCTION_PICKING_ORDER:
                    // 生产领料单的商品，如果有源单生产任务单那么必须修改源单对应商品的剩余商品数量
                    if (productionSourceBillId != null && productionSourceBillId > 0) {
                        productionBillProductNumQueryWrapper = new QueryWrapper<>();
                        productionBillProductNumQueryWrapper.eq("production_bill_id", productionSourceBillId);
                        productionBillProductNumQueryWrapper.eq("product_id", productId);
                        IpmsProductionBillProductNum productionSourceBillProduct = ipmsProductionBillProductNumMapper.selectOne(productionBillProductNumQueryWrapper);
                        if (productionSourceBillProduct != null) {
                            // 查找到生产任务单剩余需要出库的商品数量
                            BigDecimal sourceSurplusNeedExecutionProductNum = productionSourceBillProduct.getSurplusNeedExecutionProductNum();
                            // 查找到原来生产领料单需要出库的商品数量
                            BigDecimal oldNeedExecutionProductNum = productionBillProduct.getNeedExecutionProductNum();
                            // 现在更新的商品数量 productNum
                            // 得出现在和原来数量的差值
                            BigDecimal differenceValue = productNum.subtract(oldNeedExecutionProductNum);
                            // 如果大于 0，说明生产领料单据的商品数量变多，生产任务单的剩余商品数量应该减少
                            // 但是不能小于 0
                            if (differenceValue.doubleValue() > 0) {
                                BigDecimal reduceAfterSourceSurplusNeedExecutionProductNum = sourceSurplusNeedExecutionProductNum.subtract(differenceValue);
                                if (reduceAfterSourceSurplusNeedExecutionProductNum.doubleValue() < 0) {
                                    throw new BusinessException(ErrorCode.PARAMS_ERROR, "生产任务单没有更多的商品可以出库了");
                                }
                                productionSourceBillProduct.setSurplusNeedExecutionProductNum(reduceAfterSourceSurplusNeedExecutionProductNum);
                                int updateSuperBill = ipmsProductionBillProductNumMapper.updateById(productionSourceBillProduct);
                                if (updateSuperBill != 1) {
                                    throw new BusinessException(ErrorCode.SYSTEM_ERROR, "更新上级单据剩余商品数量失败");
                                }
                            }
                            if (differenceValue.doubleValue() < 0) {
                                BigDecimal addAfterSourceSurplusNeedDeliveryProductNum = sourceSurplusNeedExecutionProductNum.add(differenceValue.abs());
                                if (addAfterSourceSurplusNeedDeliveryProductNum.doubleValue() > productionSourceBillProduct.getNeedExecutionProductNum().doubleValue()) {
                                    throw new BusinessException(ErrorCode.PARAMS_ERROR, "生产任务单已经达到可出库的最大商品数量");
                                }
                                productionSourceBillProduct.setSurplusNeedExecutionProductNum(addAfterSourceSurplusNeedDeliveryProductNum);
                                int updateSuperBill = ipmsProductionBillProductNumMapper.updateById(productionSourceBillProduct);
                                if (updateSuperBill != 1) {
                                    throw new BusinessException(ErrorCode.SYSTEM_ERROR, "更新上级单据剩余商品数量失败");
                                }
                            }
                        }
                    }
                    productionBillProductNum.setNeedExecutionProductNum(productNum);
                    productionBillProductNum.setSurplusNeedExecutionProductNum(productNum);
                    break;
                case ProductionBillConstant.PRODUCTION_RETURN_ORDER:
                    // 生产退料单的商品，如果有源单生产领料单那么必须修改源单对应商品的剩余商品数量
                    if (productionSourceBillId != null && productionSourceBillId > 0) {
                        productionBillProductNumQueryWrapper = new QueryWrapper<>();
                        productionBillProductNumQueryWrapper.eq("production_bill_id", productionSourceBillId);
                        productionBillProductNumQueryWrapper.eq("product_id", productId);
                        IpmsProductionBillProductNum productionSourceBillProduct = ipmsProductionBillProductNumMapper.selectOne(productionBillProductNumQueryWrapper);
                        if (productionSourceBillProduct != null) {
                            // 查找到生产领料单剩余需要出库的商品数量
                            BigDecimal sourceSurplusNeedExecutionProductNum = productionSourceBillProduct.getSurplusNeedExecutionProductNum();
                            // 查找到原来生产退料单需要出库的商品数量
                            BigDecimal oldNeedExecutionProductNum = productionBillProduct.getNeedExecutionProductNum();
                            // 现在更新的商品数量 productNum
                            // 得出现在和原来数量的差值
                            BigDecimal differenceValue = productNum.subtract(oldNeedExecutionProductNum);
                            // 如果大于 0，说明生产领料单据的商品数量变多，生产订单的剩余商品数量应该减少
                            // 但是不能小于 0
                            if (differenceValue.doubleValue() > 0) {
                                BigDecimal reduceAfterSourceSurplusNeedExecutionProductNum = sourceSurplusNeedExecutionProductNum.subtract(differenceValue);
                                if (reduceAfterSourceSurplusNeedExecutionProductNum.doubleValue() < 0) {
                                    throw new BusinessException(ErrorCode.PARAMS_ERROR, "生产领料单没有更多的商品可以退货了");
                                }
                                productionSourceBillProduct.setSurplusNeedExecutionProductNum(reduceAfterSourceSurplusNeedExecutionProductNum);
                                int updateSuperBill = ipmsProductionBillProductNumMapper.updateById(productionSourceBillProduct);
                                if (updateSuperBill != 1) {
                                    throw new BusinessException(ErrorCode.SYSTEM_ERROR, "更新上级单据剩余商品数量失败");
                                }
                            }
                            if (differenceValue.doubleValue() < 0) {
                                BigDecimal addAfterSourceSurplusNeedDeliveryProductNum = sourceSurplusNeedExecutionProductNum.add(differenceValue.abs());
                                if (addAfterSourceSurplusNeedDeliveryProductNum.doubleValue() > productionSourceBillProduct.getNeedExecutionProductNum().doubleValue()) {
                                    throw new BusinessException(ErrorCode.PARAMS_ERROR, "生产领料单已经达到可退货的最大商品数量");
                                }
                                productionSourceBillProduct.setSurplusNeedExecutionProductNum(addAfterSourceSurplusNeedDeliveryProductNum);
                                int updateSuperBill = ipmsProductionBillProductNumMapper.updateById(productionSourceBillProduct);
                                if (updateSuperBill != 1) {
                                    throw new BusinessException(ErrorCode.SYSTEM_ERROR, "更新上级单据剩余商品数量失败");
                                }
                            }
                        }
                    }
                    productionBillProductNum.setNeedExecutionProductNum(productNum);
                    productionBillProductNum.setSurplusNeedExecutionProductNum(productNum);
                    break;
                case ProductionBillConstant.PRODUCTION_RECEIPT_ORDER:
                    // 生产入库单的商品，如果有源单生产任务单那么必须修改源单对应商品的剩余商品数量
                    if (productionSourceBillId != null && productionSourceBillId > 0) {
                        IpmsProductionBill productionSourceBillProduct = ipmsProductionBillService.getById(productionSourceBillId);
                        if (productionSourceBillProduct != null) {
                            // 查找到生产任务单剩余需要出库的商品数量
                            BigDecimal sourceSurplusNeedWarehousingProductNum = productionSourceBillProduct.getSurplusNeedWarehousingProductNum();
                            // 查找到原来生产入库单需要出库的商品数量
                            BigDecimal oldNeedExecutionProductNum = productionBillProduct.getNeedExecutionProductNum();
                            // 现在更新的商品数量 productNum
                            // 得出现在和原来数量的差值
                            BigDecimal differenceValue = productNum.subtract(oldNeedExecutionProductNum);
                            // 如果大于 0，说明生产入库单据的商品数量变多，生产任务单的剩余商品数量应该减少
                            // 但是不能小于 0
                            if (differenceValue.doubleValue() > 0) {
                                BigDecimal reduceAfterSourceSurplusNeedExecutionProductNum = sourceSurplusNeedWarehousingProductNum.subtract(differenceValue);
                                if (reduceAfterSourceSurplusNeedExecutionProductNum.doubleValue() < 0) {
                                    throw new BusinessException(ErrorCode.PARAMS_ERROR, "生产任务单没有更多的商品可以出库了");
                                }
                                productionSourceBillProduct.setSurplusNeedWarehousingProductNum(reduceAfterSourceSurplusNeedExecutionProductNum);
                                boolean result = ipmsProductionBillService.updateById(productionSourceBillProduct);
                                if (!result) {
                                    throw new BusinessException(ErrorCode.SYSTEM_ERROR, "更新上级单据剩余商品数量失败");
                                }
                            }
                            if (differenceValue.doubleValue() < 0) {
                                BigDecimal addAfterSourceSurplusNeedDeliveryProductNum = sourceSurplusNeedWarehousingProductNum.add(differenceValue.abs());
                                if (addAfterSourceSurplusNeedDeliveryProductNum.doubleValue() > productionSourceBillProduct.getNeedWarehousingProductNum().doubleValue()) {
                                    throw new BusinessException(ErrorCode.PARAMS_ERROR, "生产任务单已经达到可出库的最大商品数量");
                                }
                                productionSourceBillProduct.setSurplusNeedWarehousingProductNum(addAfterSourceSurplusNeedDeliveryProductNum);
                                boolean result = ipmsProductionBillService.updateById(productionSourceBillProduct);
                                if (!result) {
                                    throw new BusinessException(ErrorCode.SYSTEM_ERROR, "更新上级单据剩余商品数量失败");
                                }
                            }
                        }
                    }
                    productionBillProductNum.setNeedExecutionProductNum(productNum);
                    productionBillProductNum.setSurplusNeedExecutionProductNum(productNum);
                    break;
                case ProductionBillConstant.PRODUCTION_STOCK_RETURN_ORDER:
                    // 生产退库单的商品，如果有源单生产入库单那么必须修改源单对应商品的剩余商品数量
                    if (productionSourceBillId != null && productionSourceBillId > 0) {
                        productionBillProductNumQueryWrapper = new QueryWrapper<>();
                        productionBillProductNumQueryWrapper.eq("production_bill_id", productionSourceBillId);
                        productionBillProductNumQueryWrapper.eq("product_id", productId);
                        IpmsProductionBillProductNum productionSourceBillProduct = ipmsProductionBillProductNumMapper.selectOne(productionBillProductNumQueryWrapper);
                        if (productionSourceBillProduct != null) {
                            // 查找到生产入库单剩余需要出库的商品数量
                            BigDecimal sourceSurplusNeedExecutionProductNum = productionSourceBillProduct.getSurplusNeedExecutionProductNum();
                            // 查找到原来生产退库单需要出库的商品数量
                            BigDecimal oldNeedExecutionProductNum = productionBillProduct.getNeedExecutionProductNum();
                            // 现在更新的商品数量 productNum
                            // 得出现在和原来数量的差值
                            BigDecimal differenceValue = productNum.subtract(oldNeedExecutionProductNum);
                            // 如果大于 0，说明生产入库单据的商品数量变多，生产订单的剩余商品数量应该减少
                            // 但是不能小于 0
                            if (differenceValue.doubleValue() > 0) {
                                BigDecimal reduceAfterSourceSurplusNeedExecutionProductNum = sourceSurplusNeedExecutionProductNum.subtract(differenceValue);
                                if (reduceAfterSourceSurplusNeedExecutionProductNum.doubleValue() < 0) {
                                    throw new BusinessException(ErrorCode.PARAMS_ERROR, "生产入库单没有更多的商品可以退货了");
                                }
                                productionSourceBillProduct.setSurplusNeedExecutionProductNum(reduceAfterSourceSurplusNeedExecutionProductNum);
                                int updateSuperBill = ipmsProductionBillProductNumMapper.updateById(productionSourceBillProduct);
                                if (updateSuperBill != 1) {
                                    throw new BusinessException(ErrorCode.SYSTEM_ERROR, "更新上级单据剩余商品数量失败");
                                }
                            }
                            if (differenceValue.doubleValue() < 0) {
                                BigDecimal addAfterSourceSurplusNeedDeliveryProductNum = sourceSurplusNeedExecutionProductNum.add(differenceValue.abs());
                                if (addAfterSourceSurplusNeedDeliveryProductNum.doubleValue() > productionSourceBillProduct.getNeedExecutionProductNum().doubleValue()) {
                                    throw new BusinessException(ErrorCode.PARAMS_ERROR, "生产入库单已经达到可退货的最大商品数量");
                                }
                                productionSourceBillProduct.setSurplusNeedExecutionProductNum(addAfterSourceSurplusNeedDeliveryProductNum);
                                int updateSuperBill = ipmsProductionBillProductNumMapper.updateById(productionSourceBillProduct);
                                if (updateSuperBill != 1) {
                                    throw new BusinessException(ErrorCode.SYSTEM_ERROR, "更新上级单据剩余商品数量失败");
                                }
                            }
                        }
                    }
                    productionBillProductNum.setNeedExecutionProductNum(productNum);
                    productionBillProductNum.setSurplusNeedExecutionProductNum(productNum);
                    break;
            }
        }
        int result = ipmsProductionBillProductNumMapper.updateById(productionBillProductNum);
        if (result != 1) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "生产单据商品更新失败");
        }
        return result;
    }
}




