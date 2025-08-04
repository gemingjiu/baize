package com.gem.baize.common.core.model.vo;

import com.gem.baize.common.core.exception.BaseException;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Result<T> implements Serializable {
    private int code;
    private String message;
    private T data;

    public static <T> Result<T> newResult(int code, String message, T data) {
        return new Result<>(code, message, data);
    }

    // 成功响应（无数据）
    public static <T> Result<T> success() {
        return new Result<>(HttpStatus.OK.value(), "success", null);
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
        return newResult(code, message, null);
    }

    // 错误响应（code + message + data）
    public static <T> Result<T> error(int code, String message, T data) {
        return newResult(code, message, data);
    }

    // 从 BaseException 转换
    public static <T> Result<T> error(BaseException e) {
        return newResult(e.getCode(), e.getMessage(), null);
    }

    // 支持 HttpStatus（Spring 提供）
    public static <T> Result<T> error(HttpStatus status, String message) {
        return newResult(status.value(), message, null);
    }

    // 提供 builder() 方法
    public static <T> ResultBuilder<T> builder() {
        return new ResultBuilder<>();
    }

    // 静态内部类：ResultBuilder
    public static class ResultBuilder<T> {
        private int code;
        private String message;
        private T data;

        public ResultBuilder<T> code(int code) {
            this.code = code;
            return this;
        }

        public ResultBuilder<T> message(String message) {
            this.message = message;
            return this;
        }

        public ResultBuilder<T> data(T data) {
            this.data = data;
            return this;
        }

        // 最终构建 Result 对象
        public Result<T> build() {
            return newResult(this.code, this.message, this.data);
        }
    }
}

