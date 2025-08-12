package com.gem.baize.common.core.exception.model;

/**
 * 违反完整性约束异常
 */
public class IntegrityViolationException extends BaseException {
    public IntegrityViolationException(String message) {
        super(409, message);
    }
}
