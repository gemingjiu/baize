package com.gem.baize.common.database.handler;

import com.gem.baize.common.core.constant.DataScopeConstant;
import com.gem.baize.common.database.util.DataScopeContext;
import lombok.extern.slf4j.Slf4j;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.StringValue;
import net.sf.jsqlparser.expression.operators.conditional.AndExpression;
import net.sf.jsqlparser.expression.operators.relational.EqualsTo;
import net.sf.jsqlparser.expression.operators.relational.ExpressionList;
import net.sf.jsqlparser.expression.operators.relational.InExpression;
import net.sf.jsqlparser.schema.Column;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * 数据权限条件生成器。
 * <p>
 * 与 {@code CustomTenantHandler} 同属一层，但两者正交、可叠加：
 * 租户拦截器管 {@code tenant_id = X}（行级隔离），本处理器管 {@code dept_id IN (...)}
 * （角色可见范围）。两者各自向 WHERE 追加 AND 条件，互不感知。
 *
 * <p><b>五种范围语义（取值见 DataScopeConstant）：</b>
 * <pre>
 *   1 ALL             -> 不加条件
 *   2 CUSTOM          -> dept_id IN (sys_role_dept 为该角色配置的部门)
 *   3 DEPT            -> dept_id = 用户所属部门
 *   4 DEPT_AND_CHILD  -> dept_id IN (用户部门及其所有子孙部门)
 *   5 SELF            -> create_by = 当前用户ID
 * </pre>
 *
 * <p><b>fail-safe：</b>需要下推部门集合却算出空集合时，返回 {@code 1 = 0}（永不成立）
 * 而不是返回 null——"宁可查不到，也不越权看到全部"。
 *
 * <p><b>注入防护：</b>dept_id / create_by 在库中均为 bigint，因此下推前用
 * {@link #NUMERIC} 严格校验；任何非纯数字取值都拒绝下推并退化为 {@code 1 = 0}，
 * 避免把外部输入直接拼进 SQL。
 */
@Slf4j
public class CustomDataScopeHandler {

    private static final Pattern NUMERIC = Pattern.compile("\\d{1,19}");

    /**
     * 生成追加到 WHERE 上的数据权限条件；不需要过滤时返回 null。
     *
     * @param where   原 WHERE 表达式，可为 null
     * @param context 本次调用的数据权限上下文，可为 null
     * @return 新的 WHERE 表达式，或 null 表示保持原样
     */
    public Expression buildWhere(Expression where, DataScopeContext context) {
        if (context == null || context.isSkip()) {
            return where;
        }
        String dataScope = context.getDataScope();
        if (isBlank(dataScope) || DataScopeConstant.DATA_SCOPE_ALL.equals(dataScope)) {
            // 1 全部数据权限
            return where;
        }

        Expression segment = switch (dataScope) {
            case DataScopeConstant.DATA_SCOPE_CUSTOM -> inDepts(context.getDeptIds(), context.getDeptAlias());
            case DataScopeConstant.DATA_SCOPE_DEPT -> eqColumn("dept_id", context.getDeptId(), context.getDeptAlias());
            case DataScopeConstant.DATA_SCOPE_DEPT_AND_CHILD -> inDepts(context.getDeptIds(), context.getDeptAlias());
            case DataScopeConstant.DATA_SCOPE_SELF -> eqColumn("create_by", context.getUserId(), context.getUserAlias());
            default -> {
                log.warn("未知的数据范围取值 dataScope={}，已按【仅本人】兜底", dataScope);
                yield eqColumn("create_by", context.getUserId(), context.getUserAlias());
            }
        };

        return where == null ? segment : new AndExpression(where, segment);
    }

    private Expression inDepts(List<String> deptIds, String deptAlias) {
        List<Expression> values = new ArrayList<>();
        if (deptIds != null) {
            for (String id : deptIds) {
                if (isBlank(id)) {
                    continue;
                }
                if (!NUMERIC.matcher(id.trim()).matches()) {
                    log.warn("部门ID取值非法（非纯数字），已拒绝下推并退化为不可见：{}", id);
                    return alwaysFalse();
                }
                values.add(new StringValue(id.trim()));
            }
        }
        if (values.isEmpty()) {
            return alwaysFalse();
        }
        // jsqlparser 5.0：InExpression 只有 setRightExpression(Expression)，4.x 的 setRightItemsList(ExpressionList) 已移除
        InExpression in = new InExpression();
        in.setLeftExpression(new Column(qualify(deptAlias, "dept_id")));
        in.setRightExpression(new ExpressionList<>(values));
        return in;
    }

    private Expression eqColumn(String column, String value, String alias) {
        if (isBlank(value)) {
            return alwaysFalse();
        }
        String v = value.trim();
        if (!NUMERIC.matcher(v).matches()) {
            log.warn("字段 {} 取值非法（非纯数字），已拒绝下推并退化为不可见：{}", column, value);
            return alwaysFalse();
        }
        return new EqualsTo(new Column(qualify(alias, column)), new StringValue(v));
    }

    /** 永不成立条件：用于"本应过滤但算不出合法条件"时兜底，避免全表可见 */
    private Expression alwaysFalse() {
        return new EqualsTo(new StringValue("1"), new StringValue("0"));
    }

    private String qualify(String alias, String column) {
        return isBlank(alias) ? column : alias.trim() + "." + column;
    }

    private boolean isBlank(String s) {
        return s == null || s.isBlank();
    }
}
