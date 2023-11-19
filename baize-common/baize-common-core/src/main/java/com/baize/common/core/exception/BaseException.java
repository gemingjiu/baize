package com.baize.common.core.exception;

import com.baize.common.core.enums.BaizeExceptionEnum;
import com.baize.common.core.utils.text.StringUtils;

import java.util.Arrays;

public class BaseException extends RuntimeException {
    //  返回码
    private Integer code;
    // 返回消息
    private String msg;

    public BaseException(BaizeExceptionEnum baiZeExceptionEnum, String msg) {
        super(msg);
        this.code = baiZeExceptionEnum.getCode();
        appendMsg(baiZeExceptionEnum.getMsg(), msg);
    }

    public BaseException(BaizeExceptionEnum baiZeExceptionEnum, String... msg) {
        super(Arrays.toString(msg));
        this.code = baiZeExceptionEnum.getCode();
        appendMsg(baiZeExceptionEnum.getMsg());
        appendMsg(msg);
    }

    public BaseException(BaizeExceptionEnum baiZeExceptionEnum, Exception e) {
        super(baiZeExceptionEnum.getMsg(), e);
        this.code = baiZeExceptionEnum.getCode();
        appendMsg(baiZeExceptionEnum.getMsg(), e.getMessage());
    }

    private void appendMsg(String... msgList) {
        StringBuilder sb = new StringBuilder();
        if(StringUtils.isNotBlank(this.msg)){
            sb.append(this.msg);
        }
        for (String msg : msgList) {
            sb.append(msg);
        }
        this.msg = sb.toString();
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
