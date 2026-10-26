package com.gem.baize.auth.service.impl;

import com.gem.baize.api.system.menu.client.SysMenuFeignClient;
import com.gem.baize.api.system.role.client.SysRoleFeignClient;
import com.gem.baize.api.system.role.domain.dto.SysRoleDto;
import com.gem.baize.api.system.tenant.client.SysTenantFeignClient;
import com.gem.baize.api.system.user.client.SysUserFeignClient;
import com.gem.baize.api.system.user.domain.dto.SysUserDto;
import com.gem.baize.auth.manager.TokenManager;
import com.gem.baize.auth.service.AuthService;
import com.gem.baize.common.cache.service.TokenBlacklistService;
import com.gem.baize.common.core.constant.CustomHttpHeaders;
import com.gem.baize.common.core.exception.model.ForbiddenException;
import com.gem.baize.common.core.exception.model.NotFoundException;
import com.gem.baize.common.core.exception.model.ParamException;
import com.gem.baize.common.core.exception.model.UnauthorizedException;
import com.gem.baize.common.core.model.vo.ApiResult;
import com.gem.baize.common.core.service.OnlineUserService;
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
import java.util.List;
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
    private SysRoleFeignClient sysRoleFeignClient;

    @Autowired
    private SysMenuFeignClient sysMenuFeignClient;

    @Autowired
    private SysTenantFeignClient sysTenantFeignClient;

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private TokenManager tokenManager;

    @Autowired
    private OnlineUserService onlineUserService;

    @Autowired
    private TokenBlacklistService tokenBlacklistService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Value("${jwt.expiration:7200000}")
    private long jwtExpirationMs;

    /**
     * 超级管理员的角色编码（逗号分隔，可配置）。
     * <p>
     * 权限校验约定：令牌 role claim 存放逗号分隔的权限码，{@code *:*:*} 表示全部权限。
     * 目前系统还没有「按用户查询权限码」的接口（sys_perm/sys_role_perm 也尚无初始化数据），
     * 若直接写角色编码，PermissionAspect 的精确匹配会把超管一起拒掉，
     * 因此这里把命中本列表的角色编码统一签发为通配权限码，保持超管的既有可用性；
     * 普通角色仍写角色编码，与改造前行为一致（改造前这些角色同样无法通过前缀匹配拿到权限）。
     */
    @Value("${baize.security.super-admin-role-codes:admin}")
    private String superAdminRoleCodes;

    /**
     * 超级管理员通配权限码，需与 PermissionAspect.SUPER_ADMIN_PERMISSION 保持一致
     */
    private static final String SUPER_ADMIN_PERMISSION = "*:*:*";

    /**
     * 平台（保留）租户ID，与 CustomTenantHandler.PLATFORM_TENANT_ID、网关 TenantFilter.DEFAULT_TENANT_ID 一致。
     */
    private static final String PLATFORM_TENANT_ID = "0";

    @Override
    public LoginVO login(LoginDTO dto) {
        log.info("用户登录请求: username={}, tenant={}", dto.getUsername(), dto.getTenant());
        // 按用户名和租户查询用户
        ApiResult<SysUserDto> userResult;
        if (StringUtils.isNotBlank(dto.getTenant())) {
            userResult = sysUserFeignClient.getByUsernameAndTenantId(dto.getUsername(), dto.getTenant());
        } else {
            userResult = sysUserFeignClient.getByUsername(dto.getUsername());
        }
        if (!userResult.isSuccess() || userResult.getData() == null) {
            throw new NotFoundException("用户名或密码错误");
        }
        SysUserDto user = userResult.getData();

        // 3. 校验密码
        // 注意：SysUserDto.password 标注了 @JsonProperty(access = WRITE_ONLY)，
        // 系统服务返回 JSON 时该字段被丢弃，Feign 侧 password 恒为 null，
        // 因此必须通过内网接口单独获取密码哈希。
        String encodedPassword = null;
        try {
            ApiResult<String> passwordResult = sysUserFeignClient.getPasswordByUsername(dto.getUsername());
            if (passwordResult != null && passwordResult.isSuccess()) {
                encodedPassword = passwordResult.getData();
            }
        } catch (Exception e) {
            log.error("获取用户密码哈希失败, username={}", dto.getUsername(), e);
        }
        if (StringUtils.isBlank(encodedPassword) || !passwordEncoder.matches(dto.getPassword(), encodedPassword)) {
            throw new NotFoundException("用户名或密码错误");
        }

        // 签发令牌（权限码按用户当前角色重新聚合，保证权限变更能立即生效）
        LoginVO loginVO = issueTokenForUser(user, user.getTenantId());

        // 更新用户最后登录时间
        sysUserFeignClient.updateLastLoginTime(user.getId(), null);

        // 记录在线用户
        onlineUserService.login(user.getId(), user.getTenantId(), user.getUserName());

        return loginVO;
    }

    @Override
    public LoginVO switchTenant(Payload current, String targetTenant, String accessToken) {
        if (current == null || StringUtils.isBlank(current.getUserId())) {
            throw new UnauthorizedException("令牌无效，请重新登录");
        }
        if (StringUtils.isBlank(targetTenant) || !StringUtils.isNumeric(targetTenant)) {
            throw new ParamException("目标租户ID必须为数字");
        }
        String userName = current.getUserName();
        if (StringUtils.isBlank(userName)) {
            throw new UnauthorizedException("令牌缺少用户信息，请重新登录");
        }

        // 1. 目标租户必须存在（内部接口查询，避免把令牌签到一个不存在的租户上）
        boolean tenantExists = false;
        try {
            ApiResult<Boolean> existsResult = sysTenantFeignClient.existsById(targetTenant);
            tenantExists = existsResult != null && existsResult.isSuccess()
                    && Boolean.TRUE.equals(existsResult.getData());
        } catch (Exception e) {
            log.warn("校验目标租户失败, tenantId={}, error={}", targetTenant, e.getMessage());
        }
        if (!tenantExists) {
            throw new NotFoundException("目标租户不存在");
        }

        // 2. 归属判定：优先使用「用户在目标租户下的账号」，其次是平台超管的跨租户巡视
        ApiResult<SysUserDto> memberResult;
        SysUserDto targetUser = null;
        try {
            memberResult = sysUserFeignClient.getByUsernameAndTenantId(userName, targetTenant);
            if (memberResult != null && memberResult.isSuccess()) {
                targetUser = memberResult.getData();
            }
        } catch (Exception e) {
            log.warn("查询用户在目标租户下的账号失败, userName={}, tenantId={}, error={}",
                    userName, targetTenant, e.getMessage());
        }

        if (targetUser == null) {
            // 非超管不允许跨租户：否则任何用户都能把令牌的 X-Tenant-Id 改成别的租户，
            // 进而借 CustomTenantHandler 的行级隔离读到他人数据。
            if (!isSuperAdmin(current.getRole())) {
                throw new ForbiddenException("当前用户不属于目标租户，无法切换");
            }
            log.info("平台超管跨租户切换: userId={}, from={}, to={}",
                    current.getUserId(), current.getTenantId(), targetTenant);
            ApiResult<SysUserDto> sourceResult = sysUserFeignClient
                    .getByUsernameAndTenantId(userName, StringUtils.isNotBlank(current.getTenantId())
                            ? current.getTenantId() : PLATFORM_TENANT_ID);
            if (!sourceResult.isSuccess() || sourceResult.getData() == null) {
                throw new NotFoundException("用户不存在");
            }
            targetUser = sourceResult.getData();
        }

        // 3. 按目标租户重新签发令牌（权限码随之按目标租户重新聚合）
        LoginVO loginVO = issueTokenForUser(targetUser, targetTenant);

        // 4. 旧令牌立即失效，避免同一浏览器里两个租户的令牌并存
        blacklistAccessToken(accessToken, current.getUserId());

        log.info("切换租户成功: userId={}, from={}, to={}",
                current.getUserId(), current.getTenantId(), targetTenant);
        return loginVO;
    }

    /**
     * 为用户在指定租户下签发访问令牌与刷新令牌。
     * <p>
     * tenantId 显式传入（而不是取 user.getTenantId()），使得「平台超管切换租户」这类
     * 账号行仍在平台租户、但令牌归属目标租户的场景也能复用同一套签发逻辑。
     *
     * @param user     用户（已确认归属）
     * @param tenantId 令牌归属租户
     * @return 令牌与用户简档
     */
    private LoginVO issueTokenForUser(SysUserDto user, String tenantId) {
        Payload payload = new Payload();
        payload.setUserId(user.getId());
        payload.setUserName(user.getUserName());
        payload.setTenantId(tenantId);
        payload.setSubject(user.getId());

        // 获取用户角色及数据权限
        try {
            ApiResult<SysRoleDto> roleResult = sysRoleFeignClient.getRoleByUserId(user.getId());
            if (roleResult != null && roleResult.isSuccess() && roleResult.getData() != null) {
                SysRoleDto roleDto = roleResult.getData();
                // PermissionAspect 约定 role claim 存放逗号分隔的权限码（*:*:* 为超管），
                // 这里用「用户 → 角色 → 角色菜单 → 菜单 perms」聚合实际权限码。
                payload.setRole(resolvePermissionClaim(roleDto.getRoleCode(), user.getId()));
                payload.setDataScope(roleDto.getDataScope());
            }
        } catch (Exception e) {
            log.warn("获取用户角色信息失败, userId={}", user.getId(), e);
        }

        String accessToken = jwtUtils.createToken(user.getId(), buildClaims(payload));
        String refreshToken = UUID.randomUUID().toString().replace("-", "");
        tokenManager.saveRefreshToken(tenantId, user.getId(), refreshToken);

        LoginVO loginVO = new LoginVO();
        loginVO.setAccessToken(accessToken);
        loginVO.setRefreshToken(refreshToken);
        loginVO.setExpiresIn((int) (jwtExpirationMs / 1000));

        UserVO userVO = new UserVO();
        userVO.setId(user.getId());
        userVO.setUsername(user.getUserName());
        userVO.setNickname(user.getNickName());
        userVO.setEmail(user.getEmail());
        userVO.setPhone(user.getPhone());
        userVO.setAvatar(user.getAvatar());
        userVO.setTenantId(tenantId);
        loginVO.setUser(userVO);

        return loginVO;
    }

    /**
     * 把令牌加入黑名单，使其立即失效（用于切换租户后回收旧令牌）。
     * 写黑名单失败不阻断主流程，但需要告警：旧令牌会一直有效到自然过期。
     */
    private void blacklistAccessToken(String accessToken, String userId) {
        if (StringUtils.isBlank(accessToken)) {
            return;
        }
        try {
            long ttlSeconds = jwtUtils.getRemainingExpirationSeconds(accessToken);
            tokenBlacklistService.addToBlacklist(accessToken, ttlSeconds);
        } catch (Exception e) {
            log.warn("切换租户后旧令牌加入黑名单失败，旧令牌将保持有效直到自然过期: userId={}", userId, e);
        }
    }

    /**
     * 判断令牌中的 role claim 是否含超级管理员通配权限码
     */
    private boolean isSuperAdmin(String role) {
        if (StringUtils.isBlank(role)) {
            return false;
        }
        for (String permission : role.split(",")) {
            if (SUPER_ADMIN_PERMISSION.equals(permission.trim())) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void logout(String userId, String tenantId) {
        tokenManager.deleteRefreshToken(tenantId, userId);
        onlineUserService.logout(userId, tenantId);
        log.info("用户登出: userId={}, tenantId={}", userId, tenantId);
    }

    @Override
    public void logout(String userId, String tenantId, String accessToken) {
        // 先清理刷新令牌与在线状态
        logout(userId, tenantId);
        // 再把访问令牌写入黑名单（TTL 取令牌剩余有效期），网关据此立即拒绝该令牌
        blacklistAccessToken(accessToken, userId);
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

        // 获取用户角色及数据权限
        try {
            ApiResult<SysRoleDto> roleResult = sysRoleFeignClient.getRoleByUserId(user.getId());
            if (roleResult != null && roleResult.isSuccess() && roleResult.getData() != null) {
                SysRoleDto roleDto = roleResult.getData();
                // 同 login()：刷新令牌时重新聚合权限码，保证权限变更能在换令牌后生效
                payload.setRole(resolvePermissionClaim(roleDto.getRoleCode(), user.getId()));
                payload.setDataScope(roleDto.getDataScope());
            }
        } catch (Exception e) {
            log.warn("获取用户角色信息失败, userId={}", user.getId(), e);
        }

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

    /**
     * 构建令牌中的 role claim。
     * <p>
     * 超管角色签发通配权限码 {@code *:*:*}；其余角色取实际权限码集合（逗号分隔）。
     * 权限码由 baize-system 的 {@code /system/role/menu/user/{userId}/perms} 聚合
     * 「用户 → 角色 → 角色菜单 → 菜单 perms」，查询失败时回退为角色编码，保证登录不中断。
     *
     * @param roleCode 角色编码（如 admin）
     * @param userId   用户ID
     * @return 写入令牌的 role claim
     */
    private String resolvePermissionClaim(String roleCode, String userId) {
        String roleClaim = resolveRoleClaim(roleCode);
        // 超管已是通配权限码，无需再查
        if (SUPER_ADMIN_PERMISSION.equals(roleClaim)) {
            return roleClaim;
        }
        try {
            ApiResult<List<String>> permResult = sysMenuFeignClient.getPermCodesByUserId(userId);
            if (permResult != null && permResult.isSuccess()
                    && permResult.getData() != null && !permResult.getData().isEmpty()) {
                String permissions = String.join(",", permResult.getData());
                log.info("已加载用户权限码: userId={}, count={}", userId, permResult.getData().size());
                return permissions;
            }
            log.warn("用户未分配任何权限码，回退为角色编码: userId={}, roleCode={}", userId, roleCode);
        } catch (Exception e) {
            log.warn("查询用户权限码失败，回退为角色编码: userId={}", userId, e);
        }
        return roleClaim;
    }

    /**
     * 把角色编码转换为令牌中的 role claim（权限码）。
     * <p>
     * 命中 super-admin-role-codes 的角色签发通配权限码 {@code *:*:*}，其余返回角色编码本身，
     * 由 {@link #resolvePermissionClaim(String, String)} 在其基础上补充实际权限码。
     *
     * @param roleCode 角色编码（如 admin）
     * @return 写入令牌的 role claim
     */
    private String resolveRoleClaim(String roleCode) {
        if (StringUtils.isBlank(roleCode)) {
            return roleCode;
        }
        if (StringUtils.isBlank(superAdminRoleCodes)) {
            return roleCode;
        }
        for (String superAdminRoleCode : superAdminRoleCodes.split(",")) {
            if (roleCode.equals(superAdminRoleCode.trim())) {
                return SUPER_ADMIN_PERMISSION;
            }
        }
        return roleCode;
    }

    private Map<String, Object> buildClaims(Payload payload) {
        Map<String, Object> claims = new HashMap<>();
        claims.put(CustomHttpHeaders.USER_ID, payload.getUserId());
        claims.put(CustomHttpHeaders.USER_NAME, payload.getUserName());
        claims.put(CustomHttpHeaders.TENANT_ID, payload.getTenantId());
        if (StringUtils.isNotBlank(payload.getRole())) {
            claims.put(CustomHttpHeaders.ROLE, payload.getRole());
        }
        if (StringUtils.isNotBlank(payload.getDataScope())) {
            claims.put(CustomHttpHeaders.DATA_SCOPE, payload.getDataScope());
        }
        return claims;
    }
}
