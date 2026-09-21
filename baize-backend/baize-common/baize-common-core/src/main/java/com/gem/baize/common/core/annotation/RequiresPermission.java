package com.gem.baize.common.core.annotation;

import java.lang.annotation.*;

/**
 * 接口权限校验注解
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RequiresPermission {
    /**
     * 权限标识
     */
    String value() default "";

    /**
     * 逻辑关系（AND / OR）
     */
    Logical logical() default Logical.AND;

    enum Logical {
        AND, OR
    }
}
