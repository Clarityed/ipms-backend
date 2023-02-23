package com.clarity.ipmsbackend.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.plugins.pagination.PageDTO;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.clarity.ipmsbackend.common.ErrorCode;
import com.clarity.ipmsbackend.common.FuzzyQueryRequest;
import com.clarity.ipmsbackend.constant.UserConstant;
import com.clarity.ipmsbackend.exception.BusinessException;
import com.clarity.ipmsbackend.mapper.IpmsUserMapper;
import com.clarity.ipmsbackend.model.dto.user.AddUserRequest;
import com.clarity.ipmsbackend.model.dto.user.UpdateUserRequest;
import com.clarity.ipmsbackend.model.dto.user.UserLoginRequest;
import com.clarity.ipmsbackend.model.entity.IpmsUser;
import com.clarity.ipmsbackend.model.vo.SafeUserVO;
import com.clarity.ipmsbackend.service.IpmsUserService;
import com.clarity.ipmsbackend.utils.CodeAutoGenerator;
import com.clarity.ipmsbackend.utils.UserRoleValid;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
* @author Clarity
* @description 针对表【ipms_user】的数据库操作Service实现
* @createDate 2023-02-20 14:23:14
*/

@Slf4j
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

    @Override
    public long addUser(AddUserRequest addUserRequest, HttpServletRequest request) {
        // 1. 校验参数信息。
        //   - 参数是否为空
        String userCode = addUserRequest.getUserCode();
        String userName = addUserRequest.getUserName();
        String userAccount = addUserRequest.getUserAccount();
        String userPassword = addUserRequest.getUserPassword();
        String userRole = addUserRequest.getUserRole();
        if (StringUtils.isAnyBlank(userCode, userName, userAccount, userPassword, userRole)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "参数为空");
        }
        //   - 账号密码长度
        if (userAccount.length() < 7 || userAccount.length() > 11) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "账号长度小于 7 或者账号长度大于 11");
        }
        if (userPassword.length() < 9 || userPassword.length() > 16) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "密码长度小于 9 或者密码长度大于 16");
        }
        // 验证角色输入是否有错误
        int validResult = UserRoleValid.valid(userRole);
        if (validResult != 1) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "角色输入有误");
        }
        //   - 账号不能重复
        QueryWrapper<IpmsUser> userQueryWrapper = new QueryWrapper<>();
        userQueryWrapper.eq("user_account", userAccount);
        IpmsUser user = ipmsUserMapper.selectOne(userQueryWrapper);
        if (user != null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "账号重复");
        }
        //   - 用户编号不能重复
        userQueryWrapper = new QueryWrapper<>();
        userQueryWrapper.eq("user_code", userCode);
        user = ipmsUserMapper.selectOne(userQueryWrapper);
        if (user != null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户编号重复");
        }
        // 2. 加密密码
        String encryptPassword = DigestUtils.md5DigestAsHex((UserConstant.SALT + userPassword).getBytes());
        // 3. 插入数据
        user = new IpmsUser();
        BeanUtils.copyProperties(addUserRequest, user);
        user.setUserPassword(encryptPassword);
        user.setCreateTime(new Date());
        user.setUpdateTime(new Date());
        //   - 企业 id 绑定
        SafeUserVO loginUser = this.getLoginUser(request);
        user.setEnterpriseId(loginUser.getEnterpriseId());
        return ipmsUserMapper.insert(user);
    }

    @Override
    public int updateUser(UpdateUserRequest updateUserRequest) {
        // 1. 校验参数
        Long userId = updateUserRequest.getUserId();
        if (userId == null || userId <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "id 有误或者为空");
        }
        String userAccount = updateUserRequest.getUserAccount();
        String userCode = updateUserRequest.getUserCode();
        if (userAccount != null) {
            if (userAccount.length() < 7 || userAccount.length() > 11) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "账号长度小于 7 或者账号长度大于 11");
            }
        }
        String userPassword = updateUserRequest.getUserPassword();
        if (userPassword != null) {
            if (userPassword.length() < 9 || userPassword.length() > 16) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "密码长度小于 9 或者密码长度大于 16");
            }
        }
        //  3. 判断用户是否存在
        // 未修改前先用数据库查看用户信息，顺便判断用户是否存在
        IpmsUser user = ipmsUserMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }
        String oldUserAccount = user.getUserAccount();
        String oldUserCode = user.getUserCode();
        String oldUserRole = user.getUserRole();
        String userRole = updateUserRequest.getUserRole();
        if (userRole != null) {
            // 验证角色输入是否有错误
            if (!userRole.equals(oldUserRole)) {
                int validResult = UserRoleValid.valid(userRole);
                if (validResult != 1) {
                    throw new BusinessException(ErrorCode.PARAMS_ERROR, "角色输入有误");
                }
            }
            // 管理员无法修改自己的角色身份
            if (UserConstant.ADMIN_ROLE.equals(oldUserRole)) {
                userRole = null;
            }
        }
        // 账号不能重复
        if (userAccount != null && !userAccount.equals(oldUserAccount)) {
            QueryWrapper<IpmsUser> userQueryWrapper = new QueryWrapper<>();
            userQueryWrapper.eq("user_account", userAccount);
            long userAccountCount = ipmsUserMapper.selectCount(userQueryWrapper);
            if (userAccountCount == 1) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "账号重复");
            }
        }
        // 用户编号不能重复
        if (userCode != null && !userCode.equals(oldUserCode)) {
            QueryWrapper<IpmsUser> userQueryWrapper = new QueryWrapper<>();
            userQueryWrapper.eq("user_code", userCode);
            long userCodeCount = ipmsUserMapper.selectCount(userQueryWrapper);
            if (userCodeCount == 1) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户编号重复");
            }
        }
        // 修改用户信息
        IpmsUser updateUser = new IpmsUser();
        BeanUtils.copyProperties(updateUserRequest, updateUser);
        // 加密密码
        if (userPassword != null) {
            userPassword = DigestUtils.md5DigestAsHex((UserConstant.SALT + userPassword).getBytes());
            updateUser.setUserPassword(userPassword);
        }
        updateUser.setUserRole(userRole);
        updateUser.setUpdateTime(new Date());
        return ipmsUserMapper.updateById(updateUser);
    }

    @Override
    public int deleteUserById(long id) {
        if (id <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "id 不合法");
        }
        if (id == 1) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "无法删除管理员信息");
        }
        IpmsUser user = ipmsUserMapper.selectById(id);
        if (user == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "删除的数据不存在");
        }
        return ipmsUserMapper.deleteById(id);
    }

    @Override
    public int logoutUser(HttpServletRequest request) {
        request.getSession().removeAttribute(UserConstant.USER_LOGIN_STATE);
        return 1;
    }

    @Override
    public Page<SafeUserVO> pagingFuzzyQuery(FuzzyQueryRequest fuzzyQueryRequest, HttpServletRequest request) {
        SafeUserVO loginUser = getLoginUser(request);
        if (loginUser == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR);
        }
        if (fuzzyQueryRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "参数为空");
        }
        Page<IpmsUser> page = new Page<>(fuzzyQueryRequest.getCurrentPage(), fuzzyQueryRequest.getPageSize());
        QueryWrapper<IpmsUser> userQueryWrapper = new QueryWrapper<>();
        String fuzzyText = fuzzyQueryRequest.getFuzzyText();
        if (StringUtils.isNotBlank(fuzzyText)) {
            userQueryWrapper.like("user_id", fuzzyText).or()
                    .like("user_code", fuzzyText).or()
                    .like("user_name", fuzzyText).or()
                    .like("user_gender", fuzzyText).or()
                    .like("user_account", fuzzyText).or()
                    .like("user_role", fuzzyText).or();
        }
        Page<IpmsUser> userPage = ipmsUserMapper.selectPage(page, userQueryWrapper);
        List<SafeUserVO> safeUserVOList = userPage.getRecords().stream().map(ipmsUser -> {
            SafeUserVO safeUserVO = new SafeUserVO();
            BeanUtils.copyProperties(ipmsUser, safeUserVO);
            return safeUserVO;
        }).collect(Collectors.toList());
        // 关键步骤
        Page<SafeUserVO> safeUserVOPage = new PageDTO<>(userPage.getCurrent(), userPage.getSize(), userPage.getTotal());
        safeUserVOPage.setRecords(safeUserVOList);
        return safeUserVOPage;
    }

    @Override
    public String userCodeAutoGenerate() {
        QueryWrapper<IpmsUser> userQueryWrapper = new QueryWrapper<>();
        List<IpmsUser> ipmsUserList = ipmsUserMapper.selectList(userQueryWrapper);
        String userCode;
        if (ipmsUserList.size() == 0) {
            userCode = "KH00000";
        } else {
            IpmsUser lastUser = ipmsUserList.get(ipmsUserList.size() - 1);
            userCode = lastUser.getUserCode();
        }
        String nextUserCode = null;
        try {
             nextUserCode = CodeAutoGenerator.literallyCode(userCode);
        } catch (Exception e) {
            log.info("编码自动生成器异常");
        }
        return nextUserCode;
    }
}




