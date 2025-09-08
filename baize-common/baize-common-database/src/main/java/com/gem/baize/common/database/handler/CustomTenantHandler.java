package com.gem.baize.common.database.handler;

import com.baomidou.mybatisplus.extension.plugins.handler.TenantLineHandler;
import com.gem.baize.common.webmvc.context.RequestContextHolder;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.LongValue;

import java.util.Set;

public class CustomTenantHandler implements TenantLineHandler {
    private static final Set<String> IGNORED_TABLES = Set.of("sys_tenant");

    @Override
    public Expression getTenantId() {
        // 假设有一个租户上下文，能够从中获取当前用户的租户
        String tenantId = RequestContextHolder.getTenantId();
        // 返回租户ID的表达式，LongValue 是 JSQLParser 中的 bigint
        return new LongValue(tenantId);
    }

    @Override
    public boolean ignoreTable(String tableName) {
        return IGNORED_TABLES.contains(tableName);
    }

}
