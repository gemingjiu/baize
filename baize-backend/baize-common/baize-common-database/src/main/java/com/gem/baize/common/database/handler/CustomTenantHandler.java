package com.gem.baize.common.database.handler;

import com.baomidou.mybatisplus.extension.plugins.handler.TenantLineHandler;
import com.gem.baize.common.webmvc.context.RequestContextHolder;
import lombok.extern.slf4j.Slf4j;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.StringValue;
import org.apache.commons.lang3.StringUtils;

import java.util.Set;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 多租户行级隔离处理器。
 * <p>
 * fail-safe 原则：租户上下文缺失时按平台租户（nil UUID）处理，绝不因为取不到租户就放行全表，
 * 否则查询侧会跨租户全量可见、插入侧会因 tenant_id NOT NULL 违约。
 * 仅租户元数据表 {@code sys_tenant} 自身不参与租户过滤。
 */
@Slf4j
public class CustomTenantHandler implements TenantLineHandler {
    /** 不需要租户过滤的表（仅租户元数据表自身，平台/租户管理必须可见） */
    private static final Set<String> IGNORED_TABLES = Set.of("sys_tenant");
    /** 平台级租户ID（nil UUID），同时也是租户上下文缺失时的兜底租户 */
    private static final String PLATFORM_TENANT = "00000000-0000-0000-0000-000000000000";
    /** 空租户告警最小间隔（毫秒），避免无租户上下文时刷爆日志 */
    private static final long WARN_INTERVAL_MILLIS = 5 * 60 * 1000L;
    private static final AtomicLong LAST_WARN_AT = new AtomicLong(0L);

    @Override
    public Expression getTenantId() {
        String tenantId = RequestContextHolder.getTenantId();
        // 租户ID为空时兜底为平台租户，保证过滤条件始终可下推。
        // tenant_id 已是 varchar(36)，必须用 StringValue 输出带引号的字符串字面量
        //（LongValue 会渲染成裸数字，导致 character varying = integer 比较报错）。
        if (StringUtils.isBlank(tenantId)) {
            warnMissingTenant(tenantId);
            return new StringValue(PLATFORM_TENANT);
        }
        return new StringValue(tenantId);
    }

    @Override
    public boolean ignoreTable(String tableName) {
        // fail-safe：只有租户元数据表自身豁免；租户为空也不放行（由 getTenantId 兜底为 "0"）
        return IGNORED_TABLES.contains(tableName);
    }

    /**
     * 空租户告警去重：每 {@link #WARN_INTERVAL_MILLIS} 毫秒最多打印一次，避免日志被淹没。
     */
    private void warnMissingTenant(String rawTenantId) {
        long now = System.currentTimeMillis();
        long last = LAST_WARN_AT.get();
        if (now - last >= WARN_INTERVAL_MILLIS && LAST_WARN_AT.compareAndSet(last, now)) {
            log.warn("请求上下文缺少有效租户ID(raw={}), 已按平台租户[{}]处理; 该告警每{}分钟最多打印一次",
                    rawTenantId, PLATFORM_TENANT, WARN_INTERVAL_MILLIS / 60_000L);
        }
    }
}
