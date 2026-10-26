package com.gem.baize.gateway.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * 网关黑名单配置（{@code baize.gateway.denylist}）。
 * <p>
 * 用于封禁只允许服务间直连的内部接口，例如认证服务获取密码哈希的
 * {@code /api/v1/system/user/getPasswordByUsername}。这些接口带 {@code @InnerAuth}
 * 但内部令牌为空时不会校验，一旦经网关暴露即可被任意已登录用户调用。
 */
@Component
@ConfigurationProperties(prefix = "baize.gateway")
public class GatewayDenylist {

    /**
     * 拒绝访问的路径，支持与白名单一致的 {@code /**} 前缀匹配
     */
    private List<String> denylist = new ArrayList<>();

    public List<String> getDenylist() {
        return denylist;
    }

    public void setDenylist(List<String> denylist) {
        this.denylist = denylist == null ? new ArrayList<>() : denylist;
    }

    /**
     * 判断请求路径是否命中黑名单。
     *
     * @param path 请求路径，如 {@code /api/v1/system/user/getPasswordByUsername}
     * @return 命中返回 true
     */
    public boolean matches(String path) {
        return GatewayPathMatcher.matches(denylist, path);
    }
}
