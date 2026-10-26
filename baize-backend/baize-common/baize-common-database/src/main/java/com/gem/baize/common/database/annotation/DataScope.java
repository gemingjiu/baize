package com.gem.baize.common.database.annotation;

import java.lang.annotation.*;

/**
 * 数据权限注解
 * <p>
 * 标注在 Mapper 方法上，控制数据权限过滤行为：
 * <ul>
 *   <li>{@code skip = true}：跳过数据权限过滤（内部管理类查询需要看全量）</li>
 *   <li>{@code deptAlias}：多表 JOIN 时 dept_id 列的表别名</li>
 *   <li>{@code userAlias}：多表 JOIN 时 create_by 列的表别名</li>
 * </ul>
 * <p>
 * 注意：本注解只影响数据权限拦截器，不影响租户拦截器。
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.TYPE})
public @interface DataScope {

    /**
     * 是否跳过数据权限过滤
     */
    boolean skip() default false;

    /**
     * dept_id 列所在表的别名（JOIN 场景）
     */
    String deptAlias() default "";

    /**
     * create_by 列所在表的别名（JOIN 场景）
     */
    String userAlias() default "";
}
