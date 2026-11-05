package com.gem.baize.api.system.config.client;

import com.gem.baize.common.core.model.vo.ApiResult;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * 参数配置 Feign 客户端（2026-11-05 P2：参数消费机制）。
 * <p>
 * 对齐 {@code SysTenantFeignClient} 的装配方式；inner 接口由 InnerAuthFeignConfig
 * 携带 X-Inner-Token 通过校验。
 */
@FeignClient(path = "/api/v1/system", name = "baize-system", url = "${baize.system.url:http://localhost:8020}", contextId = "SysConfigFeignClient")
public interface SysConfigFeignClient {

    /**
     * 按键读取参数值（当前租户自定义 → 平台兜底；查不到返回 null）。
     * 典型消费方：baize-auth 读 sys.account.captchaEnabled 验证码开关。
     */
    @GetMapping("/config/inner/value")
    ApiResult<String> getConfigValueByKey(@RequestParam("key") String configKey);
}