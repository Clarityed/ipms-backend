package com.clarity.ipmsbackend.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.clarity.ipmsbackend.annotation.AuthCheck;
import com.clarity.ipmsbackend.common.BaseResponse;
import com.clarity.ipmsbackend.common.ErrorCode;
import com.clarity.ipmsbackend.common.FuzzyQueryRequest;
import com.clarity.ipmsbackend.common.ResultUtils;
import com.clarity.ipmsbackend.constant.UserConstant;
import com.clarity.ipmsbackend.exception.BusinessException;
import com.clarity.ipmsbackend.model.dto.employee.AddEmployeeRequest;
import com.clarity.ipmsbackend.model.dto.employee.UpdateEmployeeRequest;
import com.clarity.ipmsbackend.model.vo.SafeEmployeeVO;
import com.clarity.ipmsbackend.service.IpmsEmployeeService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
 * 职员控制器
 *
 * @author: clarity
 * @date: 2023年02月23日 16:07
 */

@Api(tags = "职员管理接口")
@RestController
@Slf4j
@RequestMapping("/employee")
public class IpmsEmployeeController {

    @Resource
    private IpmsEmployeeService ipmsEmployeeService;

    /**
     * 增加职员（管理员）
     * @param addEmployeeRequest
     * @return
     */
    @PostMapping("/addEmployee")
    @ApiOperation("增加职员（管理员）")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Integer> addEmployee(@RequestBody AddEmployeeRequest addEmployeeRequest) {
        if (addEmployeeRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        int result = ipmsEmployeeService.addEmployee(addEmployeeRequest);
        return ResultUtils.success(result);
    }

    /**
     * 根据 id 删除职员（管理员）
     *
     * @param id
     * @return
     */
    @DeleteMapping("/deleteEmployeeById/{id}")
    @ApiOperation(value = "根据 id 删除职员（管理员）")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Integer> deleteEmployeeById(@PathVariable("id") long id) {
        if (id <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "id 不合法");
        }
        int result = ipmsEmployeeService.deleteEmployeeById(id);
        return ResultUtils.success(result);
    }

    /**
     * 更新职员（管理员）
     *
     * @param updateEmployeeRequest
     * @return
     */
    @PutMapping("/updateEmployee")
    @ApiOperation("更新职员（管理员）")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Integer> updateEmployee(@RequestBody UpdateEmployeeRequest updateEmployeeRequest) {
        if (updateEmployeeRequest == null || updateEmployeeRequest.getEmployeeId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "参数为空或者 id 为空");
        }
        int result = ipmsEmployeeService.updateEmployee(updateEmployeeRequest);
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
    public BaseResponse<Page<SafeEmployeeVO>> pagingFuzzyQuery(FuzzyQueryRequest fuzzyQueryRequest, HttpServletRequest request) {
        if (fuzzyQueryRequest == null || request == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Page<SafeEmployeeVO> safeEmployeeVOPage = ipmsEmployeeService.pagingFuzzyQuery(fuzzyQueryRequest, request);
        return ResultUtils.success(safeEmployeeVOPage);
    }

    /**
     * 职员编号自动生成
     *
     * @return
     */
    @GetMapping("/employeeCodeAutoGenerate")
    @ApiOperation(value = "职员编号自动生成")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<String> employeeCodeAutoGenerate() {
        String result = ipmsEmployeeService.employeeCodeAutoGenerate();
        if (result == null) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "编码自动生成器异常");
        }
        return ResultUtils.success(result);
    }
}
