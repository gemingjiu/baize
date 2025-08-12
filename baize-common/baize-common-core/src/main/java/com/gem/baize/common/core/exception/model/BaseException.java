package com.gem.baize.common.core.exception.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 基础异常类
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class BaseException extends RuntimeException {
    private Integer code;

    public BaseException(String message) {
        this(400, message);
    }

    public BaseException(Integer code, String message) {
        super(message);
        this.code = code;
    }
}