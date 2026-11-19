package com.gem.baize.auth.domain;

import lombok.Data;

import java.io.Serializable;
import java.util.regex.Pattern;

/**
 * T1.3 refresh token 在 Redis 中的记录（纯数据 + 编解码，便于无中间件单测）。
 *
 * <p>服务端只存 token 的 SHA-256 哈希作为 key，value 为本记录；明文 token 仅通过 HttpOnly Cookie 下发。</p>
 */
@Data
public class RefreshTokenRecord implements Serializable {

    public static final String STATE_ACTIVE = "active";
    public static final String STATE_USED = "used";

    private static final String SEP = "|";
    private static final int FIELD_COUNT = 6;
    /** '|' 是正则元字符，必须 Pattern.quote 转义，否则 split 会退化成「处处可切」。 */
    private static final Pattern SPLITTER = Pattern.compile(Pattern.quote(SEP));

    private String familyId;
    private String tenantId;
    private String userId;
    private String state;
    private long expiresAt;
    /** 放最后一位：用户名理论上可能含分隔符，split 限长后整段归入本字段。 */
    private String username;

    /**
     * 编码为可存 Redis 的单行字符串。字段顺序固定，用户名置末位以兼容其中的 '|'。
     */
    public String encode() {
        return String.join(SEP,
                nz(familyId), nz(tenantId), nz(userId), nz(state),
                String.valueOf(expiresAt), nz(username));
    }

    /**
     * 解码；格式非法（字段数不符或过期时间非数字）返回 null，交由调用方按「无效令牌」处理。
     */
    public static RefreshTokenRecord decode(String raw) {
        if (raw == null || raw.isEmpty()) {
            return null;
        }
        String[] p = SPLITTER.split(raw, FIELD_COUNT);
        if (p.length != FIELD_COUNT) {
            return null;
        }
        RefreshTokenRecord r = new RefreshTokenRecord();
        r.familyId = p[0];
        r.tenantId = p[1];
        r.userId = p[2];
        r.state = p[3];
        r.username = p[5];
        try {
            r.expiresAt = Long.parseLong(p[4]);
        } catch (NumberFormatException e) {
            return null;
        }
        return r;
    }

    private static String nz(String s) {
        return s == null ? "" : s;
    }
}
