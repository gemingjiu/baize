package com.baize.auth.controller;


import com.baize.auth.entity.User;
import com.baize.auth.service.SysLoginService;
import com.baize.common.core.domain.Response;
import com.baize.common.core.utils.jwt.JwtUtils;
import com.baize.common.core.utils.text.StringUtils;
import com.baize.common.security.service.AuthService;
import com.baize.common.security.service.TokenService;
import com.baize.common.security.utils.SecurityUtils;
import com.baize.system.api.domain.vo.LoginUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

/**
 * Token接口
 *
 * @author gemj
 * @since 2023/08/22 11:11
 */
@RestController
public class TokenController {

    @Autowired
    private SysLoginService sysLoginService;
    @Autowired
    private TokenService tokenService;

    @Autowired(required = false)
    private AuthService authService;


    @PostMapping("login")
    public Response<?> login(@RequestBody User user) {
        // 用户登录
        LoginUser userInfo = sysLoginService.login(user.getUsername(), user.getPassword());
        // 获取登录token
        return Response.success(tokenService.createToken(userInfo));
    }

    @DeleteMapping("logout")
    public Response<?> logout(HttpServletRequest request) {
        String token = SecurityUtils.getToken(request);
        if (StringUtils.isNotEmpty(token)) {
            String username = JwtUtils.getUserName(token);
            // 删除用户缓存记录
            authService.logoutByToken(token);
            // 记录用户退出日志
            sysLoginService.logout(username);
        }
        return Response.success();
    }
}
