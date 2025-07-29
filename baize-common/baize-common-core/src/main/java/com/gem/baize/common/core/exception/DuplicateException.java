package com.gem.baize.common.core.exception;

/**
 * 重复数据错误
 */
public class DuplicateException extends BaseException {
    public DuplicateException(String message) {
        super(409, message);
    }
}
