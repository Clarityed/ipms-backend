package com.clarity.ipmsbackend.constant;

import java.util.Arrays;
import java.util.List;

/**
 * 用户常量
 *
 * @author: clarity
 * @date: 2023年02月20日 15:38
 */
public class UserConstant {

    /**
     * 用户登录态（用于存放在 session 中用户信息的 key）
     */
    public static final String USER_LOGIN_STATE = "userLoginState";

    /**
     * 盐值混淆加密的信息
     */
    public static final String SALT = "clarity";

    /**
     * 管理员权限
     */
    public static final String ADMIN_ROLE = "管理员";

    /**
     * 开发员权限
     */
    public static final String DEVELOP_ROLE = "开发员";

    /**
     * 开发主管权限
     */
    public static final String DEVELOP_ROLE_SUPER = "开发主管";

    /**
     * 销售员权限
     */
    public static final String SALE_ROLE = "销售员";

    /**
     * 销售主管权限
     */
    public static final String SALE_ROLE_SUPER = "销售主管";

    /**
     * 生产员权限
     */
    public static final String PRODUCT_ROLE = "生产员";

    /**
     * 生产主管权限
     */
    public static final String PRODUCT_ROLE_SUPER = "生产主管";

    /**
     * 采购员权限
     */
    public static final String BUY_ROLE = "采购员";

    /**
     * 采购主管权限
     */
    public static final String BUY_ROLE_SUPER = "采购主管";

    /**
     * 仓管员权限
     */
    public static final String STORE_ROLE = "仓管员";

    /**
     * 仓管主管权限
     */
    public static final String STORE_ROLE_SUPER = "仓管主管";

    /**
     * 用户角色列表
     */
    public static final List<String> USER_ROLE_LIST = Arrays.asList("开发员", "开发主管", "销售员", "销售主管",
            "生产员", "生产主管", "采购员", "采购主管", "仓管员", "仓管主管");
}
