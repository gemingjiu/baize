package com.baize.common.core.exception;

import com.baize.common.core.enums.BaizeException;

/**
 * 自动生成服务异常
 *
 * @author gemj
 * @since 2024/04/06 15:08
 */
public class GenException extends BaseException {
    private static final String module = "gen";

    public GenException(Integer code, String message, Object data, Throwable e) {
        super(code, message, data, e, module);
    }

    public GenException(BaizeException exceptionEnum, Object data) {
        super(exceptionEnum.getCode(), exceptionEnum.getMessage(), data, null, module);
    }
}
