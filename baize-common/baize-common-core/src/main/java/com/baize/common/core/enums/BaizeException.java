package com.baize.common.core.enums;

public enum BaizeException {
    /**
     * 系统相关
     */
    SYSTEM_EXCEPTION(1001, "系统异常"), REQUEST_INVALID(1002, "请求无效"), TOO_MANY_REQUESTS(1003, "请求过于频繁，清稍微重试！"),
    UTILS_EXCEPTION(1004, "工具类异常"), IMPORT_EXCEPTION(1005, "导入异常"), EXPORT_EXCEPTION(1006, "导出异常"),
    /**
     * 网关相关
     */
    CAPTCHA_EXCEPTION(2001, "验证码异常"), NO_LOGIN_EXCEPTION(2002, "登录异常"), NO_PERMISSION_EXCEPTION(2003, "权限异常"),
    NO_ROLE_EXCEPTION(2004, "角色异常"), SERVICE_EXCEPTION(2005, "服务异常"), SERVICE_UNAVAILABLE(2006, "服务不可用"),
    /**
     * 数据库相关
     */
    DB_EXCEPTION(3001, "连接数据库出现异常，请联系系统管理员！"), DB_DATA_EXCEPTION(3002, "数据异常，请联系系统管理员！"),
    DB_DUPLICATE_KEY_EXCEPTION(3003, "数据已存在，请检查！"), DB_TABLE_EXCEPTION(3004, "表不存在");

    /**
     * 错误码
     */
    private Integer code;

    /**
     * 异常类型
     */
    private String message;

    BaizeException(Integer code, String message) {
        this.code = code;
        this.message = message;
    }

    public Integer getCode() {
        return this.code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getMessage() {
        return this.message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
