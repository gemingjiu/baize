package com.gem.baize.common.database.handler;

import com.gem.baize.common.webmvc.context.RequestContextHolder;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.StringValue;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * 多租户行级隔离测试骨架（T1.6）。
 *
 * <p>覆盖 fail-safe 语义：租户上下文缺失时兜底为平台租户 nil UUID，绝不因取不到租户
 * 而放行全表；仅 sys_tenant 自身豁免过滤。
 */
class CustomTenantHandlerTest {

    private static final String NIL = "00000000-0000-0000-0000-000000000000";
    private static final String TENANT_A = "00000000-0000-7000-0000-000000000001";
    private static final String TENANT_B = "00000000-0000-7000-0000-000000000002";

    private final CustomTenantHandler handler = new CustomTenantHandler();

    @AfterEach
    void tearDown() {
        RequestContextHolder.remove();
    }

    @Test
    @DisplayName("上下文缺失时兜底为平台租户 nil UUID（fail-safe，不放行全表）")
    void missingTenantFallsBackToPlatform() {
        RequestContextHolder.remove();
        Expression expression = handler.getTenantId();
        assertThat(expression).isInstanceOf(StringValue.class);
        assertThat(((StringValue) expression).getValue()).isEqualTo(NIL);
    }

    @Test
    @DisplayName("有租户上下文时精确下推该租户 UUID")
    void tenantIdIsPushedDownVerbatim() {
        RequestContextHolder.setTenantId(TENANT_A);
        Expression expression = handler.getTenantId();
        assertThat(expression).isInstanceOf(StringValue.class);
        assertThat(((StringValue) expression).getValue()).isEqualTo(TENANT_A);
    }

    @Test
    @DisplayName("切换租户后下推新租户（同一用户跨租户 token 场景）")
    void switchedTenantIsPushedDown() {
        RequestContextHolder.setTenantId(TENANT_A);
        assertThat(((StringValue) handler.getTenantId()).getValue()).isEqualTo(TENANT_A);
        RequestContextHolder.setTenantId(TENANT_B);
        assertThat(((StringValue) handler.getTenantId()).getValue()).isEqualTo(TENANT_B);
    }

    @Test
    @DisplayName("仅 sys_tenant 元数据表豁免过滤，业务表一律过滤")
    void onlyTenantTableIsIgnored() {
        assertThat(handler.ignoreTable("sys_tenant")).isTrue();
        assertThat(handler.ignoreTable("sys_user")).isFalse();
        assertThat(handler.ignoreTable("sys_role")).isFalse();
        assertThat(handler.ignoreTable("sys_notice")).isFalse();
    }
}
