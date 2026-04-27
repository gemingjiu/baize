package com.gem.baize.auth.controller;

import com.gem.baize.auth.service.AuthService;
import com.gem.baize.common.core.model.vo.ApiResult;
import com.gem.baize.common.security.domain.dto.LoginDTO;
import com.gem.baize.common.security.domain.dto.TokenRefreshDTO;
import com.gem.baize.common.security.domain.vo.LoginVO;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @PostMapping("/login")
    @Operation(summary = "用户登录", description = "用户名密码登录")
    public ApiResult<LoginVO> login(@Valid @RequestBody LoginDTO dto) {
        LoginVO loginVO = authService.login(dto);
        return ApiResult.ok(loginVO);
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
}
