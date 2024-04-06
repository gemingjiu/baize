package com.baize.common.core.exception;

/**
 * 数据库异常
 *
 * @author gemj
 * @since 2024/04/06 14:39
 */
public class DBException extends BaseException {
    public DBException(Integer code, String message, Object data, Throwable e, String module) {
        super(code, message, data, e, module);
    }
}
