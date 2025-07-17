package com.gem.baize.gateway.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 安全配置
 */
@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "baize.gateway.security")
public class SecurityProperties {
    private List<String> excludePaths;
}
