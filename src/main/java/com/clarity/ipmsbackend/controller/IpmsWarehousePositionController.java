package com.clarity.ipmsbackend.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.clarity.ipmsbackend.annotation.AuthCheck;
import com.clarity.ipmsbackend.common.BaseResponse;
import com.clarity.ipmsbackend.common.ErrorCode;
import com.clarity.ipmsbackend.common.FuzzyQueryRequest;
import com.clarity.ipmsbackend.common.ResultUtils;
import com.clarity.ipmsbackend.constant.UserConstant;
import com.clarity.ipmsbackend.exception.BusinessException;
import com.clarity.ipmsbackend.model.dto.warehouse.AddWarehouseRequest;
import com.clarity.ipmsbackend.model.dto.warehouse.UpdateWarehouseRequest;
import com.clarity.ipmsbackend.model.dto.warehouse.position.AddWarehousePositionRequest;
import com.clarity.ipmsbackend.model.dto.warehouse.position.UpdateWarehousePositionRequest;
import com.clarity.ipmsbackend.model.vo.SafeWarehousePositionVO;
import com.clarity.ipmsbackend.model.vo.SafeWarehouseVO;
import com.clarity.ipmsbackend.service.IpmsWarehousePositionService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
 * 仓位控制器
 *
 * @author: scott
 * @date: 2023年02月28日 11:21
 */

@Api(tags = "仓位管理接口")
@RestController
@Slf4j
@RequestMapping("/warehousePosition")
public class IpmsWarehousePositionController {

    @Resource
    private IpmsWarehousePositionService ipmsWarehousePositionService;

    /**
     * 仓位编号自动生成
     *
     * @return
     */
    @GetMapping("/warehousePositionCodeAutoGenerate")
    @ApiOperation(value = "仓库编号自动生成（管理员）")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<String> customerCodeAutoGenerate() {
        String result = ipmsWarehousePositionService.warehousePositionCodeAutoGenerate();
        if (result == null) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "编码自动生成器异常");
        }
        return ResultUtils.success(result);
    }

    /**
     * 增加仓位（管理员）
     *
     * @param addWarehousePositionRequest
     * @return
     */
    @PostMapping("/addWarehousePosition")
    @ApiOperation("增加仓位（管理员）")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Integer> addWarehousePosition(@RequestBody AddWarehousePositionRequest addWarehousePositionRequest) {
        if (addWarehousePositionRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        int result = ipmsWarehousePositionService.addWarehousePosition(addWarehousePositionRequest);
        return ResultUtils.success(result);
    }

    /**
     * 根据 id 删除仓位（管理员）
     *
     * @param id
     * @return
     */
    @DeleteMapping("/deleteWarehousePositionById/{id}")
    @ApiOperation(value = "根据 id 删除仓位（管理员）")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Integer> deleteWarehousePositionById(@PathVariable("id") long id) {
        if (id <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "id 不合法");
        }
        int result = ipmsWarehousePositionService.deleteWarehousePositionById(id);
        return ResultUtils.success(result);
    }

    /**
     * 更新仓位（管理员）
     *
     * @param updateWarehousePositionRequest
     * @return
     */
    @PutMapping("/updateWarehousePosition")
    @ApiOperation("更新仓位（管理员）")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Integer> updateWarehousePosition(@RequestBody UpdateWarehousePositionRequest updateWarehousePositionRequest) {
        if (updateWarehousePositionRequest == null || updateWarehousePositionRequest.getWarehousePositionId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "参数为空或者 id 为空");
        }
        int result = ipmsWarehousePositionService.updateWarehousePosition(updateWarehousePositionRequest);
        return ResultUtils.success(result);
    }

    /**
     * 分页查询仓位，且模糊查询（任意用户）
     *
     * @param fuzzyQueryRequest
     * @param request
     * @return
     */
    @GetMapping("/pagingFuzzyQuery")
    @ApiOperation(value = "分页查询仓位，且模糊查询（任意用户）")
    public BaseResponse<Page<SafeWarehousePositionVO>> pagingFuzzyQuery(FuzzyQueryRequest fuzzyQueryRequest, HttpServletRequest request) {
        if (fuzzyQueryRequest == null || request == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Page<SafeWarehousePositionVO> safeWarehousePositionVOPage = ipmsWarehousePositionService.pagingFuzzyQuery(fuzzyQueryRequest, request);
        return ResultUtils.success(safeWarehousePositionVOPage);
    }
}
