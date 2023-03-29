package com.clarity.ipmsbackend.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.clarity.ipmsbackend.common.FuzzyQueryRequest;
import com.clarity.ipmsbackend.model.dto.inventorybill.otherdeliveryorder.AddOtherDeliveryOrderRequest;
import com.clarity.ipmsbackend.model.dto.inventorybill.otherdeliveryorder.UpdateOtherDeliveryOrderRequest;
import com.clarity.ipmsbackend.model.dto.inventorybill.otherreceiptorder.AddOtherReceiptOrderRequest;
import com.clarity.ipmsbackend.model.dto.inventorybill.otherreceiptorder.UpdateOtherReceiptOrderRequest;
import com.clarity.ipmsbackend.model.entity.IpmsInventoryBill;
import com.clarity.ipmsbackend.model.vo.inventorybill.otherdeliveryorder.SafeOtherDeliveryOrderVO;
import com.clarity.ipmsbackend.model.vo.inventorybill.otherreceiptorder.SafeOtherReceiptOrderVO;

import javax.servlet.http.HttpServletRequest;

/**
* @author Clarity
* @description 针对表【ipms_inventory_bill(库存单据)】的数据库操作Service
* @createDate 2023-03-27 22:36:41
*/
public interface IpmsInventoryBillService extends IService<IpmsInventoryBill> {
    
    /**
     * 库存单据编号自动生成
     *
     * @param inventoryBillType 库存单据类型
     * @return 对应单据的下一个编号
     */
    String inventoryBillCodeAutoGenerate(String inventoryBillType);

    /**
     * 增加其他入库单
     *
     * @param addOtherReceiptOrderRequest 增加其他入库单请求封装对象
     * @param request HttpServletRequest
     * @return 1 - 成功， 0 - 失败
     */
    int addOtherReceiptOrder(AddOtherReceiptOrderRequest addOtherReceiptOrderRequest, HttpServletRequest request);

    /**
     * 增加其他出库单
     *
     * @param addOtherDeliveryOrderRequest 增加其他出库单请求封装对象
     * @param request HttpServletRequest
     * @return 1 - 成功， 0 - 失败
     */
    int addOtherDeliveryOrder(AddOtherDeliveryOrderRequest addOtherDeliveryOrderRequest, HttpServletRequest request);

    /**
     * 审核库存单据
     *
     * @param inventoryBillId 库存单据 id
     * @return 1 - 成功， 0 - 失败
     */
    int checkInventoryBill(long inventoryBillId, HttpServletRequest request);

    /**
     * 反审核库存单据
     *
     * @param inventoryBillId 库存单据 id
     * @return 1 - 成功， 0 - 失败
     */
    int reverseCheckInventoryBill(long inventoryBillId, HttpServletRequest request);

    /**
     * 根据 id 删除 库存单据
     *
     * @param id 单据 id
     * @return 1 - 成功， 0 - 失败
     */
    int deleteInventoryBillById(long id);

    /**
     * 修改其他入库单
     *
     * @param updateOtherReceiptOrderRequest 修改其他入库单请求封装对象
     * @param request HttpServletRequest
     * @return 1 - 成功， 0 - 失败
     */
    int updateOtherReceiptOrder(UpdateOtherReceiptOrderRequest updateOtherReceiptOrderRequest, HttpServletRequest request);

    /**
     * 修改其他出库单
     *
     * @param updateOtherDeliveryOrderRequest 修改其他出库单请求封装对象
     * @param request HttpServletRequest
     * @return 1 - 成功， 0 - 失败
     */
    int updateOtherDeliveryOrder(UpdateOtherDeliveryOrderRequest updateOtherDeliveryOrderRequest, HttpServletRequest request);

    /**
     * 分页查询其他入库单，且数据脱敏，且支持模糊查询
     *
     * @param fuzzyQueryRequest
     * @param request
     * @return
     */
    Page<SafeOtherReceiptOrderVO> pagingFuzzyQueryOtherReceiptOrder(FuzzyQueryRequest fuzzyQueryRequest, HttpServletRequest request);

    /**
     * 分页查询其他出库单，且数据脱敏，且支持模糊查询
     *
     * @param fuzzyQueryRequest
     * @param request
     * @return
     */
    Page<SafeOtherDeliveryOrderVO> pagingFuzzyQueryOtherDeliveryOrder(FuzzyQueryRequest fuzzyQueryRequest, HttpServletRequest request);
}
