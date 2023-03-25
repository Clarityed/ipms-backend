package com.clarity.ipmsbackend.controller;

import com.clarity.ipmsbackend.annotation.AuthCheck;
import com.clarity.ipmsbackend.common.BaseResponse;
import com.clarity.ipmsbackend.common.ErrorCode;
import com.clarity.ipmsbackend.common.ResultUtils;
import com.clarity.ipmsbackend.constant.UserConstant;
import com.clarity.ipmsbackend.exception.BusinessException;
import com.clarity.ipmsbackend.model.dto.productionbill.AddProductionBillRequest;
import com.clarity.ipmsbackend.service.IpmsProductionBillService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
 * 生产单据控制器
 *
 * @author: clarity
 * @date: 2023年03月23日 14:58
 */

@Api(tags = "生产单据管理接口")
@RestController
@Slf4j
@RequestMapping("/productionBill")
public class IpmsProductionBillController {

    @Resource
    private IpmsProductionBillService ipmsProductionBillService;

    /**
     * 生产单据编号自动生成（生产员、生产主管）
     *
     * @param productionBillType
     * @return
     */
    @GetMapping("/productionBillCodeAutoGenerate/{productionBillType}")
    @ApiOperation(value = "生产单据编号自动生成（生产员、生产主管）")
    @AuthCheck(anyRole = {UserConstant.PRODUCT_ROLE, UserConstant.PRODUCT_ROLE_SUPER})
    public BaseResponse<String> productionBillCodeAutoGenerate(@PathVariable("productionBillType") String productionBillType) {
        String result = ipmsProductionBillService.productionBillCodeAutoGenerate(productionBillType);
        if (result == null) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "编码自动生成器异常");
        }
        return ResultUtils.success(result);
    }

    /**
     * 增加生产单据（生产员、生产主管）
     *
     * @param addProductionBillRequest
     * @param request
     * @return
     */
    @PostMapping("/addProductionBill")
    @ApiOperation("增加生产单据（生产员、生产主管）")
    @AuthCheck(anyRole = {UserConstant.PRODUCT_ROLE, UserConstant.PRODUCT_ROLE_SUPER})
    public BaseResponse<Integer> addProductionBill(@RequestBody AddProductionBillRequest addProductionBillRequest, HttpServletRequest request) {
        if (addProductionBillRequest == null || request == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        int result = ipmsProductionBillService.addProductionBill(addProductionBillRequest, request);
        return ResultUtils.success(result);
    }

    /**
     * 审核生产单据（生产主管）
     *
     * @param productionBillId
     * @param request
     * @return
     */
    @GetMapping("/checkProductionBill/{productionBillId}")
    @ApiOperation("审核生产单据（生产主管）")
    @AuthCheck(mustRole = UserConstant.PRODUCT_ROLE_SUPER)
    public BaseResponse<Integer> checkProductionBill(@PathVariable("productionBillId") long productionBillId, HttpServletRequest request) {
        if (request == null || productionBillId <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "参数为空或者 id 不合法");
        }
        int result = ipmsProductionBillService.checkProductionBill(productionBillId, request);
        return ResultUtils.success(result);
    }

    /**
     * 反审核生产单据（生产主管）
     *
     * @param productionBillId
     * @param request
     * @return
     */
    @GetMapping("/reverseCheckProductionBill/{productionBillId}")
    @ApiOperation("反审核生产单据（生产主管）")
    @AuthCheck(mustRole = UserConstant.PRODUCT_ROLE_SUPER)
    public BaseResponse<Integer> reverseCheckProductionBill(@PathVariable("productionBillId") long productionBillId, HttpServletRequest request) {
        if (request == null || productionBillId <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "参数为空或者 id 不合法");
        }
        int result = ipmsProductionBillService.reverseCheckProductionBill(productionBillId, request);
        return ResultUtils.success(result);
    }

    /**
     * 根据 id 删除生产单据（生产员、生产主管）
     *
     * @param id
     * @return
     */
    @DeleteMapping("/deleteProductionBillById/{id}")
    @ApiOperation(value = "根据 id 删除生产单据（生产员、生产主管）")
    @AuthCheck(anyRole = {UserConstant.PRODUCT_ROLE, UserConstant.PRODUCT_ROLE_SUPER})
    public BaseResponse<Integer> deleteProductionBillById(@PathVariable("id") long id) {
        if (id <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "id 不合法");
        }
        int result = ipmsProductionBillService.deleteProductionBillById(id);
        return ResultUtils.success(result);
    }
}
