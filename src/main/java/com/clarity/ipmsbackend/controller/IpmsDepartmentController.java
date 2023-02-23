package com.clarity.ipmsbackend.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.clarity.ipmsbackend.annotation.AuthCheck;
import com.clarity.ipmsbackend.common.BaseResponse;
import com.clarity.ipmsbackend.common.ErrorCode;
import com.clarity.ipmsbackend.common.FuzzyQueryRequest;
import com.clarity.ipmsbackend.common.ResultUtils;
import com.clarity.ipmsbackend.constant.UserConstant;
import com.clarity.ipmsbackend.exception.BusinessException;
import com.clarity.ipmsbackend.model.dto.department.AddDepartmentRequest;
import com.clarity.ipmsbackend.model.dto.department.UpdateDepartmentRequest;
import com.clarity.ipmsbackend.model.vo.SafeDepartmentVO;
import com.clarity.ipmsbackend.service.IpmsDepartmentService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
 * 部门控制器
 *
 * @author: clarity
 * @date: 2023年02月22日 18:05
 */

@Api(tags = "部门管理接口")
@RestController
@Slf4j
@RequestMapping("/department")
public class IpmsDepartmentController {

    @Resource
    private IpmsDepartmentService ipmsDepartmentService;

    @PostMapping("/addDepartment")
    @ApiOperation("增加部门（管理员）")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Integer> addDepartment(@RequestBody AddDepartmentRequest addDepartmentRequest, HttpServletRequest request) {
        if (addDepartmentRequest == null || request == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        int result = ipmsDepartmentService.addDepartment(addDepartmentRequest, request);
        return ResultUtils.success(result);
    }

    /**
     * 根据 id 删除部门（管理员）
     *
     * @param id
     * @return
     */
    @DeleteMapping("/deleteDepartment/{id}")
    @ApiOperation(value = "根据 id 删除部门（管理员）")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Integer> deleteDepartment(@PathVariable("id") long id) {
        if (id <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "id 不合法");
        }
        int result = ipmsDepartmentService.deleteDepartmentById(id);
        return ResultUtils.success(result);
    }

    /**
     * 更新部门（管理员）
     *
     * @param updateDepartmentRequest
     * @return
     */
    @PutMapping("/updateDepartment")
    @ApiOperation("更新部门（管理员）")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Integer> updateDepartment(@RequestBody UpdateDepartmentRequest updateDepartmentRequest) {
        if (updateDepartmentRequest == null || updateDepartmentRequest.getDepartmentId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "参数为空或者 id 为空");
        }
        int result = ipmsDepartmentService.updateDepartment(updateDepartmentRequest);
        return ResultUtils.success(result);
    }

    /**
     * 分页查询部门，且模糊查询
     *
     * @param fuzzyQueryRequest
     * @param request
     * @return
     */
    @GetMapping("/pagingFuzzyQuery")
    @ApiOperation(value = "分页查询部门，且模糊查询（管理员）")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Page<SafeDepartmentVO>> pagingFuzzyQuery(FuzzyQueryRequest fuzzyQueryRequest, HttpServletRequest request) {
        if (fuzzyQueryRequest == null || request == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Page<SafeDepartmentVO> safeDepartmentVOPage = ipmsDepartmentService.pagingFuzzyQuery(fuzzyQueryRequest, request);
        return ResultUtils.success(safeDepartmentVOPage);
    }
}
