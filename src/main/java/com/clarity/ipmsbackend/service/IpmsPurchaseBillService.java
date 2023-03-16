package com.clarity.ipmsbackend.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.clarity.ipmsbackend.model.dto.purchasebill.AddPurchaseBillRequest;
import com.clarity.ipmsbackend.model.entity.IpmsPurchaseBill;

import javax.servlet.http.HttpServletRequest;

/**
* @author Clarity
* @description 针对表【ipms_purchase_bill(采购单据)】的数据库操作Service
* @createDate 2023-03-13 15:59:08
*/
public interface IpmsPurchaseBillService extends IService<IpmsPurchaseBill> {

    /**
     * 采购单据编号自动生成
     *
     * @param purchaseBillType 采购单据类型
     * @return 对于单据的下一个编号
     */
    String purchaseBillCodeAutoGenerate(String purchaseBillType);

    /**
     * 增加采购单据
     *
     * @param addPurchaseBillRequest 增加采购单据请求封装对象
     * @param request HttpServletRequest
     * @return 1 - 成功， 0 - 失败
     */
    int addPurchaseBill(AddPurchaseBillRequest addPurchaseBillRequest, HttpServletRequest request);

    /**
     * 审核采购单据
     *
     * @param purchaseBillId 采购单据 id
     * @return 1 - 成功， 0 - 失败
     */
    int checkPurchaseBill(long purchaseBillId, HttpServletRequest request);

    /**
     * 反审核采购单据
     *
     * @param purchaseBillId 采购单据 id
     * @return 1 - 成功， 0 - 失败
     */
    int reverseCheckPurchaseBill(long purchaseBillId, HttpServletRequest request);

    /**
     * 根据 id 删除 采购单据
     *
     * @param id 单据 id
     * @return 1 - 成功， 0 - 失败
     */
    int deletePurchaseBillById(long id);
}
