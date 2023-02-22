package com.clarity.ipmsbackend.utils;

import com.clarity.ipmsbackend.constant.UserConstant;

/**
 * 用户角色校验
 *
 * @author: clarity
 * @date: 2023年02月21日 14:51
 */

public class UserRoleValid {

    /**
     * 用户角色校验
     *
     * @param userRole
     * @return 0 - 错误，1 - 正确
     */
    public static int valid(String userRole) {
        //   - 用户身份只能是系统中拥有的
        int result = 0; // 标识输入的角色是否是系统角色，是的话 + 1
        for (String role : UserConstant.USER_ROLE_LIST) {
            if (role.equals(userRole)) {
                result++;
                break;
            }
        }
        return result;
    }
}
