package com.gem.baize.common.core.exception.model;

/**
 * 权限异常
 */
public class ForbiddenException extends BaseException {
    public ForbiddenException(String message) {
        super(403, message);
    }
}
