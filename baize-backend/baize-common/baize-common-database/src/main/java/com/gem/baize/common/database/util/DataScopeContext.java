package com.gem.baize.common.database.util;

import java.util.ArrayList;
import java.util.List;

/**
 * 数据权限上下文：一次 Mapper 调用期间，把"当前用户的数据范围信息"从切面传给
 * {@code CustomDataScopeHandler}。
 * <p>
 * 为什么不用 ThreadLocal 单例：MyBatis-Plus 的拦截器拿不到 Spring 的调用栈，
 * 只能拿到 {@code mappedStatementId}（形如 {@code com.xxx.mapper.SysUserMapper.selectPage}）。
 * 因此这里仍然用 ThreadLocal 承载，但由切面在方法入口 set、finally 中 remove，
 * 生命周期严格限定在一次 Mapper 方法调用内。
 * <p>
 * 与 {@code RequestContextHolder} 的分工：后者存的是"请求级"信息（userId/tenantId/dataScope），
 * 本类是"Mapper 调用级"的增强信息（解析好的部门集合、是否跳过、表别名），二者不重复。
 */
public final class DataScopeContext {

    private static final ThreadLocal<DataScopeContext> HOLDER = new ThreadLocal<>();

    /** 角色数据范围取值，见 DataScopeConstant */
    private String dataScope;
    /** 当前用户ID（仅本人数据权限用） */
    private String userId;
    /** 用户所属部门ID（本部门数据权限用） */
    private String deptId;
    /** 部门ID集合：自定义范围=sys_role_dept 配置值；本部门及以下=部门子树 */
    private List<String> deptIds = new ArrayList<>();
    /** 是否跳过本次过滤 */
    private boolean skip;
    /** dept_id 列的表别名（JOIN 场景），可空 */
    private String deptAlias;
    /** create_by 列的表别名（JOIN 场景），可空 */
    private String userAlias;

    private DataScopeContext() {
    }

    public static DataScopeContext builder() {
        return new DataScopeContext();
    }

    /** 落盘到 ThreadLocal（由切面调用） */
    public void bind() {
        HOLDER.set(this);
    }

    /** 清理 ThreadLocal（切面 finally 中必须调用，防线程池复用串味） */
    public static void clear() {
        HOLDER.remove();
    }

    /** 取当前上下文，无则返回 null（表示"本次不过滤"） */
    public static DataScopeContext current() {
        return HOLDER.get();
    }

    public DataScopeContext dataScope(String dataScope) {
        this.dataScope = dataScope;
        return this;
    }

    public DataScopeContext userId(String userId) {
        this.userId = userId;
        return this;
    }

    public DataScopeContext deptId(String deptId) {
        this.deptId = deptId;
        return this;
    }

    public DataScopeContext deptIds(List<String> deptIds) {
        this.deptIds = deptIds == null ? new ArrayList<>() : new ArrayList<>(deptIds);
        return this;
    }

    public DataScopeContext skip(boolean skip) {
        this.skip = skip;
        return this;
    }

    public DataScopeContext deptAlias(String deptAlias) {
        this.deptAlias = deptAlias;
        return this;
    }

    public DataScopeContext userAlias(String userAlias) {
        this.userAlias = userAlias;
        return this;
    }

    public String getDataScope() {
        return dataScope;
    }

    public String getUserId() {
        return userId;
    }

    public String getDeptId() {
        return deptId;
    }

    public List<String> getDeptIds() {
        return deptIds;
    }

    public boolean isSkip() {
        return skip;
    }

    public String getDeptAlias() {
        return deptAlias;
    }

    public String getUserAlias() {
        return userAlias;
    }
}
