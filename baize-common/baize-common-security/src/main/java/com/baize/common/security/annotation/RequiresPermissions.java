package com.baize.common.security.annotation;

/**
 * 权限认证：必须具有指定权限才能进入该方法
 *
 * @author gemj
 * @since 2023/08/22 14:26
 */

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.TYPE})
public @interface RequiresPermissions {
    /**
     * 需要校验的权限码
     */
    String[] value() default {};

    /**
     * 验证模式：AND | OR，默认AND
     */
    Logical logical() default Logical.AND;
}