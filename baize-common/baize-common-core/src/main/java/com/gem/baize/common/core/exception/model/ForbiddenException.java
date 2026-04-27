package com.gem.baize.common.core.exception.model;

import com.gem.baize.common.core.enums.ErrorCode;

/**
 * 禁止访问异常
 */
public class ForbiddenException extends BaseException {

    public ForbiddenException(String message) {
        super(ErrorCode.FORBIDDEN, message);
    }

    public ForbiddenException(ErrorCode code, String message) {
        super(code, message);
    }
}
