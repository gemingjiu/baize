package com.gem.baize.common.webmvc.aspect;

import com.gem.baize.common.core.annotation.RequiresPermission;
import com.gem.baize.common.core.exception.model.ForbiddenException;
import com.gem.baize.common.webmvc.context.RequestContextHolder;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 接口权限校验切面
 * <p>
 * 约定 JWT 的 role claim 存放逗号分隔的权限码，其中 {@link #SUPER_ADMIN_PERMISSION} 视为超级管理员；
 * 权限码必须与 @RequiresPermission 的 value 精确匹配，禁止使用角色名或前缀匹配，避免越权。
 */
@Slf4j
@Aspect
@Component
public class PermissionAspect {

    /**
     * 超级管理员权限标识：拥有该权限码即拥有全部权限
     */
    private static final String SUPER_ADMIN_PERMISSION = "*:*:*";

    /**
     * 拦截带有 @RequiresPermission 注解的方法
     */
    @Before("@annotation(requiresPermission)")
    public void checkPermission(RequiresPermission requiresPermission) {
        String requiredPerm = requiresPermission.value();
        // 注解未指定权限码时不校验
        if (StringUtils.isBlank(requiredPerm)) {
            return;
        }
        String role = RequestContextHolder.getRole();
        if (StringUtils.isBlank(role)) {
            throw new ForbiddenException("无权访问");
        }

        Set<String> permissions = Arrays.stream(role.split(","))
                .map(String::trim)
                .filter(StringUtils::isNotBlank)
                .collect(Collectors.toSet());

        // 超级管理员直接放行
        if (permissions.contains(SUPER_ADMIN_PERMISSION)) {
            return;
        }

        // 权限码必须精确匹配，不做角色名包含判断，也不做前缀匹配
        if (!permissions.contains(requiredPerm.trim())) {
            log.warn("权限校验失败: userId={}, permissions={}, requiredPerm={}",
                    RequestContextHolder.getUserId(), role, requiredPerm);
            throw new ForbiddenException("无权访问: " + requiredPerm);
        }
    }
}
