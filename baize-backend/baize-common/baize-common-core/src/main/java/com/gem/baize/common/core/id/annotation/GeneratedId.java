package com.gem.baize.common.core.id.annotation;


import java.lang.annotation.*;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface GeneratedId {
    /**
     * ID生成策略
     * 可选值：uuid, snowflake, increment 等
     */
    String strategy() default "uuid";

    /**
     * ID前缀，可为空
     * 例如："ORDER_" 会生成 "ORDER_123456" 这样的ID
     */
    String prefix() default "";

    /**
     * ID长度限制(仅对某些策略有效)
     * 0表示不限制
     */
    int length() default 0;

    /**
     * 是否包含时间戳(仅对某些策略有效)
     */
    boolean timestamp() default false;
}
