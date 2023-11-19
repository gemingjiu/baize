package com.baize.common.core.constant;

/**
 * 通用常量信息
 *
 * @author gemj
 */
public class Constants {
    /**
     * UTF-8 字符集
     */
    public static final String UTF8 = "UTF-8";

    /**
     * GBK 字符集
     */
    public static final String GBK = "GBK";

    /**
     * www主域
     */
    public static final String WWW_PREFIX = "www.";

    /**
     * RMI 远程方法调用
     */
    public static final String RMI_PREFIX = "rmi:";


    /**
     * LDAP 远程方法调用
     */
    public static final String LDAP_PREFIX = "ldap:";

    /**
     * LDAPS 远程方法调用
     */
    public static final String LDAPS_PREFIX = "ldaps:";

    /**
     * http请求
     */
    public static final String HTTP_PREFIX = "http://";

    /**
     * https请求
     */
    public static final String HTTPS_PREFIX = "https://";


    /**
     * 成功标记
     */
    public static final Integer SUCCESS = 200;

    /**
     * 失败标记
     */
    public static final Integer FAIL = 500;

    /**
     * 登录成功状态
     */
    public static final String SUCCESS_STATUS = "0";

    /**
     * 登录失败状态
     */
    public static final String FAIL_STATUS = "1";

    /**
     * 登录成功
     */
    public static final String LOGIN_SUCCESS = "Success";

    /**
     * 注销
     */
    public static final String LOGOUT = "Logout";

    /**
     * 注册
     */
    public static final String REGISTER = "Register";

    /**
     * 登录失败
     */
    public static final String LOGIN_FAIL = "Error";

    /**
     * 当前记录起始索引
     */
    public static final String PAGE_NUM = "pageNum";

    /**
     * 每页显示记录数
     */
    public static final String PAGE_SIZE = "pageSize";

    /**
     * 排序列
     */
    public static final String ORDER_BY_COLUMN = "orderByColumn";

    /**
     * 排序的方向 "asc"
     */
    public static final String ASC = "asc";
    /**
     * 排序的方向 "desc"
     */
    public static final String DESC = "desc";

    /**
     * 验证码有效期（分钟）
     */
    public static final long CAPTCHA_EXPIRATION = 2;

    /**
     * 资源映射路径 前缀
     */
    public static final String RESOURCE_PREFIX = "/profile";

    /**
     * 自动识别json对象白名单配置（仅允许解析的包名，范围越小越安全）
     */
    public static final String[] JSON_WHITELIST_STR = {"org.springframework", "com.baize"};
}
