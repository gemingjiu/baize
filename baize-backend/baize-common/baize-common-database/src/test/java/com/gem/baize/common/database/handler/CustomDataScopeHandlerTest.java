package com.gem.baize.common.database.handler;

import com.gem.baize.common.core.constant.DataScopeConstant;
import com.gem.baize.common.database.util.DataScopeContext;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.StringValue;
import net.sf.jsqlparser.expression.operators.relational.EqualsTo;
import net.sf.jsqlparser.expression.operators.relational.InExpression;
import net.sf.jsqlparser.schema.Column;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * 数据权限五档测试骨架（T1.6）。
 *
 * <p><b>回归防线：</b>UUIDv7 迁移后 dept_id / create_by 已是 varchar(36) UUID，
 * 早期实现用 {@code \d{1,19}} 纯数字校验会把合法 UUID 判为非法并退化为 {@code 1=0}，
 * 导致 2/3/4/5 档数据权限静默失效。本测试全部使用真实 UUID 取值，确保下推正确。
 *
 * <p>fail-safe：算不出合法条件（空部门集 / 注入串）时下推 {@code 1=0}，宁可查不到也不越权。
 */
class CustomDataScopeHandlerTest {

    private static final String UUID_DEPT = "00000000-0000-7000-0000-000000000010";
    private static final String UUID_DEPT_2 = "00000000-0000-7000-0000-000000000011";
    private static final String UUID_USER = "00000000-0000-7000-0000-000000000020";
    private static final String INJECTION = "1' OR '1'='1";

    private final CustomDataScopeHandler handler = new CustomDataScopeHandler();

    // ---------- 1 全部 ----------

    @Test
    @DisplayName("档1 全部数据权限：不下推任何条件")
    void allScopePushesNothing() {
        Expression result = handler.buildWhere(null,
                DataScopeContext.builder().dataScope(DataScopeConstant.DATA_SCOPE_ALL).userId(UUID_USER));
        assertThat(result).isNull();
    }

    // ---------- 2 自定义 / 4 本部门及以下 ----------

    @Test
    @DisplayName("档2 自定义范围：UUID 部门集合正确下推 IN（回归防线）")
    void customScopePushesUuidDeptIds() {
        Expression result = handler.buildWhere(null, DataScopeContext.builder()
                .dataScope(DataScopeConstant.DATA_SCOPE_CUSTOM)
                .deptIds(List.of(UUID_DEPT, UUID_DEPT_2)));
        assertThat(result).isInstanceOf(InExpression.class);
        String sql = result.toString();
        assertThat(sql).contains("dept_id").contains(UUID_DEPT).contains(UUID_DEPT_2);
        assertThat(sql).doesNotContain("1 = 0");
    }

    @Test
    @DisplayName("档4 本部门及以下：UUID 部门集合正确下推 IN")
    void deptAndChildScopePushesUuidDeptIds() {
        Expression result = handler.buildWhere(null, DataScopeContext.builder()
                .dataScope(DataScopeConstant.DATA_SCOPE_DEPT_AND_CHILD)
                .deptIds(List.of(UUID_DEPT)));
        assertThat(result).isInstanceOf(InExpression.class);
        assertThat(result.toString()).contains("dept_id").contains(UUID_DEPT);
    }

    // ---------- 3 本部门 ----------

    @Test
    @DisplayName("档3 本部门：UUID 部门正确下推 dept_id = ?（回归防线）")
    void deptScopePushesUuidDeptId() {
        Expression result = handler.buildWhere(null, DataScopeContext.builder()
                .dataScope(DataScopeConstant.DATA_SCOPE_DEPT)
                .deptId(UUID_DEPT));
        assertThat(result).isInstanceOf(EqualsTo.class);
        EqualsTo eq = (EqualsTo) result;
        assertThat(eq.getLeftExpression()).isInstanceOf(Column.class);
        assertThat(((Column) eq.getLeftExpression()).getColumnName()).isEqualTo("dept_id");
        assertThat(eq.getRightExpression()).isInstanceOf(StringValue.class);
        assertThat(((StringValue) eq.getRightExpression()).getValue()).isEqualTo(UUID_DEPT);
    }

    // ---------- 5 仅本人 ----------

    @Test
    @DisplayName("档5 仅本人：UUID 用户正确下推 create_by = ?（回归防线）")
    void selfScopePushesUuidUserId() {
        Expression result = handler.buildWhere(null, DataScopeContext.builder()
                .dataScope(DataScopeConstant.DATA_SCOPE_SELF)
                .userId(UUID_USER));
        assertThat(result).isInstanceOf(EqualsTo.class);
        EqualsTo eq = (EqualsTo) result;
        assertThat(((Column) eq.getLeftExpression()).getColumnName()).isEqualTo("create_by");
        assertThat(((StringValue) eq.getRightExpression()).getValue()).isEqualTo(UUID_USER);
    }

    // ---------- fail-safe ----------

    @Test
    @DisplayName("空部门集合：下推 1=0 不可见条件")
    void emptyDeptSetDegradesToFalse() {
        Expression result = handler.buildWhere(null, DataScopeContext.builder()
                .dataScope(DataScopeConstant.DATA_SCOPE_CUSTOM)
                .deptIds(List.of()));
        assertThat(isAlwaysFalse(result)).isTrue();
    }

    @Test
    @DisplayName("注入串：拒绝下推并退化为 1=0（注入防护）")
    void injectionDegradesToFalse() {
        Expression custom = handler.buildWhere(null, DataScopeContext.builder()
                .dataScope(DataScopeConstant.DATA_SCOPE_CUSTOM)
                .deptIds(List.of(INJECTION)));
        assertThat(isAlwaysFalse(custom)).isTrue();

        Expression self = handler.buildWhere(null, DataScopeContext.builder()
                .dataScope(DataScopeConstant.DATA_SCOPE_SELF)
                .userId(INJECTION));
        assertThat(isAlwaysFalse(self)).isTrue();
    }

    @Test
    @DisplayName("skip 标记：跳过本次过滤")
    void skipReturnsWhereUnchanged() {
        Expression result = handler.buildWhere(null, DataScopeContext.builder()
                .dataScope(DataScopeConstant.DATA_SCOPE_SELF)
                .userId(UUID_USER)
                .skip(true));
        assertThat(result).isNull();
    }

    /** 判定是否为 {@code 1 = 0} 兜底条件。 */
    private boolean isAlwaysFalse(Expression expr) {
        if (!(expr instanceof EqualsTo eq)) {
            return false;
        }
        return eq.getLeftExpression() instanceof StringValue left
                && eq.getRightExpression() instanceof StringValue right
                && "1".equals(left.getValue())
                && "0".equals(right.getValue());
    }
}
