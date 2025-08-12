package com.gem.baize.common.core.exception.model;

/**
 * 请求参数错误异常
 */
public class BadRequestException extends BaseException {
    public BadRequestException(String message) {
        super(400, message);
    }
}


