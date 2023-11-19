package com.baize.gateway.service.impl;


import com.baize.common.cache.service.CacheService;
import com.baize.common.core.constant.CacheConstants;
import com.baize.common.core.constant.Constants;
import com.baize.common.core.domain.AjaxResult;
import com.baize.common.core.exception.BaseException;
import com.baize.common.core.utils.algorithm.Base64;
import com.baize.common.core.utils.text.StringUtils;
import com.baize.common.core.utils.uuid.UUIDUtils;
import com.baize.gateway.config.CaptchaConfig;
import com.baize.gateway.service.CaptchaCodeService;
import com.google.code.kaptcha.Producer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.FastByteArrayOutputStream;

import javax.annotation.Resource;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

import static com.baize.common.core.enums.BaizeExceptionEnum.CAPTCHA_EXCEPTION;

@Service
public class CaptchaCodeServiceImpl implements CaptchaCodeService {

    @Resource(name = "defaultCaptchaProducer")
    private Producer defaultCaptchaProducer;

    @Resource(name = "mathCaptchaProducer")
    private Producer mathCaptchaProducer;

    @Autowired
    private CacheService cacheService;


    @Autowired
    private CaptchaConfig captchaConfig;

    @Override
    public AjaxResult createCaptcha() throws BaseException {

        AjaxResult ajax = AjaxResult.success();
        boolean captchaEnabled = captchaConfig.getEnabled();
        ajax.put("captchaEnabled", captchaEnabled);
        if (!captchaEnabled) {
            return ajax;
        }

        // 保存验证码信息
        String uuid = UUIDUtils.simpleUUID();
        String verifyKey = CacheConstants.CAPTCHA_CODE_KEY + uuid;

        String capStr = null, code = null;
        BufferedImage image = null;

        String captchaType = captchaConfig.getType();
        // 生成验证码
        if ("math".equals(captchaType)) {
            String capText = mathCaptchaProducer.createText();
            capStr = capText.substring(0, capText.lastIndexOf("@"));
            code = capText.substring(capText.lastIndexOf("@") + 1);
            image = mathCaptchaProducer.createImage(capStr);
        } else if ("char".equals(captchaType)) {
            capStr = code = defaultCaptchaProducer.createText();
            image = defaultCaptchaProducer.createImage(capStr);
        }

        cacheService.setCacheObject(verifyKey, code, Constants.CAPTCHA_EXPIRATION, TimeUnit.MINUTES);
        // 转换流信息写出
        FastByteArrayOutputStream os = new FastByteArrayOutputStream();
        try {
            ImageIO.write(image, "jpg", os);
        } catch (IOException e) {
            return AjaxResult.error(e.getMessage());
        }

        ajax.put("uuid", uuid);
        ajax.put("img", Base64.encode(os.toByteArray()));
        return ajax;
    }

    @Override
    public void checkCaptcha(String code, String uuid) throws BaseException {
        if (StringUtils.isEmpty(code)) {
            throw new BaseException(CAPTCHA_EXCEPTION, "验证码不能为空");
        }
        if (StringUtils.isEmpty(uuid)) {
            throw new BaseException(CAPTCHA_EXCEPTION, "验证码已失效");
        }
        String verifyKey = CacheConstants.CAPTCHA_CODE_KEY + uuid;
        String captcha = cacheService.getCacheObject(verifyKey);
        cacheService.deleteObject(verifyKey);

        if (!code.equalsIgnoreCase(captcha)) {
            throw new BaseException(CAPTCHA_EXCEPTION, "验证码错误");
        }
    }
}
