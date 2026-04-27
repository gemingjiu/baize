package com.gem.baize.auth.service.impl;

import com.gem.baize.api.system.tenant.client.SysTenantFeignClient;
import com.gem.baize.api.system.tenant.domain.dto.SysTenantDto;
import com.gem.baize.api.system.user.client.SysUserFeignClient;
import com.gem.baize.api.system.user.domain.dto.SysUserDto;
import com.gem.baize.auth.manager.TokenManager;
import com.gem.baize.auth.service.AuthService;
import com.gem.baize.common.core.constant.CustomHttpHeaders;
import com.gem.baize.common.core.exception.model.NotFoundException;
import com.gem.baize.common.core.model.vo.ApiResult;
import com.gem.baize.common.security.domain.dto.LoginDTO;
import com.gem.baize.common.security.domain.dto.Payload;
import com.gem.baize.common.security.domain.dto.TokenRefreshDTO;
import com.gem.baize.common.security.domain.vo.LoginVO;
import com.gem.baize.common.security.domain.vo.UserVO;
import com.gem.baize.common.security.util.JwtUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * 认证服务实现
 */
@Slf4j
@Service
public class AuthServiceImpl implements AuthService {

    @Autowired
    private SysUserFeignClient sysUserFeignClient;

    @Autowired
    private SysTenantFeignClient sysTenantFeignClient;

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private TokenManager tokenManager;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Value("${jwt.expiration:7200000}")
    private long jwtExpirationMs;

    @Override
    public LoginVO login(LoginDTO dto) {
        log.info("用户登录请求: username={}", dto.getUsername());

        // 按用户名查询用户
        ApiResult<SysUserDto> userResult = sysUserFeignClient.getByUsername(dto.getUsername());
        if (!userResult.isSuccess() || userResult.getData() == null) {
            throw new NotFoundException("用户名或密码错误");
        }
        SysUserDto user = userResult.getData();

        // 3. 校验密码
        if (StringUtils.isBlank(user.getPassword()) || !passwordEncoder.matches(dto.getPassword(), user.getPassword())) {
            throw new NotFoundException("用户名或密码错误");
        }

        // 4. 构建JWT payload
        Payload payload = new Payload();
        payload.setUserId(user.getId());
        payload.setUserName(user.getUserName());
        payload.setTenantId(user.getTenantId());
        payload.setSubject(user.getId());

        // 4. 生成访问令牌
        Map<String, Object> claims = buildClaims(payload);
        String accessToken = jwtUtils.createToken(user.getId(), claims);

        // 5. 生成刷新令牌并保存到Redis
        String refreshToken = UUID.randomUUID().toString().replace("-", "");
        tokenManager.saveRefreshToken(user.getTenantId(), user.getId(), refreshToken);

        // 6. 更新用户最后登录时间
        sysUserFeignClient.updateLastLoginTime(user.getId(), null);

        // 7. 构建返回结果
        LoginVO loginVO = new LoginVO();
        loginVO.setAccessToken(accessToken);
        loginVO.setRefreshToken(refreshToken);
        loginVO.setExpiresIn((int) (jwtExpirationMs / 1000));

        // 8. 构建用户信息
        UserVO userVO = new UserVO();
        userVO.setId(user.getId());
        userVO.setUsername(user.getUserName());
        userVO.setNickname(user.getNickName());
        userVO.setEmail(user.getEmail());
        userVO.setPhone(user.getPhone());
        userVO.setAvatar(user.getAvatar());
        userVO.setTenantId(user.getTenantId());
        loginVO.setUser(userVO);

        return loginVO;
    }

    @Override
    public void logout(String userId, String tenantId) {
        tokenManager.deleteRefreshToken(tenantId, userId);
        log.info("用户登出: userId={}, tenantId={}", userId, tenantId);
    }

    @Override
    public LoginVO refreshToken(TokenRefreshDTO dto) {
        log.info("刷新令牌请求: userId={}", dto.getUserId());

        // 1. 验证刷新令牌
        boolean valid = tokenManager.validateRefreshToken(dto.getTenantId(), dto.getUserId(), dto.getRefreshToken());
        if (!valid) {
            throw new NotFoundException("刷新令牌无效或已过期");
        }

        // 2. 查询用户信息
        ApiResult<SysUserDto> userResult = sysUserFeignClient.getByUsernameAndTenantId(dto.getUsername(), dto.getTenantId());
        if (!userResult.isSuccess() || userResult.getData() == null) {
            throw new NotFoundException("用户不存在");
        }
        SysUserDto user = userResult.getData();

        // 3. 构建新的访问令牌
        Payload payload = new Payload();
        payload.setUserId(user.getId());
        payload.setUserName(user.getUserName());
        payload.setTenantId(user.getTenantId());
        payload.setSubject(user.getId());

        Map<String, Object> claims = buildClaims(payload);
        String accessToken = jwtUtils.createToken(user.getId(), claims);

        // 4. 生成新的刷新令牌（可选：保持原有刷新令牌）
        String refreshToken = UUID.randomUUID().toString().replace("-", "");
        tokenManager.saveRefreshToken(user.getTenantId(), user.getId(), refreshToken);

        // 5. 构建返回结果
        LoginVO loginVO = new LoginVO();
        loginVO.setAccessToken(accessToken);
        loginVO.setRefreshToken(refreshToken);
        loginVO.setExpiresIn((int) (jwtExpirationMs / 1000));

        return loginVO;
    }

    private Map<String, Object> buildClaims(Payload payload) {
        Map<String, Object> claims = new HashMap<>();
        claims.put(CustomHttpHeaders.USER_ID, payload.getUserId());
        claims.put(CustomHttpHeaders.USER_NAME, payload.getUserName());
        claims.put(CustomHttpHeaders.TENANT_ID, payload.getTenantId());
        claims.put(CustomHttpHeaders.ROLE, payload.getRole());
        return claims;
    }
}
