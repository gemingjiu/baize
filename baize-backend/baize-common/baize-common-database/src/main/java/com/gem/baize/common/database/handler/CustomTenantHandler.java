package com.gem.baize.common.database.handler;

import com.baomidou.mybatisplus.extension.plugins.handler.TenantLineHandler;
import com.gem.baize.common.webmvc.context.RequestContextHolder;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.LongValue;
import org.apache.commons.lang3.StringUtils;

import java.util.Set;

public class CustomTenantHandler implements TenantLineHandler {
    private static final Set<String> IGNORED_TABLES = Set.of("sys_tenant");
    private static final Set<String> RELATION_TABLES = Set.of("sys_user_role", "sys_role_menu", "sys_role_perm");
    private static final String ADMIN_TENANT = "admin";

    @Override
    public Expression getTenantId() {
        String tenantId = RequestContextHolder.getTenantId();
        return new LongValue(tenantId);
    }

    @Override
    public boolean ignoreTable(String tableName) {
        String tenantId = RequestContextHolder.getTenantId();
        // 租户为空或admin租户跳过过滤
        if (StringUtils.isBlank(tenantId) || ADMIN_TENANT.equals(tenantId)) {
            return true;
        }
        // 白名单表跳过过滤
        if (IGNORED_TABLES.contains(tableName)) {
            return true;
        }
        // 关系表跳过过滤
        return RELATION_TABLES.contains(tableName);
    }

}
