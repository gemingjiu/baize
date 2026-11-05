package com.gem.baize.auth.service.impl;

import com.gem.baize.api.system.config.client.SysConfigFeignClient;
import com.gem.baize.auth.service.CaptchaService;
import com.gem.baize.common.core.model.vo.ApiResult;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.time.Duration;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

/**
 * 登录图形验证码实现（2026-11-05 P5）。
 * <p>
 * 零第三方依赖：java.awt 手绘 4 位字符 + 干扰线 → Base64 PNG；
 * Redis 存会话（key=captcha:{uuid}，120s TTL，validate 一次性删除防重放）。
 * 开关读取失败/超时视为关闭（fail-open），不阻断登录主流程——验证码是增强手段。
 */
@Slf4j
@Service
public class CaptchaServiceImpl implements CaptchaService {

    /** 验证码参数键（P2 种子默认 false） */
    static final String CAPTCHA_ENABLED_KEY = "sys.account.captchaEnabled";

    private static final String CAPTCHA_KEY_PREFIX = "captcha:";
    private static final Duration CAPTCHA_TTL = Duration.ofSeconds(120);

    /** 去易混淆字符集（无 0/O/1/I/l） */
    private static final String CODE_CHARS = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789";
    private static final int CODE_LENGTH = 4;

    private static final int IMG_WIDTH = 120;
    private static final int IMG_HEIGHT = 40;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    private SysConfigFeignClient sysConfigFeignClient;

    @Override
    public Map<String, String> generate() {
        String code = RandomStringUtils.random(CODE_LENGTH, CODE_CHARS);
        String key = UUID.randomUUID().toString().replace("-", "");
        stringRedisTemplate.opsForValue().set(CAPTCHA_KEY_PREFIX + key, code, CAPTCHA_TTL);

        Map<String, String> result = new HashMap<>(2);
        result.put("key", key);
        result.put("image", renderPng(code));
        return result;
    }

    @Override
    public boolean validate(String key, String code) {
        if (StringUtils.isBlank(key) || StringUtils.isBlank(code)) {
            return false;
        }
        String redisKey = CAPTCHA_KEY_PREFIX + key;
        String stored = stringRedisTemplate.opsForValue().get(redisKey);
        // 一次性消费：无论对错都删除，防止同一验证码反复尝试
        stringRedisTemplate.delete(redisKey);
        return stored != null && stored.equalsIgnoreCase(code.trim());
    }

    @Override
    public boolean isEnabled() {
        try {
            ApiResult<String> result = sysConfigFeignClient.getConfigValueByKey(CAPTCHA_ENABLED_KEY);
            return result != null && result.isSuccess()
                    && Boolean.parseBoolean(StringUtils.trimToEmpty(result.getData()));
        } catch (Exception e) {
            log.warn("读取验证码开关失败（视为关闭）: {}", e.getMessage());
            return false;
        }
    }

    /**
     * java.awt 手绘验证码图片（headless 安全），返回 data URL 形式的 Base64 PNG。
     */
    private String renderPng(String code) {
        try {
            BufferedImage image = new BufferedImage(IMG_WIDTH, IMG_HEIGHT, BufferedImage.TYPE_INT_RGB);
            Graphics2D g = image.createGraphics();
            ThreadLocalRandom random = ThreadLocalRandom.current();
            try {
                g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                // 背景
                g.setColor(new Color(245, 247, 250));
                g.fillRect(0, 0, IMG_WIDTH, IMG_HEIGHT);
                // 干扰线
                g.setStroke(new BasicStroke(1.2f));
                for (int i = 0; i < 6; i++) {
                    g.setColor(new Color(120 + random.nextInt(100), 120 + random.nextInt(100), 120 + random.nextInt(100)));
                    g.drawLine(random.nextInt(IMG_WIDTH), random.nextInt(IMG_HEIGHT),
                            random.nextInt(IMG_WIDTH), random.nextInt(IMG_HEIGHT));
                }
                // 字符（逐个微旋转）
                Font font = new Font("SansSerif", Font.BOLD, 26);
                g.setFont(font);
                int x = 10;
                for (char c : code.toCharArray()) {
                    g.setColor(new Color(30 + random.nextInt(80), 30 + random.nextInt(80), 30 + random.nextInt(80)));
                    double theta = (random.nextInt(60) - 30) * Math.PI / 180.0;
                    int y = 28 + random.nextInt(6) - 3;
                    g.rotate(theta, x + 8, y - 12);
                    g.drawString(String.valueOf(c), x, y);
                    g.rotate(-theta, x + 8, y - 12);
                    x += 26;
                }
                // 噪点
                for (int i = 0; i < 40; i++) {
                    g.setColor(new Color(100 + random.nextInt(120), 100 + random.nextInt(120), 100 + random.nextInt(120)));
                    g.fillRect(random.nextInt(IMG_WIDTH), random.nextInt(IMG_HEIGHT), 1, 1);
                }
            } finally {
                g.dispose();
            }
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            ImageIO.write(image, "png", out);
            return "data:image/png;base64," + Base64.getEncoder().encodeToString(out.toByteArray());
        } catch (Exception e) {
            log.error("验证码图片渲染失败", e);
            return "";
        }
    }
}