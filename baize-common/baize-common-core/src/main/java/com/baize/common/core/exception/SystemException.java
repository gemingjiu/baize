package com.baize.common.core.exception;

import com.baize.common.core.enums.BaizeException;

/**
 * 系统服务异常
 *
 * @author gemj
 * @since 2024/04/06 14:55
 */
public class SystemException extends BaseException {
    private static final String module = "system";

    public SystemException(BaizeException exceptionEnum, Object data, Throwable e) {
        super(exceptionEnum.getCode(), exceptionEnum.getMessage(), data, e, module);
    }

    public SystemException(BaizeException exceptionEnum, Object data) {
        super(exceptionEnum.getCode(), exceptionEnum.getMessage(), data, null, module);
    }
}
