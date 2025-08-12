package com.gem.baize.auth.controller;

import com.gem.baize.common.core.model.vo.Result;
import com.gem.baize.common.security.domain.dto.LoginDTO;
import com.gem.baize.common.security.domain.dto.TokenRefreshDTO;
import com.gem.baize.common.security.domain.vo.LoginVO;
import com.gem.baize.common.security.domain.vo.UserVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/auth")
public class AuthController {
    @PostMapping("/login")
    public Result<LoginVO> login(@RequestBody LoginDTO dto) {
        log.info("login request: {}", dto.toString());
        // 1. 验证租户

        // 2. 验证用户

        // 3. 生成令牌

        // 4. 保存刷新令牌到Redis (用于后续验证)

        // 5. 更新用户最后登录时间
        LoginVO loginVO = new LoginVO();
        BeanUtils.copyProperties(dto, loginVO);
        return Result.success(loginVO);
    }

    @PostMapping("/logout")
    public Result<Void> logout() {
        // 1. 从请求头获取令牌

        // 2. 验证并解析令牌

        // 3. 将访问令牌加入黑名单(剩余有效期内不可用)

        // 4. 删除关联的刷新令牌

        // 5. 清除其他会话数据(如有)

        return Result.success();
    }

    // 刷新JWT（简化示例，真实项目应校验刷新令牌）
    @PostMapping("/refresh")
    public Result<LoginVO> refresh(@RequestBody TokenRefreshDTO request) {
        // 1. 验证刷新令牌

        // 2. 检查令牌是否在有效存储中

        // 3. 获取用户信息

        // 4. 生成新访问令牌

        // 5. (可选)生成新刷新令牌并替换旧的

        LoginVO loginVO = new LoginVO();
        BeanUtils.copyProperties(request, loginVO);
        return Result.success(loginVO);
    }

    // 返回当前用户信息（简化示例）
    @GetMapping("/userinfo")
    public Result<UserVO> userinfo(Authentication authentication) {
        if (authentication == null) {
            return Result.error(HttpStatus.UNAUTHORIZED, "未认证");
        }
        return Result.success(new UserVO());
    }

    // OAuth2登录成功回调
    @GetMapping("/oauth2/success")
    public Result<LoginVO> oauth2Success(OAuth2AuthenticationToken authentication) {
        // 1. 验证租户

        // 2. 验证用户

        // 3. 生成令牌

        // 4. 保存刷新令牌到Redis (用于后续验证)

        // 5. 更新用户最后登录时间
        LoginVO loginVO = new LoginVO();
        return Result.success(loginVO);
    }

    // OAuth2登录失败回调
    @GetMapping("/oauth2/failure")
    public Result<LoginVO> oauth2Failure() {
        return Result.error(HttpStatus.UNAUTHORIZED, "OAuth2 登录失败");
    }
}
