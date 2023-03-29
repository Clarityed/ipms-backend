package com.clarity.ipmsbackend.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.clarity.ipmsbackend.annotation.AuthCheck;
import com.clarity.ipmsbackend.common.BaseResponse;
import com.clarity.ipmsbackend.common.ErrorCode;
import com.clarity.ipmsbackend.common.FuzzyQueryRequest;
import com.clarity.ipmsbackend.common.ResultUtils;
import com.clarity.ipmsbackend.constant.UserConstant;
import com.clarity.ipmsbackend.exception.BusinessException;
import com.clarity.ipmsbackend.model.dto.inventorybill.otherdeliveryorder.AddOtherDeliveryOrderRequest;
import com.clarity.ipmsbackend.model.dto.inventorybill.otherdeliveryorder.UpdateOtherDeliveryOrderRequest;
import com.clarity.ipmsbackend.model.dto.inventorybill.otherreceiptorder.AddOtherReceiptOrderRequest;
import com.clarity.ipmsbackend.model.dto.inventorybill.otherreceiptorder.UpdateOtherReceiptOrderRequest;
import com.clarity.ipmsbackend.model.vo.bom.SafeBomVO;
import com.clarity.ipmsbackend.model.vo.inventorybill.otherdeliveryorder.SafeOtherDeliveryOrderVO;
import com.clarity.ipmsbackend.model.vo.inventorybill.otherreceiptorder.SafeOtherReceiptOrderVO;
import com.clarity.ipmsbackend.service.IpmsInventoryBillService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
 * 库存单据管理接口
 *
 * @author: clarity
 * @date: 2023年03月27日 22:43
 */

@Api(tags = "库存单据管理接口")
@RestController
@Slf4j
@RequestMapping("/inventoryBill")
public class IpmsInventoryBillController {

    @Resource
    private IpmsInventoryBillService ipmsInventoryBillService;

    /**
     * 库存单据编号自动生成（库存员、库存主管）
     *
     * @return
     */
    @GetMapping("/inventoryBillCodeAutoGenerate/{inventoryBillType}")
    @ApiOperation(value = "库存单据编号自动生成（库存员、库存主管）")
    @AuthCheck(anyRole = {UserConstant.STORE_ROLE, UserConstant.STORE_ROLE_SUPER})
    public BaseResponse<String> inventoryBillCodeAutoGenerate(@PathVariable("inventoryBillType") String inventoryBillType) {
        String result = ipmsInventoryBillService.inventoryBillCodeAutoGenerate(inventoryBillType);
        if (result == null) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "编码自动生成器异常");
        }
        return ResultUtils.success(result);
    }

    /**
     * 增加其他入库单（仓管员、仓管主管）
     *
     * @param addOtherReceiptOrderRequest
     * @param request
     * @return
     */
    @PostMapping("/addOtherReceiptOrderBill")
    @ApiOperation("增加其他入库单（仓管员、仓管主管）")
    @AuthCheck(anyRole = {UserConstant.STORE_ROLE, UserConstant.STORE_ROLE_SUPER})
    public BaseResponse<Integer> addOtherReceiptOrderBill(@RequestBody AddOtherReceiptOrderRequest addOtherReceiptOrderRequest, HttpServletRequest request) {
        if (addOtherReceiptOrderRequest == null || request == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        int result = ipmsInventoryBillService.addOtherReceiptOrder(addOtherReceiptOrderRequest, request);
        return ResultUtils.success(result);
    }

    /**
     * 增加其他出库单（仓管员、仓管主管）
     *
     * @param addOtherDeliveryOrderRequest
     * @param request
     * @return
     */
    @PostMapping("/addOtherDeliveryOrderBill")
    @ApiOperation("增加其他出库单（仓管员、仓管主管）")
    @AuthCheck(anyRole = {UserConstant.STORE_ROLE, UserConstant.STORE_ROLE_SUPER})
    public BaseResponse<Integer> addOtherDeliveryOrderBill(@RequestBody AddOtherDeliveryOrderRequest addOtherDeliveryOrderRequest, HttpServletRequest request) {
        if (addOtherDeliveryOrderRequest == null || request == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        int result = ipmsInventoryBillService.addOtherDeliveryOrder(addOtherDeliveryOrderRequest, request);
        return ResultUtils.success(result);
    }

    /**
     * 审核库存单据（库存主管）
     *
     * @param inventoryBillId
     * @param request
     * @return
     */
    @GetMapping("/checkInventoryBill/{inventoryBillId}")
    @ApiOperation("审核库存单据（库存主管）")
    @AuthCheck(mustRole = UserConstant.STORE_ROLE_SUPER)
    public BaseResponse<Integer> checkInventoryBill(@PathVariable("inventoryBillId") long inventoryBillId, HttpServletRequest request) {
        if (request == null || inventoryBillId <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "参数为空或者 id 不合法");
        }
        int result = ipmsInventoryBillService.checkInventoryBill(inventoryBillId, request);
        return ResultUtils.success(result);
    }

    /**
     * 反审核库存单据（库存主管）
     *
     * @param inventoryBillId
     * @param request
     * @return
     */
    @GetMapping("/reverseCheckInventoryBill/{inventoryBillId}")
    @ApiOperation("反审核库存单据（库存主管）")
    @AuthCheck(mustRole = UserConstant.STORE_ROLE_SUPER)
    public BaseResponse<Integer> reverseCheckInventoryBill(@PathVariable("inventoryBillId") long inventoryBillId, HttpServletRequest request) {
        if (request == null || inventoryBillId <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "参数为空或者 id 不合法");
        }
        int result = ipmsInventoryBillService.reverseCheckInventoryBill(inventoryBillId, request);
        return ResultUtils.success(result);
    }

    /**
     * 根据 id 删除库存单据（库存员、库存主管）
     *
     * @param id
     * @return
     */
    @DeleteMapping("/deleteInventoryBillById/{id}")
    @ApiOperation(value = "根据 id 删除库存单据（库存员、库存主管）")
    @AuthCheck(anyRole = {UserConstant.STORE_ROLE, UserConstant.STORE_ROLE_SUPER})
    public BaseResponse<Integer> deleteInventoryBillById(@PathVariable("id") long id) {
        if (id <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "id 不合法");
        }
        int result = ipmsInventoryBillService.deleteInventoryBillById(id);
        return ResultUtils.success(result);
    }

    /**
     * 修改其他入库单（仓管员、仓管主管）
     *
     * @param updateOtherReceiptOrderRequest
     * @param request
     * @return
     */
    @PutMapping("/updateOtherReceiptOrder")
    @ApiOperation("修改其他入库单（仓管员、仓管主管）")
    @AuthCheck(anyRole = {UserConstant.STORE_ROLE, UserConstant.STORE_ROLE_SUPER})
    public BaseResponse<Integer> updateOtherReceiptOrder(@RequestBody UpdateOtherReceiptOrderRequest updateOtherReceiptOrderRequest, HttpServletRequest request) {
        if (updateOtherReceiptOrderRequest == null || request == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        int result = ipmsInventoryBillService.updateOtherReceiptOrder(updateOtherReceiptOrderRequest, request);
        return ResultUtils.success(result);
    }

    /**
     * 修改其他出库单（仓管员、仓管主管）
     *
     * @param updateOtherDeliveryOrderRequest
     * @param request
     * @return
     */
    @PutMapping("/updateOtherDeliveryOrder")
    @ApiOperation("修改其他出库单（仓管员、仓管主管）")
    @AuthCheck(anyRole = {UserConstant.STORE_ROLE, UserConstant.STORE_ROLE_SUPER})
    public BaseResponse<Integer> updateOtherDeliveryOrder(@RequestBody UpdateOtherDeliveryOrderRequest updateOtherDeliveryOrderRequest, HttpServletRequest request) {
        if (updateOtherDeliveryOrderRequest == null || request == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        int result = ipmsInventoryBillService.updateOtherDeliveryOrder(updateOtherDeliveryOrderRequest, request);
        return ResultUtils.success(result);
    }

    /**
     * 分页查询其他入库单，且数据脱敏，且支持模糊查询（仓管员、仓管主管）
     *
     * @param fuzzyQueryRequest
     * @param request
     * @return
     */
    @GetMapping("/pagingFuzzyQueryOtherReceiptOrder")
    @ApiOperation(value = "分页其他入库单 ，且模糊查询（仓管员、仓管主管）")
    @AuthCheck(anyRole = {UserConstant.STORE_ROLE, UserConstant.STORE_ROLE_SUPER})
    public BaseResponse<Page<SafeOtherReceiptOrderVO>> pagingFuzzyQueryOtherReceiptOrder(FuzzyQueryRequest fuzzyQueryRequest, HttpServletRequest request) {
        if (fuzzyQueryRequest == null || request == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Page<SafeOtherReceiptOrderVO> safeOtherReceiptOrderVOPage = ipmsInventoryBillService.pagingFuzzyQueryOtherReceiptOrder(fuzzyQueryRequest, request);
        return ResultUtils.success(safeOtherReceiptOrderVOPage);
    }

    /**
     * 分页查询其他出库单，且数据脱敏，且支持模糊查询（仓管员、仓管主管）
     *
     * @param fuzzyQueryRequest
     * @param request
     * @return
     */
    @GetMapping("/pagingFuzzyQueryOtherDeliveryOrder")
    @ApiOperation(value = "分页其他出库单 ，且模糊查询（仓管员、仓管主管）")
    @AuthCheck(anyRole = {UserConstant.STORE_ROLE, UserConstant.STORE_ROLE_SUPER})
    public BaseResponse<Page<SafeOtherDeliveryOrderVO>> pagingFuzzyQueryOtherDeliveryOrder(FuzzyQueryRequest fuzzyQueryRequest, HttpServletRequest request) {
        if (fuzzyQueryRequest == null || request == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Page<SafeOtherDeliveryOrderVO> safeOtherDeliveryOrderVOPage = ipmsInventoryBillService.pagingFuzzyQueryOtherDeliveryOrder(fuzzyQueryRequest, request);
        return ResultUtils.success(safeOtherDeliveryOrderVOPage);
    }
}
