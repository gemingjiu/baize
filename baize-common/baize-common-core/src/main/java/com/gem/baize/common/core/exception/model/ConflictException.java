package com.gem.baize.common.core.exception.model;

/**
 * 并发冲突异常（如乐观锁失败）
 */
public class ConflictException extends BaseException {
    public ConflictException(String message) {
        super(409, message);
    }
}


