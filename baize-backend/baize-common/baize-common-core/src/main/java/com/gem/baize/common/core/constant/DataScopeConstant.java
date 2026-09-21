package com.gem.baize.common.core.constant;

/**
 * 数据权限范围常量
 */
public final class DataScopeConstant {

    /**
     * 全部数据权限
     */
    public static final String DATA_SCOPE_ALL = "1";

    /**
     * 自定数据权限
     */
    public static final String DATA_SCOPE_CUSTOM = "2";

    /**
     * 本部门数据权限
     */
    public static final String DATA_SCOPE_DEPT = "3";

    /**
     * 本部门及以下数据权限
     */
    public static final String DATA_SCOPE_DEPT_AND_CHILD = "4";

    /**
     * 仅本人数据权限
     */
    public static final String DATA_SCOPE_SELF = "5";

    private DataScopeConstant() {
        throw new UnsupportedOperationException("常量类禁止实例化");
    }
}
