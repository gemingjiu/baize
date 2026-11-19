package com.gem.baize.auth.controller;

import com.gem.baize.api.system.loginlog.client.SysLoginLogFeignClient;
import com.gem.baize.api.system.loginlog.domain.dto.SysLoginLogDto;
import com.gem.baize.auth.config.AuthCookieProperties;
import com.gem.baize.auth.domain.dto.SwitchTenantDTO;
import com.gem.baize.auth.manager.AuthCookieHelper;
import com.gem.baize.auth.service.AuthService;
import com.gem.baize.common.core.constant.CustomHttpHeaders;
import com.gem.baize.common.core.exception.model.UnauthorizedException;
import com.gem.baize.common.core.model.vo.ApiResult;
import com.gem.baize.common.security.domain.vo.IssuedRefresh;
import com.gem.baize.common.security.domain.dto.LoginDTO;
import com.gem.baize.common.security.domain.dto.Payload;
import com.gem.baize.common.security.domain.dto.TokenRefreshDTO;
import com.gem.baize.common.security.domain.vo.LoginVO;
import com.gem.baize.common.security.util.JwtUtils;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@Slf4j
@RestController
@RequestMapping("/auth")
public class AuthController {

    /**
     * Bearer 令牌前缀
     */
    private static final String BEARER_PREFIX = "Bearer ";

    /**
     * 登录日志 msg 字段上限，对应 sys_login_log.msg varchar(255)
     */
    private static final int LOG_MSG_MAX_LENGTH = 255;

    @Autowired
    private AuthService authService;

    @Autowired
    private SysLoginLogFeignClient sysLoginLogFeignClient;

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private com.gem.baize.auth.service.CaptchaService captchaService;

    /** T1.3：HttpOnly Cookie 模式相关组件（flag 关闭时这些方法不产生副作用）。 */
    @Autowired
    private AuthCookieProperties authCookieProperties;

    @Autowired
    private AuthCookieHelper authCookieHelper;

    /**
     * 生成登录图形验证码（2026-11-05 P5）。
     * <p>
     * 返回 {key, image(Base64 PNG)}；登录时随 captchaKey+captcha 提交。
     * 注意：该路径需加入网关白名单 baize.gateway.whitelist（匿名可访问），
     * 见 doc/DEFAULT_GROUP/baize-gateway-dev.yml。
     */
    @GetMapping("/captcha")
    @Operation(summary = "生成登录验证码", description = "返回 key 与 Base64 图片；TTL 120 秒，一次性消费")
    public ApiResult<java.util.Map<String, String>> captcha() {
        return ApiResult.ok(captchaService.generate());
    }

    /**
     * 验证码开关（登录页在渲染表单前探测是否需要验证码）。
     * 读参数 sys.account.captchaEnabled，读取失败一律回 false（不阻断登录页）。
     * 同样需要网关白名单放行。
     */
    @GetMapping("/captcha/enabled")
    @Operation(summary = "验证码开关", description = "登录页探测是否启用验证码")
    public ApiResult<Boolean> captchaEnabled() {
        return ApiResult.ok(captchaService.isEnabled());
    }

    @PostMapping("/login")
    @Operation(summary = "用户登录", description = "用户名密码登录")
    public ApiResult<LoginVO> login(@Valid @RequestBody LoginDTO dto, HttpServletRequest request,
                                    HttpServletResponse response) {
        LoginVO loginVO = null;
        String msg = "登录成功";
        String status = "0";
        try {
            loginVO = authService.login(dto);
            // T1.3：flag 开启时把 refresh 明文写进 HttpOnly Cookie，JSON 中剔除
            IssuedRefresh issued = loginVO.getIssuedRefresh();
            if (authCookieProperties.isEnabled() && issued != null) {
                authCookieHelper.writeCookies(response, issued);
                loginVO.setRefreshToken(null);
            }
            return ApiResult.ok(loginVO);
        } catch (Exception e) {
            msg = e.getMessage();
            status = "1";
            throw e;
        } finally {
            recordLoginLog(dto, request, status, msg, loginVO);
        }
    }

    @PostMapping("/logout")
    @Operation(summary = "用户登出", description = "退出登录，使当前访问令牌立即失效")
    public ApiResult<Void> logout(@RequestHeader(value = CustomHttpHeaders.AUTHORIZATION, required = false) String authorization,
                                  HttpServletRequest request, HttpServletResponse response) {
        // /auth/** 在网关是 skipAuth + 白名单，网关注入的 X-User-Id/X-Tenant-Id 头不会存在，
        // 因此这里改为直接从 Authorization 头解析令牌（原 @RequestHeader 必填会直接 400）。
        String token = parseBearerToken(authorization);
        Payload payload;
        try {
            payload = jwtUtils.parsePayload(token);
        } catch (Exception e) {
            log.warn("登出失败，令牌解析异常: {}", e.getMessage());
            throw new UnauthorizedException("令牌无效或已过期");
        }
        // T1.3：flag 开启时读取 refresh Cookie 校验 CSRF，并让 service 吊销 family
        String refresh = null;
        if (authCookieProperties.isEnabled()) {
            if (!authCookieHelper.validCsrf(request)) {
                throw new UnauthorizedException("CSRF 校验失败");
            }
            refresh = authCookieHelper.readCookie(request, authCookieProperties.getRefreshCookieName());
        }
        // 保留原有 authService.logout 调用，并额外把访问令牌加入黑名单实现立即失效
        authService.logout(payload.getUserId(), payload.getTenantId(), token, refresh);
        if (authCookieProperties.isEnabled()) {
            authCookieHelper.clearCookies(response);
        }
        return ApiResult.ok();
    }

    /**
     * 从 Authorization 请求头中提取 Bearer 令牌
     */
    private String parseBearerToken(String authorization) {
        if (StringUtils.isBlank(authorization)) {
            throw new UnauthorizedException("缺少Authorization请求头");
        }
        String token = authorization.trim();
        if (token.startsWith(BEARER_PREFIX)) {
            token = token.substring(BEARER_PREFIX.length()).trim();
        }
        if (StringUtils.isBlank(token)) {
            throw new UnauthorizedException("缺少Authorization请求头");
        }
        return token;
    }

    @PostMapping("/refresh")
    @Operation(summary = "刷新令牌", description = "使用刷新令牌获取新的访问令牌")
    public ApiResult<LoginVO> refresh(@Valid @RequestBody TokenRefreshDTO request,
                                      HttpServletRequest httpRequest, HttpServletResponse response) {
        // T1.3：flag 开启时 refresh 明文从 HttpOnly Cookie 读取，并做 CSRF 双重提交校验；
        // 其余身份字段（userId/tenantId/username）仍由前端在 body 中提交，服务端交叉校验防重放。
        if (authCookieProperties.isEnabled()) {
            if (!authCookieHelper.validCsrf(httpRequest)) {
                throw new UnauthorizedException("CSRF 校验失败");
            }
            String cookieRefresh = authCookieHelper.readCookie(
                    httpRequest, authCookieProperties.getRefreshCookieName());
            if (StringUtils.isBlank(cookieRefresh)) {
                throw new UnauthorizedException("刷新令牌无效或已过期");
            }
            request.setRefreshToken(cookieRefresh);
        }
        LoginVO loginVO = authService.refreshToken(request);
        IssuedRefresh issued = loginVO.getIssuedRefresh();
        if (authCookieProperties.isEnabled() && issued != null) {
            authCookieHelper.writeCookies(response, issued);
            loginVO.setRefreshToken(null);
        }
        return ApiResult.ok(loginVO);
    }

    /**
     * 切换租户：用当前（仍有效的）访问令牌换取目标租户下的新令牌。
     * <p>
     * /auth/** 在网关是 skipAuth + 白名单，网关不会注入 X-User-Id / X-Tenant-Id，
     * 因此这里同 logout 一样直接从 Authorization 头解析令牌，令牌本身就是唯一可信身份来源；
     * 目标租户能否切换由 {@link AuthService#switchTenant} 用「用户归属 / 平台超管」判定。
     */
    @PostMapping("/switchTenant")
    @Operation(summary = "切换租户", description = "用当前访问令牌换取目标租户下的新令牌，旧令牌立即失效")
    public ApiResult<LoginVO> switchTenant(
            @Valid @RequestBody SwitchTenantDTO request,
            @RequestHeader(value = CustomHttpHeaders.AUTHORIZATION, required = false) String authorization,
            HttpServletResponse response) {
        String token = parseBearerToken(authorization);
        Payload payload;
        try {
            payload = jwtUtils.parsePayload(token);
        } catch (Exception e) {
            log.warn("切换租户失败，令牌解析异常: {}", e.getMessage());
            throw new UnauthorizedException("令牌无效或已过期");
        }
        LoginVO loginVO = authService.switchTenant(payload, request.getTenantId(), token);
        IssuedRefresh issued = loginVO.getIssuedRefresh();
        if (authCookieProperties.isEnabled() && issued != null) {
            authCookieHelper.writeCookies(response, issued);
            loginVO.setRefreshToken(null);
        }
        return ApiResult.ok(loginVO);
    }

    private void recordLoginLog(LoginDTO dto, HttpServletRequest request, String status, String msg, LoginVO loginVO) {
        try {
            SysLoginLogDto logDto = new SysLoginLogDto();
            // 保护 tenant_id 不为空，默认平台租户（nil UUID）
            String tenantId = StringUtils.isNotBlank(dto.getTenant()) ? dto.getTenant() : "00000000-0000-0000-0000-000000000000";
            logDto.setTenantId(tenantId);
            logDto.setUserName(dto.getUsername());
            if (loginVO != null && loginVO.getUser() != null) {
                logDto.setUserId(loginVO.getUser().getId());
            }
            logDto.setIpaddr(getIpAddr(request));
            logDto.setStatus(status);
            logDto.setMsg(truncate(msg));
            logDto.setLoginTime(LocalDateTime.now());
            sysLoginLogFeignClient.record(logDto);
        } catch (Exception e) {
            log.warn("记录登录日志失败: {}", e.getMessage());
        }
    }

    /**
     * 截断登录日志消息，避免异常 message（如 Feign 异常携带的完整响应体）
     * 超过 sys_login_log.msg 的 varchar(255) 长度导致写入失败。
     */
    private String truncate(String msg) {
        if (msg == null) {
            return null;
        }
        return msg.length() <= LOG_MSG_MAX_LENGTH ? msg : msg.substring(0, LOG_MSG_MAX_LENGTH);
    }

    private String getIpAddr(HttpServletRequest request) {
        String ip = request.getHeader("X-Real-IP");
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("X-Forwarded-For");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_CLIENT_IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_X_FORWARDED_FOR");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        if (ip != null && ip.contains(",")) {
            ip = ip.split(",")[0].trim();
        }
        return ip;
    }
}
