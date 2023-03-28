package com.clarity.ipmsbackend.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.clarity.ipmsbackend.model.entity.*;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.util.List;

/**
* @author Clarity
* @description 针对表【ipms_product_inventory(商品库存表)】的数据库操作Service
* @createDate 2023-03-13 15:58:53
*/
public interface IpmsProductInventoryService extends IService<IpmsProductInventory> {

    /**
     * 采购单据增加商品库存
     *
     * @param purchaseBillProductNum 采购单据商品及商品数量
     * @param purchaseBillExchangeRate 金额汇率
     * @return 单据中的某一个本次商品总价
     */
    BigDecimal addProductInventory(IpmsPurchaseBillProductNum purchaseBillProductNum, BigDecimal purchaseBillExchangeRate);

    /**
     * 采购单据减少商品库存
     *
     * @param purchaseBillProductNum 采购单据商品及商品数量
     * @param purchaseBillExchangeRate 金额汇率
     * @return 单据中的某一个本次商品总价
     */
    BigDecimal reduceProductInventory(IpmsPurchaseBillProductNum purchaseBillProductNum, BigDecimal purchaseBillExchangeRate);

    /**
     * 销售单据增加商品库存
     *
     * @param saleBillProductNum 销售单据商品及商品数量
     * @param saleBillExchangeRate 金额汇率
     * @return 单据中的某一个本次商品总价
     */
    BigDecimal addProductInventory(IpmsSaleBillProductNum saleBillProductNum, BigDecimal saleBillExchangeRate);

    /**
     * 销售单据减少商品库存
     *
     * @param saleBillProductNum 销售单据商品及商品数量
     * @param saleBillExchangeRate 金额汇率
     * @return 单据中的某一个本次商品总价
     */
    BigDecimal reduceProductInventory(IpmsSaleBillProductNum saleBillProductNum, BigDecimal saleBillExchangeRate);

    /**
     * 生产单据增加商品库存（增加商品时注意可能是全新的父级，必须要有商品成本及商品数量）
     *
     * @param productionBillProductNum 生产单据商品及商品数量
     * @return 1 - 成功， 0 - 失败
     */
    int addProductInventory(IpmsProductionBillProductNum productionBillProductNum, List<IpmsProductionBillProductNum> taskOrderProductList);

    /**
     * 生产单据减少商品库存
     *
     * @param productionBillProductNum 生产单据商品及商品数量
     * @return 1 - 成功， 0 - 失败
     */
    int reduceProductInventory(IpmsProductionBillProductNum productionBillProductNum);

    /**
     * 库存单据增加商品库存
     *
     * @param inventoryBillProductNum 库存单据商品及商品数量
     * @return 1 - 成功， 0 - 失败
     */
    int addProductInventory(IpmsInventoryBillProductNum inventoryBillProductNum);

    /**
     * 库存单据减少商品库存
     *
     * @param inventoryBillProductNum 库存单据商品及商品数量
     * @return 1 - 成功， 0 - 失败
     */
    int reduceProductInventory(IpmsInventoryBillProductNum inventoryBillProductNum);

    /**
     * 提供一个方法对仓库的记录进行删除，但是只允许删除剩余库存数为 0 的库存记录，为负库存，代表使用超过了当前库存，应该采购
     * 而使用还未到零的说明还有库存不能直接删除，否则会导致数据和现实的仓库存储不一致。
     * 根据 id 删除，目前系统用不到，如果需求改变有需要的提供该方法的接口。
     *
     * @param id 商品库存记录 id
     * @return 1 - 成功， 0 - 失败
     */
    int deleteProductInventoryRecord(long id);

    /**
     * 根据商品 id 和仓库 id 获取对应商品可用库存量（仓位 id 可能存在，存在的话必须传递）
     *
     * @param productId 商品 id
     * @param warehouseId 仓库 id
     * @param warehousePositionId 仓位 id
     * @return 对应商品的可用库存
     */
    BigDecimal getAvailableInventoryOfProduct(long productId, long warehouseId, Long warehousePositionId, HttpServletRequest request);
}
