package com.gem.baize.auth.service;

import java.util.Map;

/**
 * 登录图形验证码服务（2026-11-05 P5）。
 * <p>
 * 生成 4 位图形验证码（Base64 PNG）并以 Redis 会话存储（120s TTL、一次性消费）。
 * 是否启用由参数 sys.account.captchaEnabled 控制（P2 参数消费）。
 */
public interface CaptchaService {

    /**
     * 生成验证码。
     *
     * @return {key: 会话标识（提交登录时回传 captchaKey）, image: Base64 PNG}
     */
    Map<String, String> generate();

    /**
     * 校验验证码（大小写不敏感；一次性消费，校验后立即删除会话）。
     *
     * @param key  会话标识
     * @param code 用户输入的验证码
     * @return true=正确
     */
    boolean validate(String key, String code);

    /**
     * 验证码开关是否开启（读参数 sys.account.captchaEnabled，'true' 才启用）。
     * 参数缺失/关闭/读取失败一律视为关闭——开关故障不能阻断登录主流程。
     */
    boolean isEnabled();
}