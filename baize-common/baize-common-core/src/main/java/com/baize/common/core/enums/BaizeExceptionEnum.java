package com.baize.common.core.enums;

public enum BaizeExceptionEnum {
    /**
     * 系统相关
     */
    SYSTEM_EXCEPTION(100001, "系统异常"),
    REQUEST_INVALID(100003, "请求无效"),
    TOO_MANY_REQUESTS(100004, "请求过于频繁，清稍微重试！"),
    UTILS_EXCEPTION(100005, "工具类异常"),
    /**
     * 网关相关
     */
    CAPTCHA_EXCEPTION(200001, "验证码异常"),
    NO_LOGIN_EXCEPTION(200002, "登录异常"),
    NO_PERMISSION_EXCEPTION(200003, "权限异常"),
    NO_ROLE_EXCEPTION(200004, "角色异常"),

    SERVICE_EXCEPTION(200005, "服务异常"),

    SERVICE_UNAVAILABLE(200006, "服务不可用"),
    /**
     * 数据库相关
     */
    DB_EXCEPTION(300001, "连接数据库出现异常，请联系系统管理员！"),
    DB_DATA_EXCEPTION(300002, "数据异常，请联系系统管理员！"),
    DB_DUPLICATE_KEY_EXCEPTION(300003, "数据已存在，请检查！"),
    ;

    /**
     * 错误码
     */
    private int code;

    /**
     * 描述
     */
    private String msg;

    BaizeExceptionEnum(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public Integer getCode() {
        return this.code;
    }

    public void setCode(int code) {
        this.code = code;
    }


    public String getMsg() {
        return this.msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
