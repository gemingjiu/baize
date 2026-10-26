package com.gem.baize.common.database.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.ArrayList;
import java.util.List;

/**
 * 数据权限配置项，键前缀 {@code baize.data-scope}。
 * <p>
 * 默认关闭（{@code enabled=false}）：数据权限是横切行为，一旦开启会影响所有业务查询，
 * 因此必须显式打开，避免"改完代码后线上查询突然少数据"。
 */
@Data
@ConfigurationProperties(prefix = "baize.data-scope")
public class DataScopeProperties {

    /**
     * 是否启用数据权限过滤，默认关闭
     */
    private boolean enabled = false;

    /**
     * 参与数据权限过滤的表名白名单。为空表示"不按白名单放行"，
     * 此时由 {@code DataScopeInterceptor} 依据表是否含 dept_id/create_by 列来决定。
     */
    private List<String> tables = new ArrayList<>();
}
