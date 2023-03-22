package com.clarity.ipmsbackend.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.clarity.ipmsbackend.model.dto.salebill.productnum.AddSaleProductNumRequest;
import com.clarity.ipmsbackend.model.dto.salebill.productnum.UpdateSaleProductNumRequest;
import com.clarity.ipmsbackend.model.entity.IpmsSaleBill;
import com.clarity.ipmsbackend.model.entity.IpmsSaleBillProductNum;

/**
* @author Clarity
* @description 针对表【ipms_sale_bill_product_num(销售单据商品数量表)】的数据库操作Service
* @createDate 2023-03-21 10:28:13
*/
public interface IpmsSaleBillProductNumService extends IService<IpmsSaleBillProductNum> {

    /**
     * 增加销售单据商品
     *
     * @param addSaleProductNumRequest 增加销售单据商品及数量请求封装对象
     * @param saleBill 销售单据对象
     * @return 增加后的记录 id
     */
    long addSaleBillProductAndNum(AddSaleProductNumRequest addSaleProductNumRequest, IpmsSaleBill saleBill);

    /**
     * 修改销售单据商品
     *
     * @param updateSaleProductNumRequest 修改销售单据商品及数量请求封装对象
     * @param saleBill 销售单据对象
     * @return 1 - 成功， 0 - 失败
     */
    int updateSaleBillProductAndNum(UpdateSaleProductNumRequest updateSaleProductNumRequest, IpmsSaleBill saleBill);
}
