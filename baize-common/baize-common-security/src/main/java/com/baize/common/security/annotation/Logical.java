package com.baize.common.security.annotation;

/**
 * 权限注解的验证模式
 *
 * @author gemj
 * @since 2023/08/22 14:26
 */


public enum Logical {
    /**
     * 必须具有所有的元素
     */
    AND,

    /**
     * 只需具有其中一个元素
     */
    OR
}