package com.clarity.ipmsbackend.controller;

import com.clarity.ipmsbackend.annotation.AuthCheck;
import com.clarity.ipmsbackend.common.BaseResponse;
import com.clarity.ipmsbackend.common.ErrorCode;
import com.clarity.ipmsbackend.common.ResultUtils;
import com.clarity.ipmsbackend.constant.UserConstant;
import com.clarity.ipmsbackend.exception.BusinessException;
import com.clarity.ipmsbackend.model.dto.bom.AddBomRequest;
import com.clarity.ipmsbackend.model.dto.purchasebill.AddPurchaseBillRequest;
import com.clarity.ipmsbackend.model.dto.purchasebill.UpdatePurchaseBillRequest;
import com.clarity.ipmsbackend.service.IpmsPurchaseBillService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
 * 采购单据控制器
 *
 * @author: clarity
 * @date: 2023年03月13日 17:20
 */

@Api(tags = "采购单据管理接口")
@RestController
@Slf4j
@RequestMapping("/purchaseBill")
public class IpmsPurchaseBillController {

    @Resource
    private IpmsPurchaseBillService ipmsPurchaseBillService;

    /**
     * 采购单据编号自动生成（采购员、采购主管）
     *
     * @return
     */
    @GetMapping("/purchaseBillCodeAutoGenerate/{purchaseBillType}")
    @ApiOperation(value = "采购单据编号自动生成（采购员、采购主管）")
    @AuthCheck(anyRole = {UserConstant.BUY_ROLE, UserConstant.BUY_ROLE_SUPER})
    public BaseResponse<String> purchaseBillCodeAutoGenerate(@PathVariable("purchaseBillType") String purchaseBillType) {
        String result = ipmsPurchaseBillService.purchaseBillCodeAutoGenerate(purchaseBillType);
        if (result == null) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "编码自动生成器异常");
        }
        return ResultUtils.success(result);
    }

    /**
     * 增加采购单据（采购员、采购主管）
     *
     * @param addPurchaseBillRequest
     * @param request
     * @return
     */
    @PostMapping("/addPurchaseBill")
    @ApiOperation("增加采购单据（采购员、采购主管）")
    @AuthCheck(anyRole = {UserConstant.BUY_ROLE, UserConstant.BUY_ROLE_SUPER})
    public BaseResponse<Integer> addPurchaseBill(@RequestBody AddPurchaseBillRequest addPurchaseBillRequest, HttpServletRequest request) {
        if (addPurchaseBillRequest == null || request == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        int result = ipmsPurchaseBillService.addPurchaseBill(addPurchaseBillRequest, request);
        return ResultUtils.success(result);
    }

    /**
     * 审核采购单据（采购主管）
     *
     * @param purchaseBillId
     * @param request
     * @return
     */
    @GetMapping("/checkPurchaseBill/{purchaseBillId}")
    @ApiOperation("审核采购单据（采购主管）")
    @AuthCheck(mustRole = UserConstant.BUY_ROLE_SUPER)
    public BaseResponse<Integer> checkPurchaseBill(@PathVariable("purchaseBillId") long purchaseBillId, HttpServletRequest request) {
        if (request == null || purchaseBillId <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "参数为空或者 id 不合法");
        }
        int result = ipmsPurchaseBillService.checkPurchaseBill(purchaseBillId, request);
        return ResultUtils.success(result);
    }

    /**
     * 反审核采购单据（采购主管）
     *
     * @param purchaseBillId
     * @param request
     * @return
     */
    @GetMapping("/reverseCheckPurchaseBill/{purchaseBillId}")
    @ApiOperation("反审核采购单据（采购主管）")
    @AuthCheck(mustRole = UserConstant.BUY_ROLE_SUPER)
    public BaseResponse<Integer> reverseCheckPurchaseBill(@PathVariable("purchaseBillId") long purchaseBillId, HttpServletRequest request) {
        if (request == null || purchaseBillId <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "参数为空或者 id 不合法");
        }
        int result = ipmsPurchaseBillService.reverseCheckPurchaseBill(purchaseBillId, request);
        return ResultUtils.success(result);
    }

    /**
     * 根据 id 删除采购单据（采购员、采购主管）
     *
     * @param id
     * @return
     */
    @DeleteMapping("/deletePurchaseBillById/{id}")
    @ApiOperation(value = "根据 id 删除采购单据（采购员、采购主管）")
    @AuthCheck(anyRole = {UserConstant.BUY_ROLE, UserConstant.BUY_ROLE_SUPER})
    public BaseResponse<Integer> deletePurchaseBillById(@PathVariable("id") long id) {
        if (id <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "id 不合法");
        }
        int result = ipmsPurchaseBillService.deletePurchaseBillById(id);
        return ResultUtils.success(result);
    }

    /**
     * 修改采购单据（采购员、采购主管）
     *
     * @param updatePurchaseBillRequest
     * @param request
     * @return
     */
    @PutMapping("/updatePurchaseBill")
    @ApiOperation("修改采购单据（采购员、采购主管）")
    @AuthCheck(anyRole = {UserConstant.BUY_ROLE, UserConstant.BUY_ROLE_SUPER})
    public BaseResponse<Integer> updatePurchaseBill(@RequestBody UpdatePurchaseBillRequest updatePurchaseBillRequest, HttpServletRequest request) {
        if (updatePurchaseBillRequest == null || request == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        int result = ipmsPurchaseBillService.updatePurchaseBill(updatePurchaseBillRequest, request);
        return ResultUtils.success(result);
    }
}
