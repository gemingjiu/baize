package com.gem.baize.common.core.model.vo;

import com.gem.baize.common.core.enums.ErrorCode;
import com.gem.baize.common.core.exception.model.BaseException;
import lombok.Data;
import org.springframework.http.HttpStatus;

import java.io.Serializable;

@Data
public class ApiResult<T> implements Serializable {
    private boolean success;
    private T data;
    private int errorCode;
    private String errorMessage;
    private int showType;
    private String traceId;
    private long timeStamp;

    public ApiResult() {
    }

    public ApiResult(int errorCode, String errorMessage) {
        this.success = errorCode == ErrorCode.SUCCESS.code();
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
        this.data = null;
        this.timeStamp = System.currentTimeMillis();
        this.traceId = null;
        this.showType = 0;
    }

    public ApiResult(ErrorCode errorCode, String errorMessage) {
        this.success = errorCode.code() == ErrorCode.SUCCESS.code();
        this.errorCode = errorCode.code();
        this.errorMessage = errorMessage;
        this.data = null;
        this.timeStamp = System.currentTimeMillis();
        this.traceId = null;
        this.showType = 0;
    }


    public ApiResult(int errorCode, String errorMessage, T data) {
        this.success = errorCode == ErrorCode.SUCCESS.code();
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
        this.data = data;
        this.timeStamp = System.currentTimeMillis();
        this.traceId = null;
        this.showType = 0;
    }

    public ApiResult(ErrorCode errorCode, String errorMessage, T data) {
        this.success = errorCode.code() == ErrorCode.SUCCESS.code();
        this.errorCode = errorCode.code();
        this.errorMessage = errorMessage;
        this.data = data;
        this.timeStamp = System.currentTimeMillis();
        this.traceId = null;
        this.showType = 0;
    }


    public static <T> ApiResult<T> of(int code, String message, T data) {
        return new ApiResult<>(code, message, data);
    }

    public static <T> ApiResult<T> of(int code, String message) {
        return new ApiResult<>(code, message);
    }

    // 成功响应（无数据）
    public static <T> ApiResult<T> ok() {
        return new ApiResult<>(ErrorCode.SUCCESS, "ok");
    }

    // 成功响应（带数据）
    public static <T> ApiResult<T> ok(T data) {
        return new ApiResult<>(ErrorCode.SUCCESS, "ok", data);
    }

    // 成功响应（带数据）自带描述
    public static <T> ApiResult<T> ok(T data, String message) {
        return new ApiResult<>(ErrorCode.SUCCESS, message, data);
    }

    // 错误响应（仅 code + message）
    public static <T> ApiResult<T> fail(int code, String message) {
        return of(code, message);
    }

    // 错误响应（code + message + data）
    public static <T> ApiResult<T> fail(int code, String message, T data) {
        return of(code, message, data);
    }

    // 从 BaseException 转换
    public static <T> ApiResult<T> fail(BaseException e) {
        return of(e.getErrorCode().code(), e.getMessage());
    }

    // 支持 HttpStatus（Spring 提供）
    public static <T> ApiResult<T> fail(HttpStatus status, String message) {
        return of(status.value(), message);
    }
}
