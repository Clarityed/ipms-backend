package com.clarity.ipmsbackend.controller;

import com.clarity.ipmsbackend.annotation.AuthCheck;
import com.clarity.ipmsbackend.common.BaseResponse;
import com.clarity.ipmsbackend.common.ErrorCode;
import com.clarity.ipmsbackend.common.ResultUtils;
import com.clarity.ipmsbackend.constant.UserConstant;
import com.clarity.ipmsbackend.exception.BusinessException;
import com.clarity.ipmsbackend.model.dto.user.AddUserRequest;
import com.clarity.ipmsbackend.model.dto.user.UserLoginRequest;
import com.clarity.ipmsbackend.model.vo.SafeUserVO;
import com.clarity.ipmsbackend.service.IpmsUserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
 * 用户控制器
 *
 * @author: clarity
 * @date: 2023年02月20日 14:35
 */

@Api(tags = "用户管理接口")
@RestController
@Slf4j
@RequestMapping("/user")
public class IpmsUserController {

    @Resource
    private IpmsUserService ipmsUserService;

    /**
     * 用户登录接口
     *
     * @param userLoginRequest
     * @param request
     * @return
     */
    @PostMapping("/login")
    @ApiOperation(value = "用户登录（任意用户）")
    public BaseResponse<SafeUserVO> userLogin(@RequestBody UserLoginRequest userLoginRequest, HttpServletRequest request) {
        if (userLoginRequest == null || request == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        SafeUserVO safeUserVO = ipmsUserService.userLogin(userLoginRequest, request);
        return ResultUtils.success(safeUserVO);
    }

    /**
     * 获得当前在线用户信息
     *
     * @param request
     * @return
     */
    @PostMapping("/getLoginUser")
    @ApiOperation(value = "获取当前在线用户（任意用户）")
    public BaseResponse<SafeUserVO> getLoginUser(HttpServletRequest request) {
        if (request == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        SafeUserVO loginUser = ipmsUserService.getLoginUser(request);
        return ResultUtils.success(loginUser);
    }

    @PostMapping("/addUser")
    @ApiOperation(value = "增加用户（管理员）")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Long> addUser(@RequestBody AddUserRequest addUserRequest, HttpServletRequest request) {
        if (addUserRequest == null || request == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        long result = ipmsUserService.addUser(addUserRequest, request);
        return ResultUtils.success(result);
    }
}
