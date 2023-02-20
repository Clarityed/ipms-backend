package com.clarity.ipmsbackend.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.clarity.ipmsbackend.common.ErrorCode;
import com.clarity.ipmsbackend.constant.UserConstant;
import com.clarity.ipmsbackend.exception.BusinessException;
import com.clarity.ipmsbackend.mapper.IpmsUserMapper;
import com.clarity.ipmsbackend.model.dto.user.UserLoginRequest;
import com.clarity.ipmsbackend.model.entity.IpmsUser;
import com.clarity.ipmsbackend.model.vo.SafeUserVO;
import com.clarity.ipmsbackend.service.IpmsUserService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
* @author Clarity
* @description 针对表【ipms_user】的数据库操作Service实现
* @createDate 2023-02-20 14:23:14
*/
@Service
public class IpmsUserServiceImpl extends ServiceImpl<IpmsUserMapper, IpmsUser>
    implements IpmsUserService{

    @Resource
    private IpmsUserMapper ipmsUserMapper;

    @Override
    public SafeUserVO userLogin(UserLoginRequest userLoginRequest, HttpServletRequest request) {
        // 1. 进行账号和密码长度校验等等校验
        String userAccount = userLoginRequest.getUserAccount();
        String userPassword = userLoginRequest.getUserPassword();
        if (StringUtils.isAnyBlank(userAccount, userPassword)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "账号或者密码为空");
        }
        if (userAccount.length() < 7 || userAccount.length() > 11) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "账号长度小于 7 或者账号长度大于 11");
        }
        if (userPassword.length() < 9 || userPassword.length() > 16) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "密码长度小于 9 或者密码长度大于 16");
        }
        //2. 加密密码
        String encryptPassword = DigestUtils.md5DigestAsHex((UserConstant.SALT + userPassword).getBytes());
        //3. 查询用户是否存在
        QueryWrapper<IpmsUser> userQueryWrapper = new QueryWrapper<>();
        userQueryWrapper.eq("user_account", userAccount);
        userQueryWrapper.eq("user_password", encryptPassword);
        IpmsUser user = ipmsUserMapper.selectOne(userQueryWrapper);
        if (user == null) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "账号或者密码错误");
        }
        //4. 用户信息进行敏感信息脱敏返回
        SafeUserVO safeUserVO = new SafeUserVO();
        BeanUtils.copyProperties(user, safeUserVO);
        //5. 将用户信息存入 session 中
        request.getSession().setAttribute(UserConstant.USER_LOGIN_STATE, safeUserVO);
        return safeUserVO;
    }

    @Override
    public SafeUserVO getLoginUser(HttpServletRequest request) {
        if (request == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Object safeUser = request.getSession().getAttribute(UserConstant.USER_LOGIN_STATE);
        if (safeUser == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR);
        }
        return (SafeUserVO) safeUser;
    }
}




