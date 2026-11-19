package com.gem.baize.common.security.domain.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * T1.3：签发结果中「仅用于写 HttpOnly Cookie、绝不进 JSON 响应」的刷新令牌载荷。
 *
 * <p>挂在 {@link LoginVO} 上并以 {@code @JsonIgnore} 排除序列化，仅在 auth 服务内部由
 * service → controller 传递；feature flag 关闭时本字段为 null，链路完全走旧逻辑。</p>
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class IssuedRefresh {
    /** 明文 refresh token，只写进 HttpOnly Cookie。 */
    private String rawToken;
    /** 令牌家族 ID，用于轮转链与复用检测。 */
    private String familyId;
    /** 过期时间（epoch millis）。 */
    private long expiresAt;
    /** CSRF 双重提交令牌，写进可被 JS 读取的 XSRF-TOKEN Cookie。 */
    private String csrfToken;
}
