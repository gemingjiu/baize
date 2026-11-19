package com.gem.baize.auth.service.impl;

import com.gem.baize.api.system.config.client.SysConfigFeignClient;
import com.gem.baize.api.system.menu.client.SysMenuFeignClient;
import com.gem.baize.api.system.role.client.SysRoleFeignClient;
import com.gem.baize.api.system.role.domain.dto.SysRoleDto;
import com.gem.baize.api.system.tenant.client.SysTenantFeignClient;
import com.gem.baize.api.system.tenant.domain.dto.SysTenantDto;
import com.gem.baize.api.system.user.client.SysUserFeignClient;
import com.gem.baize.api.system.user.domain.dto.SysUserDto;
import com.gem.baize.auth.config.AuthCookieProperties;
import com.gem.baize.auth.domain.RefreshTokenRecord;
import com.gem.baize.auth.manager.RefreshTokenStore;
import com.gem.baize.auth.manager.TokenManager;
import com.gem.baize.auth.service.AuthService;
import com.gem.baize.auth.service.CaptchaService;
import com.gem.baize.common.cache.service.TokenBlacklistService;
import com.gem.baize.common.core.constant.CustomHttpHeaders;
import com.gem.baize.common.core.constant.DataScopeConstant;
import com.gem.baize.common.core.exception.model.ForbiddenException;
import com.gem.baize.common.core.exception.model.NotFoundException;
import com.gem.baize.common.core.exception.model.ParamException;
import com.gem.baize.common.core.exception.model.UnauthorizedException;
import com.gem.baize.common.core.model.vo.ApiResult;
import com.gem.baize.common.core.service.OnlineUserService;
import com.gem.baize.common.security.domain.dto.LoginDTO;
import com.gem.baize.common.security.domain.dto.Payload;
import com.gem.baize.common.security.domain.dto.TokenRefreshDTO;
import com.gem.baize.common.security.domain.vo.IssuedRefresh;
import com.gem.baize.common.security.domain.vo.LoginVO;
import com.gem.baize.common.security.domain.vo.UserVO;
import com.gem.baize.common.security.util.JwtUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
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

    /** T1.3：HttpOnly Cookie 模式刷新令牌存储，仅 flag 开启时使用。 */
    @Autowired
    private RefreshTokenStore refreshTokenStore;

    /** T1.3：特性开关，默认 false 保持旧链路零变更。 */
    @Autowired
    private AuthCookieProperties authCookieProperties;

    @Autowired
    private OnlineUserService onlineUserService;

    @Autowired
    private TokenBlacklistService tokenBlacklistService;

    @Autowired
    private CaptchaService captchaService;

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
     * 平台（保留）租户ID（nil UUID），与 CustomTenantHandler.PLATFORM_TENANT、网关 TenantFilter.DEFAULT_TENANT_ID 一致。
     */
    private static final String PLATFORM_TENANT_ID = "00000000-0000-0000-0000-000000000000";

    @Override
    public LoginVO login(LoginDTO dto) {
        log.info("用户登录请求: username={}, tenant={}", dto.getUsername(), dto.getTenant());
        // 验证码校验（P5）：sys.account.captchaEnabled=true 时必填；开关关闭时跳过
        if (captchaService.isEnabled()) {
            if (!captchaService.validate(dto.getCaptchaKey(), dto.getCaptcha())) {
                throw new UnauthorizedException("验证码错误或已过期");
            }
        }
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

        // 租户过期校验（P4）：expire_time 已过的租户拒绝登录（null 表示永不过期）
        checkTenantNotExpired(user.getTenantId());

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
        // 目标租户ID必须是 UUIDv7 形态（36 位含连字符）。2026-11-05 P4 修复：
        // 历史代码用 isNumeric 校验数字，UUIDv7 迁移后所有合法租户ID都会被误拒，
        // 切换租户功能自 V2026_10_21 起完全不可用。
        if (StringUtils.isBlank(targetTenant) || !targetTenant.matches("[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}")) {
            throw new ParamException("目标租户ID格式非法");
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

        // 租户过期校验（P4）：过期租户拒绝切换
        checkTenantNotExpired(targetTenant);

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
     * 租户过期校验（2026-11-05 P4）：expire_time 已过拒绝登录/切换；
     * 查不到租户行（含 inner 接口异常）不拦截——过期校验是增强治理，不作为鉴权边界。
     */
    private void checkTenantNotExpired(String tenantId) {
        if (StringUtils.isBlank(tenantId)) {
            return;
        }
        try {
            ApiResult<SysTenantDto> tenantResult = sysTenantFeignClient.getById(tenantId);
            if (tenantResult == null || !tenantResult.isSuccess() || tenantResult.getData() == null) {
                return;
            }
            java.time.LocalDateTime expireTime = tenantResult.getData().getExpireTime();
            if (expireTime != null && expireTime.isBefore(java.time.LocalDateTime.now())) {
                throw new ForbiddenException("租户已过期，请联系管理员");
            }
        } catch (ForbiddenException e) {
            throw e;
        } catch (Exception e) {
            log.warn("租户过期校验失败（不拦截登录）: tenantId={}, error={}", tenantId, e.getMessage());
        }
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
            ApiResult<List<SysRoleDto>> rolesResult = sysRoleFeignClient.getRolesByUserId(user.getId());
            if (rolesResult != null && rolesResult.isSuccess() && rolesResult.getData() != null) {
                List<SysRoleDto> roles = rolesResult.getData();
                // 多角色聚合签发：超管判定扫全部角色码，避免「绑定了超管角色但排第一的不是超管」被截断
                payload.setRole(resolvePermissionClaim(roles, user.getId()));
                // 数据范围聚合：任一角色“1 全部”→ 全部；否则含部门类（2/3/4）→ 按自定义档下推全部角色部门并集；否则“5 仅本人”
                payload.setDataScope(resolveAggregatedDataScope(roles));
            }
        } catch (Exception e) {
            log.warn("获取用户角色信息失败, userId={}", user.getId(), e);
        }

        String accessToken = jwtUtils.createToken(user.getId(), buildClaims(payload));

        LoginVO loginVO = new LoginVO();
        loginVO.setAccessToken(accessToken);
        loginVO.setExpiresIn((int) (jwtExpirationMs / 1000));

        if (authCookieProperties.isEnabled()) {
            // T1.3：HttpOnly Cookie 模式——明文 refresh 只通过 IssuedRefresh 交给 controller 写 Cookie，
            // 不进 JSON；服务端存哈希 + family。loginVO.refreshToken 保持 null。
            IssuedRefresh issued = refreshTokenStore.issue(tenantId, user.getId(), user.getUserName());
            loginVO.setIssuedRefresh(issued);
        } else {
            // 旧链路：JSON 返回 refresh，Redis 明文存储（feature flag 关闭时零变更）
            String refreshToken = UUID.randomUUID().toString().replace("-", "");
            tokenManager.saveRefreshToken(tenantId, user.getId(), refreshToken);
            loginVO.setRefreshToken(refreshToken);
        }

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
        logout(userId, tenantId, accessToken, null);
    }

    @Override
    public void logout(String userId, String tenantId, String accessToken, String refreshToken) {
        // 先清理刷新令牌与在线状态
        logout(userId, tenantId);
        // T1.3：flag 开启时按明文 refresh 吊销整个 family（含所有已轮转令牌）
        if (authCookieProperties.isEnabled() && StringUtils.isNotBlank(refreshToken)) {
            refreshTokenStore.revokeByToken(refreshToken);
        }
        // 再把访问令牌写入黑名单（TTL 取令牌剩余有效期），网关据此立即拒绝该令牌
        blacklistAccessToken(accessToken, userId);
    }

    @Override
    public LoginVO refreshToken(TokenRefreshDTO dto) {
        log.info("刷新令牌请求: userId={}", dto.getUserId());

        // 1. 验证刷新令牌
        // T1.3：flag 开启时走 family 轮转 + 复用检测（服务端只存哈希）；关闭时保持旧明文校验零变更。
        String familyId = null;
        if (authCookieProperties.isEnabled()) {
            RefreshTokenRecord consumed = refreshTokenStore.consume(dto.getRefreshToken());
            // 身份一致性：令牌内的 tenant/user 必须与 DTO 声明一致，防止跨租户/跨用户重放
            if (!consumed.getTenantId().equals(dto.getTenantId())
                    || !consumed.getUserId().equals(dto.getUserId())) {
                throw new UnauthorizedException("刷新令牌与请求身份不一致");
            }
            familyId = consumed.getFamilyId();
        } else {
            boolean valid = tokenManager.validateRefreshToken(dto.getTenantId(), dto.getUserId(), dto.getRefreshToken());
            if (!valid) {
                throw new NotFoundException("刷新令牌无效或已过期");
            }
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
            ApiResult<List<SysRoleDto>> rolesResult = sysRoleFeignClient.getRolesByUserId(user.getId());
            if (rolesResult != null && rolesResult.isSuccess() && rolesResult.getData() != null) {
                List<SysRoleDto> roles = rolesResult.getData();
                // 同 login()：多角色聚合签发，刷新令牌时重新聚合权限码，保证权限变更能在换令牌后生效
                payload.setRole(resolvePermissionClaim(roles, user.getId()));
                payload.setDataScope(resolveAggregatedDataScope(roles));
            }
        } catch (Exception e) {
            log.warn("获取用户角色信息失败, userId={}", user.getId(), e);
        }

        Map<String, Object> claims = buildClaims(payload);
        String accessToken = jwtUtils.createToken(user.getId(), claims);

        // 4. 生成新的刷新令牌
        LoginVO loginVO = new LoginVO();
        loginVO.setAccessToken(accessToken);
        loginVO.setExpiresIn((int) (jwtExpirationMs / 1000));
        if (authCookieProperties.isEnabled()) {
            // T1.3：同 family 轮转签发新令牌，明文只走 Cookie
            loginVO.setIssuedRefresh(refreshTokenStore.issueForFamily(
                    familyId, user.getTenantId(), user.getId(), user.getUserName()));
        } else {
            String refreshToken = UUID.randomUUID().toString().replace("-", "");
            tokenManager.saveRefreshToken(user.getTenantId(), user.getId(), refreshToken);
            loginVO.setRefreshToken(refreshToken);
        }

        return loginVO;
    }

    /**
     * 构建令牌中的 role claim（多角色聚合）。
     * <p>
     * 任一角色命中超管白名单即签发通配权限码 {@code *:*:*}；其余角色取实际权限码集合（逗号分隔）。
     * 权限码由 baize-system 的 {@code /system/role/menu/user/{userId}/perms} 聚合
     * 「用户 → 全部角色 → 角色菜单 → 菜单 perms」，查询失败时回退为全部角色编码，保证登录不中断。
     *
     * @param roles   用户绑定的全部角色（可能为空列表）
     * @param userId  用户ID
     * @return 写入令牌的 role claim
     */
    private String resolvePermissionClaim(List<SysRoleDto> roles, String userId) {
        String roleClaim = resolveRoleClaim(roles);
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
            log.warn("用户未分配任何权限码，回退为角色编码: userId={}, roleCodes={}", userId, roleClaim);
        } catch (Exception e) {
            log.warn("查询用户权限码失败，回退为角色编码: userId={}", userId, e);
        }
        return roleClaim;
    }

    /**
     * 把全部角色编码合并为令牌中的 role claim 兜底值。
     * <p>
     * 任一角色命中 super-admin-role-codes 即返回通配权限码 {@code *:*:*}；
     * 否则返回全部角色编码的逗号串（去空去重，保持绑定顺序），
     * 由 {@link #resolvePermissionClaim(List, String)} 在其基础上补充实际权限码。
     *
     * @param roles 用户绑定的全部角色
     * @return 写入令牌的 role claim 兜底值
     */
    private String resolveRoleClaim(List<SysRoleDto> roles) {
        if (CollectionUtils.isEmpty(roles)) {
            return "";
        }
        List<String> roleCodes = new ArrayList<>(roles.size());
        for (SysRoleDto role : roles) {
            String roleCode = role.getRoleCode();
            if (StringUtils.isNotBlank(roleCode) && !roleCodes.contains(roleCode)) {
                roleCodes.add(roleCode);
            }
        }
        // 任一角色是超管角色即放行通配，避免多角色时超管角色被其他角色截断
        if (StringUtils.isNotBlank(superAdminRoleCodes)) {
            for (String superAdminRoleCode : superAdminRoleCodes.split(",")) {
                String trimmed = superAdminRoleCode.trim();
                if (!trimmed.isEmpty() && roleCodes.contains(trimmed)) {
                    return SUPER_ADMIN_PERMISSION;
                }
            }
        }
        return String.join(",", roleCodes);
    }

    /**
     * 聚合全部角色的数据范围（取"最大可见范围"，与数据权限"可见范围取并集"的直觉一致）。
     * <ul>
     *   <li>任一角色为 {@code 1 全部} → {@code 1}（拦截器直接放行）；</li>
     *   <li>否则含部门类 {@code 2 自定义 / 3 本部门 / 4 本部门及以下} → {@code 2}，
     *       由 SysDataScopeDeptProvider 按全部角色逐角色解析部门并集（含本部门/子树档）；</li>
     *   <li>全部角色均为 {@code 5 仅本人}（或无角色）→ {@code 5}。</li>
     * </ul>
     */
    private String resolveAggregatedDataScope(List<SysRoleDto> roles) {
        if (CollectionUtils.isEmpty(roles)) {
            return null;
        }
        boolean hasDeptScope = false;
        for (SysRoleDto role : roles) {
            String scope = role.getDataScope();
            if (DataScopeConstant.DATA_SCOPE_ALL.equals(scope)) {
                return DataScopeConstant.DATA_SCOPE_ALL;
            }
            if (DataScopeConstant.DATA_SCOPE_CUSTOM.equals(scope)
                    || DataScopeConstant.DATA_SCOPE_DEPT.equals(scope)
                    || DataScopeConstant.DATA_SCOPE_DEPT_AND_CHILD.equals(scope)) {
                hasDeptScope = true;
            }
        }
        return hasDeptScope ? DataScopeConstant.DATA_SCOPE_CUSTOM : DataScopeConstant.DATA_SCOPE_SELF;
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
