package com.clarity.ipmsbackend.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.clarity.ipmsbackend.model.entity.IpmsProductInventory;
import com.clarity.ipmsbackend.model.entity.IpmsPurchaseBillProductNum;

import java.math.BigDecimal;

/**
* @author Clarity
* @description 针对表【ipms_product_inventory(商品库存表)】的数据库操作Service
* @createDate 2023-03-13 15:58:53
*/
public interface IpmsProductInventoryService extends IService<IpmsProductInventory> {

    /**
     * 增加商品库存
     *
     * @param purchaseBillProductNum 商品及商品数量
     * @return 单据中的某一个本次商品总价
     */
    BigDecimal addProductInventory(IpmsPurchaseBillProductNum purchaseBillProductNum);

    /**
     * 减少商品库存
     *
     * @param purchaseBillProductNum 商品及商品数量
     * @return 单据中的某一个本次商品总价
     */
    BigDecimal reduceProductInventory(IpmsPurchaseBillProductNum purchaseBillProductNum);

    /**
     * 提供一个方法对仓库的记录进行删除，但是只允许删除剩余库存数为 0 的库存记录，为负库存，代表使用超过了当前库存，应该采购
     * 而使用还未到零的说明还有库存不能直接删除，否则会导致数据和现实的仓库存储不一致。
     * 根据 id 删除，目前系统用不到，如果需求改变有需要的提供该方法的接口。
     *
     * @param id 商品库存记录 id
     * @return 1 - 成功， 0 - 失败
     */
    int deleteProductInventoryRecord(long id);
}
