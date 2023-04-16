package com.clarity.ipmsbackend.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.clarity.ipmsbackend.annotation.AuthCheck;
import com.clarity.ipmsbackend.common.BaseResponse;
import com.clarity.ipmsbackend.common.ErrorCode;
import com.clarity.ipmsbackend.common.SaleBillQueryRequest;
import com.clarity.ipmsbackend.common.ResultUtils;
import com.clarity.ipmsbackend.constant.UserConstant;
import com.clarity.ipmsbackend.exception.BusinessException;
import com.clarity.ipmsbackend.model.dto.salebill.UpdateSaleBillRequest;
import com.clarity.ipmsbackend.model.dto.salebill.AddSaleBillRequest;
import com.clarity.ipmsbackend.model.entity.IpmsSaleBill;
import com.clarity.ipmsbackend.model.vo.salebill.SafeSaleBillVO;
import com.clarity.ipmsbackend.service.IpmsSaleBillService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
 * 销售单据控制器
 *
 * @author: scott
 * @date: 2023年03月21日 10:42
 */

@Api(tags = "销售单据管理接口")
@RestController
@Slf4j
@RequestMapping("/saleBill")
public class IpmsSaleBillController {

    @Resource
    private IpmsSaleBillService ipmsSaleBillService;

    /**
     * 销售单据编号自动生成（销售员、销售主管）
     *
     * @return
     */
    @GetMapping("/saleBillCodeAutoGenerate/{saleBillType}")
    @ApiOperation(value = "销售单据编号自动生成（销售员、销售主管）")
    @AuthCheck(anyRole = {UserConstant.SALE_ROLE, UserConstant.SALE_ROLE_SUPER})
    public BaseResponse<String> saleBillCodeAutoGenerate(@PathVariable("saleBillType") String saleBillType) {
        String result = ipmsSaleBillService.saleBillCodeAutoGenerate(saleBillType);
        if (result == null) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "编码自动生成器异常");
        }
        return ResultUtils.success(result);
    }

    /**
     * 增加销售单据（销售员、销售主管）
     *
     * @param addSaleBillRequest
     * @param request
     * @return
     */
    @PostMapping("/addSaleBill")
    @ApiOperation("增加销售单据（销售员、销售主管）")
    @AuthCheck(anyRole = {UserConstant.SALE_ROLE, UserConstant.SALE_ROLE_SUPER})
    public BaseResponse<Integer> addSaleBill(@RequestBody AddSaleBillRequest addSaleBillRequest, HttpServletRequest request) {
        if (addSaleBillRequest == null || request == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        int result = ipmsSaleBillService.addSaleBill(addSaleBillRequest, request);
        return ResultUtils.success(result);
    }

    /**
     * 审核销售单据（销售主管）
     *
     * @param saleBillId
     * @param request
     * @return
     */
    @GetMapping("/checkSaleBill/{saleBillId}")
    @ApiOperation("审核销售单据（销售主管），如果是销售出库单和销售退货单那么审核人必须是仓管员")
    @AuthCheck(anyRole = {UserConstant.SALE_ROLE_SUPER, UserConstant.STORE_ROLE, UserConstant.STORE_ROLE_SUPER})
    public BaseResponse<Integer> checkSaleBill(@PathVariable("saleBillId") long saleBillId, HttpServletRequest request) {
        if (request == null || saleBillId <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "参数为空或者 id 不合法");
        }
        int result = ipmsSaleBillService.checkSaleBill(saleBillId, request);
        return ResultUtils.success(result);
    }

    /**
     * 反审核销售单据（销售主管）
     *
     * @param saleBillId
     * @param request
     * @return
     */
    @GetMapping("/reverseCheckSaleBill/{saleBillId}")
    @ApiOperation("反审核销售单据（销售主管），如果是销售出库单和销售退货单那么反审核人必须是仓管员")
    @AuthCheck(anyRole = {UserConstant.SALE_ROLE_SUPER, UserConstant.STORE_ROLE, UserConstant.STORE_ROLE_SUPER})
    public BaseResponse<Integer> reverseCheckSaleBill(@PathVariable("saleBillId") long saleBillId, HttpServletRequest request) {
        if (request == null || saleBillId <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "参数为空或者 id 不合法");
        }
        int result = ipmsSaleBillService.reverseCheckSaleBill(saleBillId, request);
        return ResultUtils.success(result);
    }

    /**
     * 根据 id 删除销售单据（销售员、销售主管）
     *
     * @param id
     * @return
     */
    @DeleteMapping("/deleteSaleBillById/{id}")
    @ApiOperation(value = "根据 id 删除销售单据（销售员、销售主管）")
    @AuthCheck(anyRole = {UserConstant.SALE_ROLE, UserConstant.SALE_ROLE_SUPER})
    public BaseResponse<Integer> deleteSaleBillById(@PathVariable("id") long id) {
        if (id <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "id 不合法");
        }
        int result = ipmsSaleBillService.deleteSaleBillById(id);
        return ResultUtils.success(result);
    }

    /**
     * 修改销售单据（销售员、销售主管）
     *
     * @param updateSaleBillRequest
     * @param request
     * @return
     */
    @PutMapping("/updateSaleBill")
    @ApiOperation("修改销售单据（销售员、销售主管）")
    @AuthCheck(anyRole = {UserConstant.SALE_ROLE, UserConstant.SALE_ROLE_SUPER})
    public BaseResponse<Integer> updateSaleBill(@RequestBody UpdateSaleBillRequest updateSaleBillRequest, HttpServletRequest request) {
        if (updateSaleBillRequest == null || request == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        int result = ipmsSaleBillService.updateSaleBill(updateSaleBillRequest, request);
        return ResultUtils.success(result);
    }

    /**
     * 销售单据查询（可作为选单源功能，销售单据列表查询功能，并且也支持模糊查询）
     *
     * @param saleBillQueryRequest
     * @return
     */
    @GetMapping("/selectSaleBill")
    @ApiOperation(value = "销售单据查询（可作为选单源功能，销售单据列表查询功能，并且也支持模糊查询）（销售员、销售主管）")
    @AuthCheck(anyRole = {UserConstant.SALE_ROLE, UserConstant.SALE_ROLE_SUPER})
    public BaseResponse<Page<SafeSaleBillVO>> selectSaleBill(SaleBillQueryRequest saleBillQueryRequest) {
        if (saleBillQueryRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Page<SafeSaleBillVO> safeSaleBillSelectSingleSourceVOPage = ipmsSaleBillService.selectSaleBill(saleBillQueryRequest);
        return ResultUtils.success(safeSaleBillSelectSingleSourceVOPage);
    }
}
