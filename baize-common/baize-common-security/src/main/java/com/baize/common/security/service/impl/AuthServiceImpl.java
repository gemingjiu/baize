package com.baize.common.security.service.impl;

import com.baize.common.core.context.SecurityContext;
import com.baize.common.core.exception.BaseException;
import com.baize.common.core.utils.SpringUtils;
import com.baize.common.core.utils.text.StringUtils;
import com.baize.common.security.annotation.Logical;
import com.baize.common.security.annotation.RequiresPermissions;
import com.baize.common.security.annotation.RequiresRoles;
import com.baize.common.security.service.AuthService;
import com.baize.common.security.service.TokenService;
import com.baize.common.security.utils.SecurityUtils;
import com.baize.system.api.domain.vo.LoginUser;
import org.springframework.stereotype.Component;
import org.springframework.util.PatternMatchUtils;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import static com.baize.common.core.enums.BaizeExceptionEnum.NO_LOGIN_EXCEPTION;
import static com.baize.common.core.enums.BaizeExceptionEnum.NO_PERMISSION_EXCEPTION;
import static com.baize.common.core.enums.BaizeExceptionEnum.NO_ROLE_EXCEPTION;

/**
 * 认证服务
 *
 * @author gemj
 * @since 2023/08/22 14:21
 */
@Component
public class AuthServiceImpl implements AuthService {
    /**
     * 所有权限标识
     */
    private static final String ALL_PERMISSION = "*:*:*";

    /**
     * 管理员角色权限标识
     */
    private static final String SUPER_ADMIN = "admin";

    public TokenService tokenService = SpringUtils.getBean(TokenService.class);


    @Override
    public void logout() {
        String token = SecurityUtils.getToken();
        if (token == null) {
            return;
        }
        logoutByToken(token);
    }

    @Override
    public void logoutByToken(String token) {
        tokenService.delLoginUser(token);
    }

    @Override
    public void checkLogin() {
        getLoginUser();
    }

    /**
     * 获取当前用户缓存信息, 如果未登录，则抛出异常
     *
     * @return 用户缓存信息
     */
    public LoginUser getLoginUser() {
        String token = SecurityUtils.getToken();
        if (token == null) {
            throw new BaseException(NO_LOGIN_EXCEPTION, "未提供token");
        }
        LoginUser loginUser = SecurityUtils.getLoginUser();
        if (loginUser == null) {
            throw new BaseException(NO_LOGIN_EXCEPTION, "无效的token");
        }
        return loginUser;
    }

    @Override
    public LoginUser getLoginUser(String token) {
        return tokenService.getLoginUser(token);
    }

    @Override
    public void verifyLoginUserExpire(LoginUser loginUser) {
        tokenService.verifyToken(loginUser);
    }

    @Override
    public boolean hasRole(String role) {
        return hasRole(getRoleList(), role);
    }

    @Override
    public void checkRole(String role) {
        if (!hasRole(role)) {
            throw new BaseException(NO_ROLE_EXCEPTION, role);
        }
    }

    @Override
    public void checkRole(RequiresRoles requiresRoles) {
        if (requiresRoles.logical() == Logical.AND) {
            checkRoleAnd(requiresRoles.value());
        } else {
            checkRoleOr(requiresRoles.value());
        }
    }

    @Override
    public void checkRoleAnd(String... roles) {
        Set<String> roleList = getRoleList();
        for (String role : roles) {
            if (!hasRole(roleList, role)) {
                throw new BaseException(NO_ROLE_EXCEPTION, role);
            }
        }
    }

    @Override
    public void checkRoleOr(String... roles) {
        Set<String> roleList = getRoleList();
        for (String role : roles) {
            if (hasRole(roleList, role)) {
                return;
            }
        }
        if (roles.length > 0) {
            throw new BaseException(NO_ROLE_EXCEPTION, roles);
        }
    }


    @Override
    public boolean hasPermit(String permission) {
        return hasPermit(getPermitList(), permission);
    }

    @Override
    public void checkPermit(String permission) {
        if (!hasPermit(getPermitList(), permission)) {
            throw new BaseException(NO_PERMISSION_EXCEPTION, permission);
        }
    }

    @Override
    public void checkPermit(RequiresPermissions requiresPermissions) {
        SecurityContext.setPermission(StringUtils.join(requiresPermissions.value(), ","));
        if (requiresPermissions.logical() == Logical.AND) {
            checkPermitAnd(requiresPermissions.value());
        } else {
            checkPermitOr(requiresPermissions.value());
        }
    }

    @Override
    public void checkPermitAnd(String... permissions) {
        Set<String> permissionList = getPermitList();
        for (String permission : permissions) {
            if (!hasPermit(permissionList, permission)) {
                throw new BaseException(NO_PERMISSION_EXCEPTION, permission);
            }
        }
    }


    @Override
    public void checkPermitOr(String... permissions) {
        Set<String> permissionList = getPermitList();
        for (String permission : permissions) {
            if (hasPermit(permissionList, permission)) {
                return;
            }
        }
        if (permissions.length > 0) {
            throw new BaseException(NO_PERMISSION_EXCEPTION, permissions);
        }
    }

    /**
     * 获取当前账号的角色列表
     *
     * @return 角色列表
     */
    public Set<String> getRoleList() {
        try {
            LoginUser loginUser = getLoginUser();
            return loginUser.getRoles();
        } catch (Exception e) {
            return new HashSet<>();
        }
    }

    /**
     * 获取当前账号的权限列表
     *
     * @return 权限列表
     */
    public Set<String> getPermitList() {
        try {
            LoginUser loginUser = getLoginUser();
            return loginUser.getPermissions();
        } catch (Exception e) {
            return new HashSet<>();
        }
    }

    /**
     * 判断是否包含角色
     *
     * @param roles 角色列表
     * @param role  角色
     * @return 用户是否具备某角色权限
     */
    public boolean hasRole(Collection<String> roles, String role) {
        return roles.stream().filter(StringUtils::hasText).anyMatch(x -> SUPER_ADMIN.contains(x) || PatternMatchUtils.simpleMatch(x, role));
    }

    /**
     * 判断是否包含权限
     *
     * @param authorities 权限列表
     * @param permission  权限字符串
     * @return 用户是否具备某权限
     */
    private boolean hasPermit(Set<String> authorities, String permission) {
        return authorities.stream().filter(StringUtils::hasText).anyMatch(x -> ALL_PERMISSION.contains(x) || PatternMatchUtils.simpleMatch(x, permission));
    }
}
