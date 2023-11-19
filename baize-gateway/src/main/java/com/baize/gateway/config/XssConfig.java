package com.baize.gateway.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;

/**
 * XSS 跨站脚本配置
 *
 * @author gemj
 */
@Configuration
@RefreshScope
@ConfigurationProperties(prefix = "baize.gateway.xss")
public class XssConfig {
    /**
     * Xss开关
     */
    private Boolean enabled;

    /**
     * 排除路径
     */
    private List<String> excludes = new ArrayList<>();

    public Boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    public List<String> getExcludes() {
        return excludes;
    }

    public void setExcludes(List<String> excludes) {
        this.excludes = excludes;
    }
}