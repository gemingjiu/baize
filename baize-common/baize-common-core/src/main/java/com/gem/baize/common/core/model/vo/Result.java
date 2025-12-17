package com.gem.baize.common.core.model.vo;

import com.gem.baize.common.core.exception.model.BaseException;
import lombok.Data;
import org.springframework.http.HttpStatus;

import java.io.Serial;
import java.io.Serializable;
import java.util.Optional;
import java.util.function.Function;

@Data
public class Result<T> implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private int code;
    private String message;
    private T data;

    private Result(int code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }

    private Result(int code, String message) {
        this(code, message, null);
    }

    public static <T> Result<T> newResult(int code, String message, T data) {
        return new Result<>(code, message, data);
    }

    public static Result<Void> newResult(int code, String message) {
        return new Result<>(code, message);
    }

    // 成功响应（无数据）
    public static Result<Void> success() {
        return newResult(HttpStatus.OK.value(), "success");
    }

    // 成功响应（带数据）
    public static <T> Result<T> success(T data) {
        return newResult(HttpStatus.OK.value(), "success", data);
    }

    // 成功响应（带数据）自带描述
    public static <T> Result<T> success(T data, String message) {
        return newResult(HttpStatus.OK.value(), message, data);
    }

    // 错误响应（仅 code + message）
    public static Result<Void> error(int code, String message) {
        return newResult(code, message);
    }

    // 错误响应（code + message + data）
    public static <T> Result<T> error(int code, String message, T data) {
        return newResult(code, message, data);
    }

    // 从 BaseException 转换
    public static Result<Void> error(BaseException e) {
        return newResult(e.getCode(), e.getMessage());
    }

    // 支持 HttpStatus（Spring 提供）
    public static Result<Void> error(HttpStatus status, String message) {
        return newResult(status.value(), message);
    }

    // 提供 builder() 方法 - 改进版
    public static Builder builder() {
        return new Builder();
    }

    // 静态内部类：改进的 Builder
    public static class Builder {
        private int code = HttpStatus.OK.value();
        private String message = "success";
        private Object data;

        public Builder code(int code) {
            this.code = code;
            return this;
        }

        public Builder message(String message) {
            this.message = message;
            return this;
        }

        public Builder data(Object data) {
            this.data = data;
            return this;
        }

        // 构建指定类型的 Result
        @SuppressWarnings("unchecked")
        public <T> Result<T> build() {
            return new Result<>(code, message, (T) data);
        }

        // 构建无数据的 Result
        public Result<Void> buildWithoutData() {
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
