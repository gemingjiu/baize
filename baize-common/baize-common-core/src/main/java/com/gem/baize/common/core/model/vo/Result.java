package com.gem.baize.common.core.model.vo;

import com.gem.baize.common.core.exception.model.BaseException;
import org.springframework.http.HttpStatus;

import java.io.Serializable;
import java.util.Optional;
import java.util.function.Function;


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
        return of(code, message,data);
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

    // 提供 builder() 方法 - 改进版
    public static <T> Builder<T> builder() {
        return new Builder<>();
    }


    // 静态内部类：改进的 Builder
    public static class Builder<T> {
        private int code = HttpStatus.OK.value();
        private String message = "success";
        private T data;

        public Builder<T> code(int code) {
            this.code = code;
            return this;
        }

        public Builder<T> message(String message) {
            this.message = message;
            return this;
        }

        public Builder<T> data(T data) {
            this.data = data;
            return this;
        }

        // 构建指定类型的 Result
        @SuppressWarnings("unchecked")
        public <T> Result<T> build() {
            return new Result<>(code, message, (T) data);
        }

        // 构建无数据的 Result
        public <T> Result<T> buildWithoutData() {
            return new Result<>(code, message, null);
        }
    }

    // ===== 便捷方法 =====

    public boolean isSuccess() {
        return code == HttpStatus.OK.value();
    }

    public Optional<T> getOptionalData() {
        return Optional.ofNullable(data);
    }

    public <R> Result<R> map(Function<? super T, ? extends R> mapper) {
        if (data == null) {
            return new Result<>(code, message, null);
        }
        return new Result<>(code, message, mapper.apply(data));
    }

    // 如果 code 不是成功，抛出异常（用于链式调用）
    public T orElseThrow() {
        if (!isSuccess()) {
            throw new RuntimeException(message);
        }
        return data;
    }

    public T orElseThrow(Function<Result<T>, ? extends RuntimeException> exceptionProvider) {
        if (!isSuccess()) {
            throw exceptionProvider.apply(this);
        }
        return data;
    }

    // 获取数据或默认值
    public T orElse(T defaultValue) {
        return isSuccess() && data != null ? data : defaultValue;
    }
}
