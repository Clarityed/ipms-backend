package com.clarity.ipmsbackend.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.clarity.ipmsbackend.model.dto.productionbill.productnum.AddProductionProductNumRequest;
import com.clarity.ipmsbackend.model.dto.productionbill.productnum.UpdateProductionProductNumRequest;
import com.clarity.ipmsbackend.model.entity.IpmsProductionBill;
import com.clarity.ipmsbackend.model.entity.IpmsProductionBillProductNum;

/**
* @author Clarity
* @description 针对表【ipms_production_bill_product_num(生产单据商品数量表)】的数据库操作Service
* @createDate 2023-03-23 14:42:26
*/
public interface IpmsProductionBillProductNumService extends IService<IpmsProductionBillProductNum> {
    /**
     * 增加生产单据商品
     *
     * @param addProductionProductNumRequest 增加生产单据商品及数量请求封装对象
     * @param productionBill 生产单据对象
     * @return 增加后的记录 id
     */
    long addProductionBillProductAndNum(AddProductionProductNumRequest addProductionProductNumRequest, IpmsProductionBill productionBill);

    /**
     * 修改生产单据商品
     *
     * @param updateProductionProductNumRequest 修改生产单据商品及数量请求封装对象
     * @param productionBill 生产单据对象
     * @return 1 - 成功， 0 - 失败
     */
    int updateProductionBillProductAndNum(UpdateProductionProductNumRequest updateProductionProductNumRequest, IpmsProductionBill productionBill);
}
