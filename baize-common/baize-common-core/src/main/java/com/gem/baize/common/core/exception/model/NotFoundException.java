package com.gem.baize.common.core.exception.model;

/**
 * 资源未找到异常
 */
public class NotFoundException extends BaseException {
    public NotFoundException(String message) {
        super(404, message);
    }
}

