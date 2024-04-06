package com.baize.common.core.exception;

/**
 * 文件异常
 *
 * @author gemj
 * @since 2024/04/06 14:38
 */
public class FileException extends BaseException {
    public FileException(Integer code, String message, Object data, Throwable e, String module) {
        super(code, message, data, e, module);
    }
}
