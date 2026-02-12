package com.gem.baize.common.core.exception.model;

import com.gem.baize.common.core.enums.ErrorCode;

public class BusinessException  extends  BaseException{
    public BusinessException(ErrorCode errorCode) {
        super(errorCode);
    }
    public BusinessException(ErrorCode errorCode, String message) {
        super(errorCode, message);
    }
}
