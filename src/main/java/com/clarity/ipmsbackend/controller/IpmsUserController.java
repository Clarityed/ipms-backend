package com.clarity.ipmsbackend.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.clarity.ipmsbackend.annotation.AuthCheck;
import com.clarity.ipmsbackend.common.BaseResponse;
import com.clarity.ipmsbackend.common.ErrorCode;
import com.clarity.ipmsbackend.common.FuzzyQueryRequest;
import com.clarity.ipmsbackend.common.ResultUtils;
import com.clarity.ipmsbackend.constant.UserConstant;
import com.clarity.ipmsbackend.exception.BusinessException;
import com.clarity.ipmsbackend.model.dto.user.AddUserRequest;
import com.clarity.ipmsbackend.model.dto.user.UpdateUserRequest;
import com.clarity.ipmsbackend.model.dto.user.UserLoginRequest;
import com.clarity.ipmsbackend.model.dto.user.UserQueryRequest;
import com.clarity.ipmsbackend.model.entity.IpmsUser;
import com.clarity.ipmsbackend.model.vo.SafeUserVO;
import com.clarity.ipmsbackend.service.IpmsUserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

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
    @GetMapping("/getLoginUser")
    @ApiOperation(value = "获取当前在线用户（任意用户）")
    public BaseResponse<SafeUserVO> getLoginUser(HttpServletRequest request) {
        if (request == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        SafeUserVO loginUser = ipmsUserService.getLoginUser(request);
        return ResultUtils.success(loginUser);
    }

    /**
     * 增加用户
     *
     * @param addUserRequest
     * @param request
     * @return
     */
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

    /**
     * 更新用户信息（管理员不能修改自己的身份字段）
     *
     * @param updateUserRequest
     * @return
     */
    @PutMapping("/updateUser")
    @ApiOperation(value = "更新用户信息（管理员不能修改自己的身份字段）")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Integer> updateUser(@RequestBody UpdateUserRequest updateUserRequest) {
        if (updateUserRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        int result = ipmsUserService.updateUser(updateUserRequest);
        return ResultUtils.success(result);
    }

    /**
     * 根据 id 删除用户（管理员）
     *
     * @param id
     * @return
     */
    @DeleteMapping("/deleteUserById/{id}")
    @ApiOperation(value = "根据 id 删除用户（管理员）")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Integer> deleteUserById(@PathVariable("id") long id) {
        if (id <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "id 不合法");
        }
        int result = ipmsUserService.deleteUserById(id);
        return ResultUtils.success(result);
    }

    /**
     * 注销用户
     *
     * @param request
     * @return
     */
    @GetMapping("/logoutUser")
    @ApiOperation(value = "注销用户（任意用户）")
    public BaseResponse<Integer> logoutUser(HttpServletRequest request) {
        if (request == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        int result = ipmsUserService.logoutUser(request);
        return ResultUtils.success(result);
    }

    /**
     * 分页查询用户，且模糊查询
     *
     * @param fuzzyQueryRequest
     * @param request
     * @return
     */
    @GetMapping("/pagingFuzzyQuery")
    @ApiOperation(value = "分页查询用户，且模糊查询（管理员）")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Page<SafeUserVO>> pagingFuzzyQuery(FuzzyQueryRequest fuzzyQueryRequest, HttpServletRequest request) {
        if (fuzzyQueryRequest == null || request == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Page<SafeUserVO> safeUserVOPage = ipmsUserService.pagingFuzzyQuery(fuzzyQueryRequest, request);
        return ResultUtils.success(safeUserVOPage);
    }

    /**
     * 用户编号自动生成
     *
     * @return
     */
    @GetMapping("/userCodeAutoGenerate")
    @ApiOperation(value = "用户编号自动生成")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<String> userCodeAutoGenerate() {
        String result = ipmsUserService.userCodeAutoGenerate();
        if (result == null) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "编码自动生成器异常");
        }
        return ResultUtils.success(result);
    }
}
