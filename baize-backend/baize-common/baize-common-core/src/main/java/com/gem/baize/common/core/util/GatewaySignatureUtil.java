package com.gem.baize.common.core.util;

import com.gem.baize.common.core.constant.CustomHttpHeaders;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * 网关签名工具。
 * <p>
 * 问题背景（P0-A）：后端 {@code HeaderInterceptor} 完全信任请求头里的
 * {@code X-Tenant-Id / X-User-Id / X-Role / X-Data-Scope / X-User-Name / X-Subject-Id / X-Trace-Id}，
 * 这些头一旦被伪造（例如直连后端 8020 端口绕过网关），攻击者即可越权切换租户 / 扮演任意用户。
 * 修复：网关注入身份头时附带一个 HMAC-SHA256 签名头 {@code X-Gateway-Signature}，后端在信任身份头之前先校验签名。
 * </p>
 * <p>
 * 该工具被网关（WebFlux）与后端（Servlet）共用，因此放在 {@code baize-common-core}（三者的共同依赖），
 * 且不引入任何第三方依赖，仅使用 JDK 自带的 {@code javax.crypto}。
 * </p>
 * <p>
 * 约定：参与签名的身份头按小写名称排序后拼接，签名串形如
 * {@code METHOD\nPATH\nheader:val\n...}，再用 HMAC-SHA256 计算并转 hex。
 * 网关与后端必须使用完全相同的规范化算法，否则校验必失败。
 * </p>
 * <p>
 * 降级策略（与 {@code JwtUtils} 一致）：若 {@code secret} 为空，{@link #sign} 返回 {@code null}，
 * 调用方据此「不签名 / 不校验」，保持与未启用签名时行为一致，避免密钥未配置时整站 401。
 * 一旦运维在 Nacos（或环境变量）同时给网关节点与后端节点配置了 {@code baize.gateway.signature-secret}，
 * 防护即自动生效（fail-closed）。
 * </p>
 */
public final class GatewaySignatureUtil {

    /** 签名头名称。 */
    public static final String SIGNATURE_HEADER = "X-Gateway-Signature";

    /** 配置项：网关与后端必须配置同一个密钥，否则签名/校验均被跳过（降级为不防护）。 */
    public static final String SECRET_PROPERTY = "baize.gateway.signature-secret";

    /** 参与签名的身份头（顺序无关，算法内部会排序）。 */
    private static final List<String> SIGNED_HEADERS = Arrays.asList(
            CustomHttpHeaders.TENANT_ID,
            CustomHttpHeaders.USER_ID,
            CustomHttpHeaders.SUBJECT_ID,
            CustomHttpHeaders.USER_NAME,
            CustomHttpHeaders.ROLE,
            CustomHttpHeaders.DATA_SCOPE,
            CustomHttpHeaders.TRACE_ID
    );

    private static final String HMAC_ALGORITHM = "HmacSHA256";

    private GatewaySignatureUtil() {
        throw new UnsupportedOperationException("工具类禁止实例化");
    }

    /**
     * 计算签名。
     *
     * @param method   HTTP 方法（大小写不敏感，内部转大写）
     * @param path     请求路径（网关与后端必须一致；本项目已去掉 StripPrefix，二者相同）
     * @param headers  请求头（key 大小写不敏感）；只取 {@link #SIGNED_HEADERS} 中存在的头
     * @param secret   共享密钥；为空返回 {@code null}（降级）
     * @return 小写 hex 签名串，或 {@code null}
     */
    public static String sign(String method, String path, Map<String, String> headers, String secret) {
        if (secret == null || secret.isEmpty()) {
            return null;
        }
        try {
            String canonical = canonicalize(method, path, headers);
            Mac mac = Mac.getInstance(HMAC_ALGORITHM);
            mac.init(new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), HMAC_ALGORITHM));
            byte[] digest = mac.doFinal(canonical.getBytes(StandardCharsets.UTF_8));
            return bytesToHex(digest);
        } catch (Exception e) {
            throw new IllegalStateException("网关签名计算失败", e);
        }
    }

    /**
     * 校验签名。
     *
     * @param method        HTTP 方法
     * @param path          请求路径
     * @param headers       请求头（key 大小写不敏感）
     * @param signature     客户端携带的签名（来自 {@link #SIGNATURE_HEADER}），可为 {@code null}
     * @param secret        共享密钥；为空返回 {@code false}（降级：不校验）
     * @return 校验是否通过
     */
    public static boolean verify(String method, String path, Map<String, String> headers,
                                 String signature, String secret) {
        if (secret == null || secret.isEmpty()) {
            return false;
        }
        if (signature == null || signature.isEmpty()) {
            return false;
        }
        String expected = sign(method, path, headers, secret);
        if (expected == null) {
            return false;
        }
        return constantTimeEquals(expected, signature);
    }

    /**
     * 判断请求是否携带了需要被信任的「身份头」。
     * 后端据此区分两类请求：
     * <ul>
     *   <li>携带身份头 → 必须附带合法签名，否则拒绝（防伪造）；</li>
     *   <li>未携带身份头 → 匿名/内部调用（内部 Feign 不转发身份头），放行（由 InnerAuth 等其他机制保护）。</li>
     * </ul>
     */
    public static boolean hasIdentityHeaders(Map<String, String> headers) {
        for (String h : SIGNED_HEADERS) {
            if (headers.getOrDefault(h, "").trim().isEmpty()) {
                // 尝试小写 key 命中（部分容器对自定义头大小写敏感）
                if (!headers.entrySet().stream()
                        .anyMatch(e -> e.getKey().equalsIgnoreCase(h) && e.getValue() != null
                                && !e.getValue().trim().isEmpty())) {
                    continue;
                }
            }
            return true;
        }
        return false;
    }

    private static String canonicalize(String method, String path, Map<String, String> headers) {
        StringBuilder sb = new StringBuilder();
        sb.append(method == null ? "" : method.toUpperCase()).append('\n');
        sb.append(path == null ? "" : path).append('\n');

        // 把 SIGNED_HEADERS 中存在的头按小写名称排序后拼接
        TreeMap<String, String> sorted = new TreeMap<>();
        for (String name : SIGNED_HEADERS) {
            String value = lookup(headers, name);
            if (value != null && !value.isEmpty()) {
                sorted.put(name.toLowerCase(), value);
            }
        }
        for (Map.Entry<String, String> entry : sorted.entrySet()) {
            sb.append(entry.getKey()).append(':').append(entry.getValue()).append('\n');
        }
        return sb.toString();
    }

    private static String lookup(Map<String, String> headers, String name) {
        String v = headers.get(name);
        if (v != null) {
            return v;
        }
        for (Map.Entry<String, String> e : headers.entrySet()) {
            if (e.getKey().equalsIgnoreCase(name)) {
                return e.getValue();
            }
        }
        return null;
    }

    private static boolean constantTimeEquals(String a, String b) {
        if (a.length() != b.length()) {
            return false;
        }
        int result = 0;
        for (int i = 0; i < a.length(); i++) {
            result |= a.charAt(i) ^ b.charAt(i);
        }
        return result == 0;
    }

    private static String bytesToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder(bytes.length * 2);
        for (byte b : bytes) {
            sb.append(Character.forDigit((b >> 4) & 0xF, 16));
            sb.append(Character.forDigit(b & 0xF, 16));
        }
        return sb.toString();
    }
}
