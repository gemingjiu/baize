package com.gem.baize.common.core.enums;

public enum ErrorCode {

    SUCCESS(0, "成功"),

    // ===== 1xxxx 参数类错误 =====
    INVALID_PARAM(10001, "参数错误"),
    MISSING_PARAM(10002, "缺少必要参数"),
    PARAM_FORMAT_ERROR(10003, "参数格式错误"),

    // ===== 2xxxx 业务冲突 =====
    BUSINESS_CONFLICT(20001, "业务冲突"),
    DATA_ALREADY_EXISTS(20002, "数据已存在"),
    DATA_NOT_EXISTS(20003, "数据不存在"),

    // ===== 3xxxx 权限 / 认证 =====
    UNAUTHORIZED(30001, "未登录或登录已过期"),
    FORBIDDEN(30002, "无访问权限"),

    // ===== 5xxxx 系统异常 =====
    SYSTEM_ERROR(50000, "系统异常"),
    SERVICE_UNAVAILABLE(50001, "服务暂不可用"),
    DEPENDENCY_ERROR(50002, "依赖服务异常");

    private final int code;
    private final String message;

    ErrorCode(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public int code() {
        return code;
    }

    public String message() {
        return message;
    }
}
