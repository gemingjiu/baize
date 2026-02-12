package com.gem.baize.common.core.exception.model;

import com.gem.baize.common.core.enums.ErrorCode;

/**
 * 资源未找到异常
 */
public class NotFoundException extends BaseException {
    public NotFoundException(String message) {
        super(ErrorCode.DATA_NOT_EXISTS, message);
    }
}

