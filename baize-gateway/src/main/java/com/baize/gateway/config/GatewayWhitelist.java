package com.baize.gateway.config;

import java.util.ArrayList;
import java.util.List;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Configuration;

import com.baize.common.core.utils.text.StringUtils;

/**
 * 网关白名单配置
 *
 * @author gemj
 */
@Configuration
@RefreshScope
@ConfigurationProperties(prefix = "baize.gateway")
public class GatewayWhitelist {
    /**
     * 放行白名单配置，网关不校验此处的白名单
     */
    private List<String> whitelist = new ArrayList<>();

    public List<String> getWhitelist() {
        return whitelist;
    }

    public void setWhitelist(List<String> whitelist) {
        this.whitelist = whitelist;
    }

    public boolean checkUrl(String url) {
        return StringUtils.matches(url, whitelist);
    }
}
