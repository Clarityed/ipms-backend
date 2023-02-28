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
import com.clarity.ipmsbackend.model.vo.SafeWarehouseVO;
import com.clarity.ipmsbackend.service.IpmsWarehouseService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
 * 仓库控制器
 *
 * @author: clarity
 * @date: 2023年02月27日 20:32
 */

@Api(tags = "仓库管理接口")
@RestController
@Slf4j
@RequestMapping("/warehouse")
public class IpmsWarehouseController {

    @Resource
    private IpmsWarehouseService ipmsWarehouseService;

    /**
     * 仓库编号自动生成
     *
     * @return
     */
    @GetMapping("/warehouseCodeAutoGenerate")
    @ApiOperation(value = "仓库编号自动生成（管理员）")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<String> customerCodeAutoGenerate() {
        String result = ipmsWarehouseService.warehouseCodeAutoGenerate();
        if (result == null) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "编码自动生成器异常");
        }
        return ResultUtils.success(result);
    }

    /**
     * 增加仓库（管理员）
     *
     * @param addWarehouseRequest
     * @return
     */
    @PostMapping("/addWarehouse")
    @ApiOperation("增加仓库（管理员）")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Integer> addWarehouse(@RequestBody AddWarehouseRequest addWarehouseRequest) {
        if (addWarehouseRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        int result = ipmsWarehouseService.addWarehouse(addWarehouseRequest);
        return ResultUtils.success(result);
    }

    /**
     * 根据 id 删除仓库（管理员）
     *
     * @param id
     * @return
     */
    @DeleteMapping("/deleteWarehouseById/{id}")
    @ApiOperation(value = "根据 id 删除仓库（管理员）")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Integer> deleteWarehouseById(@PathVariable("id") long id) {
        if (id <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "id 不合法");
        }
        int result = ipmsWarehouseService.deleteWarehouseById(id);
        return ResultUtils.success(result);
    }

    /**
     * 更新仓库（管理员）
     *
     * @param updateWarehouseRequest
     * @return
     */
    @PutMapping("/updateWarehouse")
    @ApiOperation("更新仓库（管理员）")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Integer> updateWarehouse(@RequestBody UpdateWarehouseRequest updateWarehouseRequest) {
        if (updateWarehouseRequest == null || updateWarehouseRequest.getWarehouseId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "参数为空或者 id 为空");
        }
        int result = ipmsWarehouseService.updateWarehouse(updateWarehouseRequest);
        return ResultUtils.success(result);
    }

    /**
     * 分页查询仓库，且模糊查询（任意用户）
     *
     * @param fuzzyQueryRequest
     * @param request
     * @return
     */
    @GetMapping("/pagingFuzzyQuery")
    @ApiOperation(value = "分页查询仓库，且模糊查询（任意用户）")
    public BaseResponse<Page<SafeWarehouseVO>> pagingFuzzyQuery(FuzzyQueryRequest fuzzyQueryRequest, HttpServletRequest request) {
        if (fuzzyQueryRequest == null || request == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Page<SafeWarehouseVO> safeWarehouseVOPage = ipmsWarehouseService.pagingFuzzyQuery(fuzzyQueryRequest, request);
        return ResultUtils.success(safeWarehouseVOPage);
    }
}
