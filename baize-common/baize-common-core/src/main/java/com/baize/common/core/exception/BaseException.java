package com.baize.common.core.exception;

import com.baize.common.core.enums.BaizeException;

public class BaseException extends RuntimeException {
    // 模块
    protected String module;
    // 错误码
    protected Integer code;
    // 错误信息
    protected Object data;

    public BaseException(Integer code, String message, Object data, Throwable e, String module) {
        super(message, e);
        this.code = code;
        this.data = data;
        this.module = module;
    }

    public BaseException(Integer code, String message, Object data) {
        super(message, null);
        this.code = code;
        this.data = data;
    }

    public BaseException(BaizeException exceptionEnum, Object data) {
        super(exceptionEnum.getMessage(), null);
        this.code = exceptionEnum.getCode();
        this.data = data;
    }

    public BaseException(BaizeException exceptionEnum, Object data, Throwable e) {
        super(exceptionEnum.getMessage(), e);
        this.code = exceptionEnum.getCode();
        this.data = data;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public String getModule() {
        return module;
    }

    public void setModule(String module) {
        this.module = module;
    }
}
