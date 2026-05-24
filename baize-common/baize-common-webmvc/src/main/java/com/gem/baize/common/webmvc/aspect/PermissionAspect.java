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
 */
@Slf4j
@Aspect
@Component
public class PermissionAspect {

    /**
     * 拦截带有 @RequiresPermission 注解的方法
     */
    @Before("@annotation(requiresPermission)")
    public void checkPermission(RequiresPermission requiresPermission) {
        String requiredPerm = requiresPermission.value();
        if (StringUtils.isBlank(requiredPerm)) {
            return;
        }

        // 获取当前用户的角色信息（简化处理：假设角色即权限）
        String role = RequestContextHolder.getRole();
        if (StringUtils.isBlank(role)) {
            throw new ForbiddenException("无权访问");
        }

        // 超级管理员直接放行
        if (role.contains("admin") || role.contains("ADMIN")) {
            return;
        }

        // 校验权限
        Set<String> permissions = Arrays.stream(role.split(","))
                .map(String::trim)
                .collect(Collectors.toSet());

        // 如果所需权限为空，则放行
        if (StringUtils.isBlank(requiredPerm)) {
            return;
        }

        // 简单匹配：检查用户的角色中是否包含所需权限
        // 实际项目中应该从缓存或数据库中查询用户的具体权限列表
        boolean hasPermission = permissions.stream()
                .anyMatch(p -> p.equals(requiredPerm) || p.startsWith(requiredPerm.split(":")[0]));

        if (!hasPermission) {
            log.warn("权限校验失败: userRole={}, requiredPerm={}", role, requiredPerm);
            throw new ForbiddenException("无权访问: " + requiredPerm);
        }
    }
}
