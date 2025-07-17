package com.gem.baize.common.core.exception;

import lombok.Data;


@Data
public class BusinessException extends RuntimeException {
    private Integer code;

    public BusinessException(String message) {
        this(400, message);
    }

    public BusinessException(Integer code, String message) {
        super(message);
        this.code = code;
    }
}