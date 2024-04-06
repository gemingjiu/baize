package com.baize.common.core.constant;

/**
 * 缓存常量信息
 *
 * @author gemj
 */
public class CacheConstants {
    /**
     * 缓存有效期，默认720（分钟）
     */
    public static final long EXPIRATION = 720;

    /**
     * 缓存刷新时间，默认120（分钟）
     */
    public static final long REFRESH_TIME = 120;

    /**
     * 密码最大错误次数
     */
    public static final int PASSWORD_MAX_RETRY_COUNT = 5;

    /**
     * 密码锁定时间，默认10（分钟）
     */
    public static final long PASSWORD_LOCK_TIME = 10;

    /**
     * 权限缓存 redis key prefix
     */
    public static final String LOGIN_TOKEN_PREFIX = "LOGIN_TOKENS:";

    /**
     * 验证码 redis key prefix
     */
    public static final String CAPTCHA_PREFIX = "CAPTCHA_CODES:";

    /**
     * 参数管理 cache key prefix
     */
    public static final String SYS_CONFIG_PREFIX = "SYS_CONFIG:";

    /**
     * 字典管理 cache key prefix
     */
    public static final String SYS_DICT_PREFIX = "SYS_DICT:";

    /**
     * 登录账户密码错误次数 redis key prefix
     */
    public static final String LOGIN_ERROR_PREFIX = "LOGIN_ERROR:";

    /**
     * 登录IP黑名单 cache key prefix
     */
    public static final String LOGIN_BLACKLIST_PREFIX = SYS_CONFIG_PREFIX + "LOGIN_BLACKLIST";
}
