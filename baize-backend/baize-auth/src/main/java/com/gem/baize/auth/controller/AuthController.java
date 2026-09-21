package com.gem.baize.auth.controller;

import com.gem.baize.api.system.loginlog.client.SysLoginLogFeignClient;
import com.gem.baize.api.system.loginlog.domain.dto.SysLoginLogDto;
import com.gem.baize.auth.service.AuthService;
import com.gem.baize.common.core.model.vo.ApiResult;
import com.gem.baize.common.security.domain.dto.LoginDTO;
import com.gem.baize.common.security.domain.dto.TokenRefreshDTO;
import com.gem.baize.common.security.domain.vo.LoginVO;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@Slf4j
@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @Autowired
    private SysLoginLogFeignClient sysLoginLogFeignClient;

    @PostMapping("/login")
    @Operation(summary = "用户登录", description = "用户名密码登录")
    public ApiResult<LoginVO> login(@Valid @RequestBody LoginDTO dto, HttpServletRequest request) {
        LoginVO loginVO = null;
        String msg = "登录成功";
        String status = "0";
        try {
            loginVO = authService.login(dto);
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
    @Operation(summary = "用户登出", description = "退出登录，清除令牌")
    public ApiResult<Void> logout(@RequestHeader("X-User-Id") String userId,
                                  @RequestHeader("X-Tenant-Id") String tenantId) {
        authService.logout(userId, tenantId);
        return ApiResult.ok();
    }

    @PostMapping("/refresh")
    @Operation(summary = "刷新令牌", description = "使用刷新令牌获取新的访问令牌")
    public ApiResult<LoginVO> refresh(@Valid @RequestBody TokenRefreshDTO request) {
        LoginVO loginVO = authService.refreshToken(request);
        return ApiResult.ok(loginVO);
    }

    private void recordLoginLog(LoginDTO dto, HttpServletRequest request, String status, String msg, LoginVO loginVO) {
        try {
            SysLoginLogDto logDto = new SysLoginLogDto();
            logDto.setTenantId(dto.getTenant());
            logDto.setUserName(dto.getUsername());
            if (loginVO != null && loginVO.getUser() != null) {
                logDto.setUserId(loginVO.getUser().getId());
            }
            logDto.setIpaddr(getIpAddr(request));
            logDto.setStatus(status);
            logDto.setMsg(msg);
            logDto.setLoginTime(LocalDateTime.now());
            sysLoginLogFeignClient.record(logDto);
        } catch (Exception e) {
            log.warn("记录登录日志失败: {}", e.getMessage());
        }
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
