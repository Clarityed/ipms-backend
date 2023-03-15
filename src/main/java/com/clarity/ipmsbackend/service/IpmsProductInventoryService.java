package com.clarity.ipmsbackend.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.clarity.ipmsbackend.model.entity.IpmsProductInventory;
import com.clarity.ipmsbackend.model.entity.IpmsPurchaseBillProductNum;

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
     * @return 1 - 成功， 0 - 失败
     */
    int addProductInventory(IpmsPurchaseBillProductNum purchaseBillProductNum);

    /**
     * 减少商品库存
     *
     * @param purchaseBillProductNum 商品及商品数量
     * @return 1 - 成功， 0 - 失败
     */
    int reduceProductInventory(IpmsPurchaseBillProductNum purchaseBillProductNum);
}
