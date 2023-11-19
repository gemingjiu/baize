package com.baize.common.security.service;

import com.baize.common.security.annotation.RequiresPermissions;
import com.baize.common.security.annotation.RequiresRoles;
import com.baize.system.api.domain.vo.LoginUser;

/**
 * Token 权限验证工具类
 *
 * @author gemj
 * @since 2023/08/22 14:16
 */

public interface AuthService {

    /**
     * 会话注销
     */
    public void logout();

    /**
     * 会话注销，根据指定Token
     *
     * @param token 指定token
     */
    public void logoutByToken(String token);

    /**
     * 检验当前会话是否已经登录，如未登录，则抛出异常
     */
    public void checkLogin();

    /**
     * 获取当前登录用户信息
     *
     * @param token 指定token
     * @return 用户信息
     */
    public LoginUser getLoginUser(String token);

    /**
     * 验证当前用户有效期
     *
     * @param loginUser 用户信息
     */
    public void verifyLoginUserExpire(LoginUser loginUser);

    /**
     * 当前账号是否含有指定角色标识, 返回true或false
     *
     * @param role 角色标识
     * @return 是否含有指定角色标识
     */
    public boolean hasRole(String role);

    /**
     * 当前账号是否含有指定角色标识, 如果验证未通过，则抛出异常: NotRoleException
     *
     * @param role 角色标识
     */
    public void checkRole(String role);

    /**
     * 根据注解传入参数鉴权, 如果验证未通过，则抛出异常: NotRoleException
     *
     * @param requiresRoles 角色权限注解
     */
    public void checkRole(RequiresRoles requiresRoles);

    /**
     * 当前账号是否含有指定角色标识 [指定多个，必须全部验证通过]
     *
     * @param roles 角色标识数组
     */
    public void checkRoleAnd(String... roles);

    /**
     * 当前账号是否含有指定角色标识 [指定多个，只要其一验证通过即可]
     *
     * @param roles 角色标识数组
     */
    public void checkRoleOr(String... roles);

    /**
     * 当前账号是否含有指定权限, 返回true或false
     *
     * @param permission 权限码
     * @return 是否含有指定权限
     */
    public boolean hasPermit(String permission);

    /**
     * 当前账号是否含有指定权限, 如果验证未通过，则抛出异常: NotPermissionException
     *
     * @param permission 权限码
     */
    public void checkPermit(String permission);

    /**
     * 根据注解传入参数鉴权, 如果验证未通过，则抛出异常: NotPermissionException
     *
     * @param requiresPermissions 权限注解
     */
    public void checkPermit(RequiresPermissions requiresPermissions);

    /**
     * 当前账号是否含有指定权限 [指定多个，必须全部验证通过]
     *
     * @param permissions 权限码数组
     */
    public void checkPermitAnd(String... permissions);

    /**
     * 当前账号是否含有指定权限 [指定多个，只要其一验证通过即可]
     *
     * @param permissions 权限码数组
     */
    public void checkPermitOr(String... permissions);
}
