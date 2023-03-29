package com.clarity.ipmsbackend.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.clarity.ipmsbackend.model.dto.inventorybill.otherdeliveryorder.productnum.AddOtherDeliveryOrderProductNumRequest;
import com.clarity.ipmsbackend.model.dto.inventorybill.otherdeliveryorder.productnum.UpdateOtherDeliveryOrderProductNumRequest;
import com.clarity.ipmsbackend.model.dto.inventorybill.otherreceiptorder.productnum.AddOtherReceiptOrderProductNumRequest;
import com.clarity.ipmsbackend.model.dto.inventorybill.otherreceiptorder.productnum.UpdateOtherReceiptOrderProductNumRequest;
import com.clarity.ipmsbackend.model.dto.inventorybill.warehousetransferorder.productnum.AddWarehouseTransferOrderProductNumRequest;
import com.clarity.ipmsbackend.model.dto.inventorybill.warehousetransferorder.productnum.UpdateWarehouseTransferOrderProductNumRequest;
import com.clarity.ipmsbackend.model.entity.IpmsInventoryBillProductNum;
import com.clarity.ipmsbackend.model.entity.IpmsInventoryBill;

/**
* @author Clarity
* @description 针对表【ipms_inventory_bill_product_num(库存单据商品数量表)】的数据库操作Service
* @createDate 2023-03-27 22:36:45
*/
public interface IpmsInventoryBillProductNumService extends IService<IpmsInventoryBillProductNum> {

    /**
     * 增加其他入库单商品
     *
     * @param addOtherReceiptOrderProductNumRequest 增加其他入库单商品请求封装对象
     * @param inventoryBill 其他入库单对象
     * @return 增加的其他入库单 id
     */
    long addOtherReceiptOrderProductAndNum(AddOtherReceiptOrderProductNumRequest addOtherReceiptOrderProductNumRequest, IpmsInventoryBill inventoryBill);

    /**
     * 增加其他出库单商品
     *
     * @param addOtherDeliveryOrderProductNumRequest 增加其他出库单商品请求封装对象
     * @param inventoryBill 其他出库单对象
     * @return 增加的其他出库单 id
     */
    long addOtherDeliveryOrderProductAndNum(AddOtherDeliveryOrderProductNumRequest addOtherDeliveryOrderProductNumRequest, IpmsInventoryBill inventoryBill);

    /**
     * 增加移仓单商品
     *
     * @param addWarehouseTransferOrderProductNumRequest 增加移仓单商品请求封装对象
     * @param inventoryBill 移仓单对象
     * @return 增加的移仓单 id
     */
    long addWarehouseTransferOrderProductAndNum(AddWarehouseTransferOrderProductNumRequest addWarehouseTransferOrderProductNumRequest, IpmsInventoryBill inventoryBill);

    /**
     * 修改其他入库单商品
     *
     * @param updateOtherReceiptOrderProductNumRequest 修改其他入库单商品请求封装对象
     * @param inventoryBill 其他入库单对象
     * @return 1 - 表示成功，0 - 表示失败
     */
    int updateOtherReceiptOrderProductAndNum(UpdateOtherReceiptOrderProductNumRequest updateOtherReceiptOrderProductNumRequest, IpmsInventoryBill inventoryBill);

    /**
     * 修改其他出库单商品
     *
     * @param updateOtherDeliveryOrderProductNumRequest 修改其他出库单商品请求封装对象
     * @param inventoryBill 其他出库单对象
     * @return 1 - 表示成功，0 - 表示失败
     */
    int updateOtherDeliveryOrderProductAndNum(UpdateOtherDeliveryOrderProductNumRequest updateOtherDeliveryOrderProductNumRequest, IpmsInventoryBill inventoryBill);

    /**
     * 修改移仓单商品
     *
     * @param updateWarehouseTransferOrderProductNumRequest 修改移仓单商品请求封装对象
     * @param inventoryBill 移仓单对象
     * @return 1 - 表示成功，0 - 表示失败
     */
    int updateWarehouseTransferOrderProductAndNum(UpdateWarehouseTransferOrderProductNumRequest updateWarehouseTransferOrderProductNumRequest, IpmsInventoryBill inventoryBill);
}
