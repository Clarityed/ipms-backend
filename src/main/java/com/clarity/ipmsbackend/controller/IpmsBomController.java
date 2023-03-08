package com.clarity.ipmsbackend.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.clarity.ipmsbackend.annotation.AuthCheck;
import com.clarity.ipmsbackend.common.BaseResponse;
import com.clarity.ipmsbackend.common.ErrorCode;
import com.clarity.ipmsbackend.common.FuzzyQueryRequest;
import com.clarity.ipmsbackend.common.ResultUtils;
import com.clarity.ipmsbackend.constant.UserConstant;
import com.clarity.ipmsbackend.exception.BusinessException;
import com.clarity.ipmsbackend.model.dto.bom.AddBomRequest;
import com.clarity.ipmsbackend.model.dto.bom.UpdateBomRequest;
import com.clarity.ipmsbackend.model.dto.unit.UpdateUnitRequest;
import com.clarity.ipmsbackend.model.vo.SafeBomVO;
import com.clarity.ipmsbackend.model.vo.SafeProductVO;
import com.clarity.ipmsbackend.model.vo.SafeUnitVO;
import com.clarity.ipmsbackend.service.IpmsBomService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
 * BOM 控制器
 *
 * @author: clarity
 * @date: 2023年03月05日 14:44
 */

@Api(tags = "BOM 管理接口")
@RestController
@Slf4j
@RequestMapping("/bom")
public class IpmsBomController {

    @Resource
    private IpmsBomService ipmsBomService;

    /**
     * BOM 编号自动生成
     *
     * @return
     */
    @GetMapping("/bomCodeAutoGenerate")
    @ApiOperation(value = "BOM 编号自动生成（开发员、开发主管）")
    @AuthCheck(anyRole = {UserConstant.DEVELOP_ROLE, UserConstant.DEVELOP_ROLE_SUPER})
    public BaseResponse<String> bomCodeAutoGenerate() {
        String result = ipmsBomService.bomCodeAutoGenerate();
        if (result == null) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "编码自动生成器异常");
        }
        return ResultUtils.success(result);
    }

    /**
     * 增加 BOM（开发主管、开发员）
     *
     * @param addBomRequest
     * @param request
     * @return
     */
    @PostMapping("/addBom")
    @ApiOperation("增加 BOM（开发主管、开发员）")
    @AuthCheck(anyRole = {UserConstant.DEVELOP_ROLE, UserConstant.DEVELOP_ROLE_SUPER})
    public BaseResponse<Integer> addBom(@RequestBody AddBomRequest addBomRequest, HttpServletRequest request) {
        if (addBomRequest == null || request == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        int result = ipmsBomService.addBom(addBomRequest, request);
        return ResultUtils.success(result);
    }

    /**
     * 根据 id 删除 BOM（开发主管、开发员）
     *
     * @param id
     * @return
     */
    @DeleteMapping("/deleteBomById/{id}")
    @ApiOperation(value = "根据 id 删除 BOM（开发主管、开发员）")
    @AuthCheck(anyRole = {UserConstant.DEVELOP_ROLE, UserConstant.DEVELOP_ROLE_SUPER})
    public BaseResponse<Integer> deleteBomById(@PathVariable("id") long id) {
        if (id <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "id 不合法");
        }
        int result = ipmsBomService.deleteBomById(id);
        return ResultUtils.success(result);
    }

    /**
     * 更新 BOM（开发主管、开发员）
     *
     * @param updateBomRequest
     * @return
     */
    @PutMapping("/updateBom")
    @ApiOperation("更新 BOM（开发主管、开发员）")
    @AuthCheck(anyRole = {UserConstant.DEVELOP_ROLE, UserConstant.DEVELOP_ROLE_SUPER})
    public BaseResponse<Integer> updateBom(@RequestBody UpdateBomRequest updateBomRequest, HttpServletRequest request) {
        if (updateBomRequest == null || updateBomRequest.getBomId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "参数为空或者 id 不合法");
        }
        int result = ipmsBomService.updateBom(updateBomRequest, request);
        return ResultUtils.success(result);
    }

    /**
     * 分页 BOM ，且模糊查询（任意用户）
     *
     * @param fuzzyQueryRequest
     * @param request
     * @return
     */
    @GetMapping("/pagingFuzzyQuery")
    @ApiOperation(value = "分页 BOM ，且模糊查询（任意用户）")
    public BaseResponse<Page<SafeBomVO>> pagingFuzzyQuery(FuzzyQueryRequest fuzzyQueryRequest, HttpServletRequest request) {
        if (fuzzyQueryRequest == null || request == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Page<SafeBomVO> safeBomVOPage = ipmsBomService.pagingFuzzyQuery(fuzzyQueryRequest, request);
        return ResultUtils.success(safeBomVOPage);
    }

    /**
     * 审核 BOM（开发主管）
     *
     * @param bomId
     * @param request
     * @return
     */
    @GetMapping("/checkBom/{bomId}")
    @ApiOperation("审核 BOM（开发主管）")
    @AuthCheck(anyRole = {UserConstant.DEVELOP_ROLE_SUPER})
    public BaseResponse<Integer> checkBom(@PathVariable("bomId") long bomId, HttpServletRequest request) {
        if (request == null || bomId <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "参数为空或者 id 不合法");
        }
        int result = ipmsBomService.checkBom(bomId, request);
        return ResultUtils.success(result);
    }

    /**
     * 反审核 BOM（开发主管）
     *
     * @param bomId
     * @return
     */
    @GetMapping("/reverseCheckBom/{bomId}")
    @ApiOperation("反审核 BOM（开发主管）")
    @AuthCheck(anyRole = {UserConstant.DEVELOP_ROLE_SUPER})
    public BaseResponse<Integer> reverseCheckBom(@PathVariable("bomId") long bomId) {
        if (bomId <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "参数为空或者 id 不合法");
        }
        int result = ipmsBomService.reverseCheckBom(bomId);
        return ResultUtils.success(result);
    }
}
