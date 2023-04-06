package com.clarity.ipmsbackend.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.clarity.ipmsbackend.common.FuzzyQueryRequest;
import com.clarity.ipmsbackend.model.dto.user.AddUserRequest;
import com.clarity.ipmsbackend.model.dto.user.UpdateUserRequest;
import com.clarity.ipmsbackend.model.dto.user.UserLoginRequest;
import com.clarity.ipmsbackend.model.entity.IpmsUser;
import com.clarity.ipmsbackend.model.vo.SafeUserVO;

import javax.servlet.http.HttpServletRequest;

/**
* @author Clarity
* @description 针对表【ipms_user】的数据库操作Service
* @createDate 2023-02-20 14:23:14
*/
public interface IpmsUserService extends IService<IpmsUser> {

    /**
     * 用户登录
     *
     * @param userLoginRequest
     * @param request
     * @return
     */
    SafeUserVO userLogin(UserLoginRequest userLoginRequest, HttpServletRequest request);

    /**
     * 获取当前登录用户信息
     *
     * @param request
     * @return
     */
    SafeUserVO getLoginUser(HttpServletRequest request);

    /**
     * 创建用户
     *
     * @param addUserRequest
     * @return
     */
    long addUser(AddUserRequest addUserRequest, HttpServletRequest request);

    /**
     * 更新用户
     *
     * @param updateUserRequest
     * @param request
     * @return
     */
    int updateUser(UpdateUserRequest updateUserRequest, HttpServletRequest request);

    /**
     * 根据 id 删除用户
     *
     * @param id
     * @return
     */
    int deleteUserById(long id);

    /**
     * 注销用户
     *
     * @param request
     * @return
     */
    int logoutUser(HttpServletRequest request);

    /**
     * 分页查询用户，且数据脱敏，且支持模糊查询
     *
     * @param fuzzyQueryRequest
     * @param request
     * @return
     */
    Page<SafeUserVO> pagingFuzzyQuery(FuzzyQueryRequest fuzzyQueryRequest, HttpServletRequest request);

    /**
     * 用户编号自动生成
     *
     * @return
     */
    String userCodeAutoGenerate();
}
