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
     * 权限缓存前缀
     */
    public static final String LOGIN_TOKEN_KEY = "LOGIN_TOKENS:";

    /**
     * 验证码 redis key
     */
    public static final String CAPTCHA_CODE_KEY = "CAPTCHA_CODES:";

    /**
     * 参数管理 cache key
     */
    public static final String SYS_CONFIG_KEY = "SYS_CONFIG:";

    /**
     * 字典管理 cache key
     */
    public static final String SYS_DICT_KEY = "SYS_DICT:";

    /**
     * 登录账户密码错误次数 redis key
     */
    public static final String PWD_ERR_CNT_KEY = "LOGIN_ERROR:";

    /**
     * 登录IP黑名单 cache key
     */
    public static final String SYS_LOGIN_BLACKLIST = SYS_CONFIG_KEY + "LOGIN_BLACKLIST";
}
