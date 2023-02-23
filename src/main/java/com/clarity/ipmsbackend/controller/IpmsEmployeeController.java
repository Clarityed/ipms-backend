package com.clarity.ipmsbackend.controller;

import com.clarity.ipmsbackend.annotation.AuthCheck;
import com.clarity.ipmsbackend.common.BaseResponse;
import com.clarity.ipmsbackend.common.ErrorCode;
import com.clarity.ipmsbackend.common.ResultUtils;
import com.clarity.ipmsbackend.constant.UserConstant;
import com.clarity.ipmsbackend.exception.BusinessException;
import com.clarity.ipmsbackend.model.dto.employee.AddEmployeeRequest;
import com.clarity.ipmsbackend.service.IpmsEmployeeService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

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
}
