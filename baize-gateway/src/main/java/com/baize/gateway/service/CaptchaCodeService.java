package com.baize.gateway.service;

import com.baize.common.core.domain.AjaxResult;
import com.baize.common.core.exception.BaseException;

import java.io.IOException;

public interface CaptchaCodeService {
    /**
     * 生成验证码
     */
    public AjaxResult createCaptcha() throws IOException, BaseException;

    /**
     * 校验验证码
     */
    public void checkCaptcha(String key, String value) throws BaseException;
}
