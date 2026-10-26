package com.gem.baize.common.database.interceptor;

import com.baomidou.mybatisplus.extension.plugins.inner.BaseMultiTableInnerInterceptor;
import com.gem.baize.common.core.constant.DataScopeConstant;
import com.gem.baize.common.database.annotation.DataScope;
import com.gem.baize.common.database.config.DataScopeProperties;
import com.gem.baize.common.database.handler.CustomDataScopeHandler;
import com.gem.baize.common.database.handler.DataScopeDeptProvider;
import com.gem.baize.common.database.util.DataScopeContext;
import com.gem.baize.common.webmvc.context.RequestContextHolder;
import lombok.extern.slf4j.Slf4j;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.schema.Table;
import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;

/**
 * 数据权限拦截器（MyBatis-Plus InnerInterceptor）。
 * <p>
 * <b>为什么继承 {@code BaseMultiTableInnerInterceptor} 而不是直接用
 * {@code DataPermissionInterceptor}：</b>后者只把 {@code mappedStatementId} 交给
 * {@code DataPermissionHandler}，拿不到"这张表叫什么、有没有别名"，
 * 也就没法做"只对带 dept_id/create_by 的业务表生效"的判定——那正是本项目的核心要求。
 * 继承基类后 {@code buildTableExpression(Table, where, msId)} 能拿到每一张表，逐表决策。
 *
 * <p><b>与租户拦截器的叠加关系：</b>两者都是向 WHERE 追加 AND 条件的独立 InnerInterceptor，
 * 注册在同一个 {@code MybatisPlusInterceptor} 里按序执行即可叠加：
 * {@code tenant_id = X AND dept_id IN (...)}。互不感知、互不覆盖。
 *
 * <p><b>三层跳过机制（任一命中即跳过）：</b>
 * <ol>
 *   <li>总开关 {@code baize.data-scope.enabled=false}（默认关闭）；</li>
 *   <li>Mapper 方法上的 {@code @DataScope(skip=true)}；</li>
 *   <li>表不在白名单（白名单非空时）或表不含 dept_id/create_by 列。</li>
 * </ol>
 *
 * <p><b>fail-safe 取向：</b>默认关闭。若显式开启但"解析不出任何合法过滤条件"
 * （例如用户名下没有部门、自定义范围没配部门），则由 {@link CustomDataScopeHandler}
 * 下推 {@code 1 = 0}——宁可查不到，也不越权看到全部。
 */
@Slf4j
public class DataScopeInnerInterceptor extends BaseMultiTableInnerInterceptor {

    /** 带 dept_id 列、参与部门维度过滤的表（当前库中只有 sys_user；sys_role_dept 是关系表，不参与） */
    private static final Set<String> DEPT_TABLES = Set.of("sys_user");

    /** 带 create_by 列、参与"仅本人"过滤的表 */
    private static final Set<String> CREATE_BY_TABLES = Set.of(
            "sys_config", "sys_data_perm", "sys_dept", "sys_dict_data", "sys_dict_type",
            "sys_job", "sys_menu", "sys_notice", "sys_perm", "sys_post", "sys_role",
            "sys_tenant", "sys_user");

    private final DataScopeProperties properties;
    private final CustomDataScopeHandler handler;

    /**
     * 部门解析实现的<b>延迟</b>提供者。
     * <p>
     * 这里必须持有 Supplier 而不是直接持有 Bean 实例：{@code MybatisPlusInterceptor} 是
     * {@code SqlSessionFactory} 的前置依赖，而 {@code SysDataScopeDeptProvider} 自己又要注入
     * {@code SysUserMapper}（进而依赖 SqlSessionFactory）。若在构造期就 {@code getIfAvailable()}
     * 取实体，Spring 会在创建拦截器时被迫提前实例化 Provider，形成
     * {@code mybatisPlusInterceptor → sysDataScopeDeptProvider → sysUserMapper → sqlSessionFactory → mybatisPlusInterceptor}
     * 的循环依赖，服务直接启动失败。改为首次真正需要过滤时才解析，启动期不再触碰。
     */
    private final Supplier<DataScopeDeptProvider> deptProviderSupplier;

    /** 首次解析成功后缓存，避免每次 SQL 都走一次容器查找。 */
    private volatile DataScopeDeptProvider deptProviderCache;

    /**
     * 防重入标记：Provider 解析部门时自己会查 sys_user / sys_dept，而这两张表也在数据权限白名单里，
     * 若不拦截，它自己的查询又会触发本拦截器 → 再次解析部门 → 无限递归。
     * 标记期间一律跳过数据权限条件，只保留租户隔离。
     */
    private static final ThreadLocal<Boolean> RESOLVING = ThreadLocal.withInitial(() -> Boolean.FALSE);

    /**
     * Mapper 方法 → @DataScope 注解 的缓存。
     * 键为 {@code com.xxx.Mapper.selectPage} 形式，反射只做一次。
     */
    private final Map<String, DataScope> ANNOTATION_CACHE = new ConcurrentHashMap<>();

    public DataScopeInnerInterceptor(DataScopeProperties properties,
                                     CustomDataScopeHandler handler,
                                     Supplier<DataScopeDeptProvider> deptProviderSupplier) {
        this.properties = properties;
        this.handler = handler;
        this.deptProviderSupplier = deptProviderSupplier;
    }

    /**
     * 延迟取部门解析实现（首次调用时才真正从容器解析）。
     *
     * @return 未接入数据权限的服务返回 null，此时相关数据范围退化为"不可见"
     */
    private DataScopeDeptProvider deptProvider() {
        DataScopeDeptProvider cached = this.deptProviderCache;
        if (cached != null) {
            return cached;
        }
        DataScopeDeptProvider resolved = deptProviderSupplier == null ? null : deptProviderSupplier.get();
        if (resolved != null) {
            this.deptProviderCache = resolved;
        }
        return resolved;
    }

    /**
     * 在"防重入"保护下执行部门解析，避免 Provider 自身的查询再次触发数据权限。
     */
    private <T> T withReentrancyGuard(Supplier<T> action) {
        if (Boolean.TRUE.equals(RESOLVING.get())) {
            return null;
        }
        RESOLVING.set(Boolean.TRUE);
        try {
            return action.get();
        } finally {
            RESOLVING.set(Boolean.FALSE);
        }
    }

    /**
     * 查询前改写 SQL（这是本拦截器真正的入口）。
     * <p>
     * <b>为什么必须实现这个方法：</b>{@link BaseMultiTableInnerInterceptor} 只是 JSQLParser 的骨架，
     * 它自己<b>不会</b>被 MyBatis 调用。官方 {@code DataPermissionInterceptor} 正是靠实现
     * {@code beforeQuery/beforePrepare} 并调用 {@code parserSingle} 才生效的。
     * 早期版本只重写了 {@code buildTableExpression}，拦截器虽然注册进了 MybatisPlusInterceptor，
     * 但永远不会执行——数据权限静默失效（SQL 里看不到任何 dept 条件）。
     *
     * <p>这里刻意只改写 <b>SELECT</b>：数据权限的语义是"能看见哪些数据"，
     * UPDATE/DELETE 的越权防护应由 @RequiresPermission 与业务校验负责；
     * 若在这里也改写 UPDATE/DELETE，会影响逻辑删除的 update 语句（例如删除时补的 tenant 条件之外
     * 又追加 dept 条件，导致删除 0 行却不报错）。
     */
    @Override
    public void beforeQuery(org.apache.ibatis.executor.Executor executor,
                            org.apache.ibatis.mapping.MappedStatement ms,
                            Object parameter,
                            org.apache.ibatis.session.RowBounds rowBounds,
                            org.apache.ibatis.session.ResultHandler resultHandler,
                            org.apache.ibatis.mapping.BoundSql boundSql) throws java.sql.SQLException {
        if (!enabled()) {
            return;
        }
        // 用 parserSingle 改写 SELECT：内部会走 processSelect → processPlainSelect →
        // andExpression → buildTableExpression，逐表决定是否追加条件。
        // BoundSql.sql 是私有字段，MyBatis 没有 setSql；官方做法是通过 PluginUtils 的包装器写回
        // （MPBoundSql.sql(String) 内部用 MetaObject 反射赋值）。
        com.baomidou.mybatisplus.core.toolkit.PluginUtils.MPBoundSql mpBoundSql =
                com.baomidou.mybatisplus.core.toolkit.PluginUtils.mpBoundSql(boundSql);
        mpBoundSql.sql(parserSingle(mpBoundSql.sql(), ms.getId()));
    }

    /**
     * 解析 SELECT 语句并逐表追加数据权限条件。
     * <p>
     * <b>必须重写：</b>{@code JsqlParserSupport.processSelect} 的默认实现是直接抛
     * {@code UnsupportedOperationException}，它只是给子类留的钩子。不重写的话
     * {@code parserSingle} 一进来就炸（实测报 "UnsupportedOperationException" 且接口 500）。
     *
     * <p>与官方 {@code DataPermissionInterceptor} 的差异：官方通过 {@code DataPermissionHandler}
     * 统一回调拿条件；本项目继承了 {@code BaseMultiTableInnerInterceptor} 的
     * {@code processSelectBody} 流程，逐表回调 {@link #buildTableExpression}，
     * 因此这里只需把 {@code Select} 交给父类的 {@code processSelectBody} 即可。
     * 该参数 {@code mappedStatementId} 是 {@code parserSingle} 传下来的 {@code ms.getId()}。
     */
    @Override
    protected void processSelect(net.sf.jsqlparser.statement.select.Select select,
                                 int index,
                                 String sql,
                                 Object obj) {
        processSelectBody(select, (String) obj);
    }

    /**
     * 对单张表生成数据权限条件。
     *
     * @param table 当前处理的表（含别名信息）
     * @param where 该表已有的 WHERE 表达式，可为 null
     * @param mappedStatementId Mapper 方法全限定名
     * @return 追加数据权限后的表达式；null 或原值表示不过滤
     */
    @Override
    public Expression buildTableExpression(Table table, Expression where, String mappedStatementId) {
        if (!enabled() || table == null) {
            return null;
        }

        // 防重入：部门解析自身发出的查询（sys_user / sys_dept）不再叠加数据权限
        if (Boolean.TRUE.equals(RESOLVING.get())) {
            return null;
        }

        DataScope annotation = resolveAnnotation(mappedStatementId);
        if (annotation != null && annotation.skip()) {
            return null;
        }

        String tableName = table.getName();
        if (StringUtils.isBlank(tableName)) {
            return null;
        }
        // 去掉库名限定符：baize-system.sys_user -> sys_user
        String bare = tableName.contains(".") ? tableName.substring(tableName.lastIndexOf('.') + 1) : tableName;
        bare = bare.replace("\"", "");

        if (!isTargetTable(bare)) {
            return null;
        }

        DataScopeContext context = buildContext(bare, annotation, table.getAlias());
        if (context == null) {
            return null;
        }

        try {
            // 只返回「增量条件」，不要自己 AND 上 where：
            // 父类 andExpression() 已经做 `where AND buildTableExpression(...)`，
            // 这里若再把 where 拼进来，WHERE 会被重复追加（实测 SQL 出现两遍
            // `id = ? AND deleted = '0' AND tenant_id = 0`，参数个数不匹配直接 500）。
            return handler.buildWhere(null, context);
        } catch (RuntimeException e) {
            // 数据权限属于增强能力，解析失败不应让业务查询整体报错：记录后放行原条件
            log.error("数据权限条件生成失败，已跳过本次过滤. table={}, msId={}", bare, mappedStatementId, e);
            return null;
        }
    }

    /**
     * 是否对这张表生效：白名单非空时以白名单为准，否则按"表是否含 dept_id/create_by 列"判定。
     */
    private boolean isTargetTable(String tableName) {
        List<String> whitelist = properties == null ? null : properties.getTables();
        if (whitelist != null && !whitelist.isEmpty()) {
            return whitelist.contains(tableName);
        }
        return DEPT_TABLES.contains(tableName) || CREATE_BY_TABLES.contains(tableName);
    }

    /**
     * 组装本次调用的数据权限上下文。
     *
     * @return null 表示本次不过滤（无用户信息 / 全部数据权限 / 无部门解析实现）
     */
    private DataScopeContext buildContext(String tableName, DataScope annotation, net.sf.jsqlparser.expression.Alias alias) {
        String dataScope = RequestContextHolder.getDataScope();
        if (StringUtils.isBlank(dataScope) || DataScopeConstant.DATA_SCOPE_ALL.equals(dataScope)) {
            // 无数据范围信息 或 "1 全部"：不加条件
            return null;
        }

        String userId = RequestContextHolder.getUserId();
        if (StringUtils.isBlank(userId)) {
            // 无用户上下文（定时任务、内部调用）：放弃过滤，维持原有行为
            return null;
        }

        String aliasName = alias == null ? null : alias.getName();
        String deptAlias = annotation != null && StringUtils.isNotBlank(annotation.deptAlias())
                ? annotation.deptAlias() : aliasName;
        String userAlias = annotation != null && StringUtils.isNotBlank(annotation.userAlias())
                ? annotation.userAlias() : aliasName;

        DataScopeContext context = DataScopeContext.builder()
                .dataScope(dataScope)
                .userId(userId)
                .deptAlias(deptAlias)
                .userAlias(userAlias);

        // 仅"本部门"需要单个 deptId；"自定义"/"本部门及以下"需要部门集合
        if (DataScopeConstant.DATA_SCOPE_DEPT.equals(dataScope)) {
            String deptId = withReentrancyGuard(() -> {
                DataScopeDeptProvider provider = deptProvider();
                return provider == null ? null : provider.resolveDeptId(userId);
            });
            if (StringUtils.isBlank(deptId)) {
                // 解析不出部门：交给 handler 下推 1=0，而不是放行全表
                log.warn("用户[{}]所属部门解析失败，本部门数据权限将下推不可见条件", userId);
            }
            context.deptId(deptId);
        } else if (DataScopeConstant.DATA_SCOPE_CUSTOM.equals(dataScope)
                || DataScopeConstant.DATA_SCOPE_DEPT_AND_CHILD.equals(dataScope)) {
            List<String> deptIds = withReentrancyGuard(() -> {
                DataScopeDeptProvider provider = deptProvider();
                return provider == null ? List.<String>of() : provider.resolveDeptIds(userId, dataScope);
            });
            if (deptIds == null || deptIds.isEmpty()) {
                log.warn("用户[{}]在 dataScope={} 下未解析到任何部门，将下推不可见条件", userId, dataScope);
            }
            context.deptIds(deptIds == null ? List.of() : deptIds);
        }

        // 该表没有对应列时不下推（部门维度过滤只作用于带 dept_id 的表，反之亦然）
        if (!isTargetTable(tableName)) {
            return null;
        }
        return context;
    }

    private boolean enabled() {
        return properties != null && properties.isEnabled();
    }

    /**
     * 解析 Mapper 方法上的 {@code @DataScope} 注解（方法级优先，其次类级）。
     * 解析失败（类不存在 / 无此方法）时缓存一个"无注解"结果，避免每次反射。
     */
    private DataScope resolveAnnotation(String mappedStatementId) {
        if (StringUtils.isBlank(mappedStatementId)) {
            return null;
        }
        return ANNOTATION_CACHE.computeIfAbsent(mappedStatementId, this::lookupAnnotation);
    }

    private DataScope lookupAnnotation(String msId) {
        int idx = msId.lastIndexOf('.');
        if (idx <= 0) {
            return null;
        }
        String className = msId.substring(0, idx);
        String methodName = msId.substring(idx + 1);
        try {
            Class<?> clazz = Class.forName(className);
            DataScope typeLevel = clazz.getAnnotation(DataScope.class);
            for (Method m : clazz.getMethods()) {
                if (Objects.equals(m.getName(), methodName)) {
                    DataScope methodLevel = m.getAnnotation(DataScope.class);
                    return methodLevel != null ? methodLevel : typeLevel;
                }
            }
            return typeLevel;
        } catch (ClassNotFoundException | LinkageError e) {
            log.debug("无法解析 Mapper 类[{}]，按无 @DataScope 处理", className);
            return null;
        }
    }
}
