package com.gem.baize.common.core.exception;

/**
 * 数据创建异常
 */
public class DataCreationException extends BaseException {
    public DataCreationException(String message) {
        super(500, message);
    }
}
