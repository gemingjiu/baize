package com.gem.baize.common.database.handler;

import com.baomidou.mybatisplus.extension.plugins.handler.TenantLineHandler;
import com.gem.baize.common.core.context.RequestContextHolder;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.LongValue;

public class CustomTenantHandler implements TenantLineHandler {
    @Override
    public Expression getTenantId() {
        // 假设有一个租户上下文，能够从中获取当前用户的租户
        String tenantId = RequestContextHolder.getTenantId();
        // 返回租户ID的表达式，LongValue 是 JSQLParser 中的 bigint
        return new LongValue(tenantId);
    }
}
