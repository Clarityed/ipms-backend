package com.clarity.ipmsbackend.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.clarity.ipmsbackend.model.dto.purchasebill.productnum.AddProductNumRequest;
import com.clarity.ipmsbackend.model.dto.purchasebill.productnum.UpdateProductNumRequest;
import com.clarity.ipmsbackend.model.entity.IpmsPurchaseBill;
import com.clarity.ipmsbackend.model.entity.IpmsPurchaseBillProductNum;

/**
* @author Clarity
* @description 针对表【ipms_purchase_bill_product_num(采购单据商品数量表)】的数据库操作Service
* @createDate 2023-03-13 16:00:04
*/
public interface IpmsPurchaseBillProductNumService extends IService<IpmsPurchaseBillProductNum> {

    /**
     * 增加采购单据商品
     *
     * @param addProductNumRequest 增加采购单据商品及数量请求封装对象
     * @param purchaseBill 采购单据对象
     * @return 增加后的记录 id
     */
    long addPurchaseBillProductAndNum(AddProductNumRequest addProductNumRequest, IpmsPurchaseBill purchaseBill);

    /**
     * 修改采购单据商品
     *
     * @param updateProductNumRequest 修改采购单据商品及数量请求封装对象
     * @return 1 - 成功， 0 - 失败
     */
    int updatePurchaseBillProductAndNum(UpdateProductNumRequest updateProductNumRequest, IpmsPurchaseBill purchaseBill);
}
