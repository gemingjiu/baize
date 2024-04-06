package com.baize.common.core.exception;

import com.baize.common.core.enums.BaizeException;

/**
 * 接口异常
 *
 * @author gemj
 * @since 2024/04/05 18:52
 */
public class ApiException extends BaseException {
    public ApiException(Integer code, String message, Object data, Throwable e, String module) {
        super(code, message, data, e, module);
    }

    public ApiException(BaizeException exceptionEnum, Object data, Throwable e, String module) {
        super(exceptionEnum.getCode(), exceptionEnum.getMessage(), data, e, module);
    }
}
