package com.gem.baize.common.core.exception.model;

import com.gem.baize.common.core.enums.ErrorCode;

/**
 * 参数异常
 */
public class ParamException extends BaseException {
    public ParamException(String message) {
        super(ErrorCode.INVALID_PARAM, message);
    }
}


