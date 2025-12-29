package com.gem.baize.common.core.model.vo;

import com.gem.baize.common.core.exception.model.BaseException;
import lombok.Data;
import org.springframework.http.HttpStatus;

import java.io.Serializable;

@Data
public class Result<T> implements Serializable {
    private final Integer code;
    private final String message;
    private final T data;


    public Result(int code, String message) {
        this.code = code;
        this.message = message;
        this.data = null;
    }

    public Result(Integer code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }


    public static <T> Result<T> of(Integer code, String message, T data) {
        return new Result<>(code, message, data);
    }

    public static <T> Result<T> of(Integer code, String message) {
        return new Result<>(code, message);
    }

    // 成功响应（无数据）
    public static <T> Result<T> success() {
        return new Result<>(HttpStatus.OK.value(), "success");
    }

    // 成功响应（带数据）
    public static <T> Result<T> success(T data) {
        return new Result<>(HttpStatus.OK.value(), "success", data);
    }

    // 成功响应（带数据）自带描述
    public static <T> Result<T> success(T data, String message) {
        return new Result<>(HttpStatus.OK.value(), message, data);
    }

    // 错误响应（仅 code + message）
    public static <T> Result<T> error(int code, String message) {
        return of(code, message);
    }

    // 错误响应（code + message + data）
    public static <T> Result<T> error(int code, String message, T data) {
        return of(code, message, data);
    }

    // 从 BaseException 转换
    public static <T> Result<T> error(BaseException e) {
//        return of(e.getCode(), e.getMessage());
        return null;
    }

    // 支持 HttpStatus（Spring 提供）
    public static <T> Result<T> error(HttpStatus status, String message) {
        return of(status.value(), message);
    }
}
