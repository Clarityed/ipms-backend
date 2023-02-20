package com.clarity.ipmsbackend.service;

import com.baomidou.mybatisplus.extension.service.IService;
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
}
