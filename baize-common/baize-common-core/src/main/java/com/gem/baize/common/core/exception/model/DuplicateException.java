package com.gem.baize.common.core.exception.model;

import com.gem.baize.common.core.enums.ErrorCode;

/**
 * 重复数据错误
 */
public class DuplicateException extends BaseException {
    public DuplicateException(String message) {
        super(ErrorCode.DATA_ALREADY_EXISTS, message);
    }
}
