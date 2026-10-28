package com.gem.baize.common.core.constant;

/**
 * 权限码常量（P1-M：替换 @RequiresPermission 字面量）。
 * <p>与 sys_menu.perms 种子数据一一对应；新增权限码时两处需同步。
 */
public final class PermCodes {
    private PermCodes() {}

    // system:user
    public static final String USER_LIST = "system:user:list";
    public static final String USER_QUERY = "system:user:query";
    public static final String USER_ADD = "system:user:add";
    public static final String USER_EDIT = "system:user:edit";
    public static final String USER_REMOVE = "system:user:remove";
    public static final String USER_RESET_PWD = "system:user:resetPwd";
    // system:role
    public static final String ROLE_LIST = "system:role:list";
    public static final String ROLE_QUERY = "system:role:query";
    public static final String ROLE_ADD = "system:role:add";
    public static final String ROLE_EDIT = "system:role:edit";
    public static final String ROLE_REMOVE = "system:role:remove";
    // system:menu
    public static final String MENU_LIST = "system:menu:list";
    public static final String MENU_QUERY = "system:menu:query";
    public static final String MENU_ADD = "system:menu:add";
    public static final String MENU_EDIT = "system:menu:edit";
    public static final String MENU_REMOVE = "system:menu:remove";
    // system:dept
    public static final String DEPT_LIST = "system:dept:list";
    public static final String DEPT_QUERY = "system:dept:query";
    public static final String DEPT_ADD = "system:dept:add";
    public static final String DEPT_EDIT = "system:dept:edit";
    public static final String DEPT_REMOVE = "system:dept:remove";
    // system:dict
    public static final String DICT_LIST = "system:dict:list";
    public static final String DICT_QUERY = "system:dict:query";
    public static final String DICT_ADD = "system:dict:add";
    public static final String DICT_EDIT = "system:dict:edit";
    public static final String DICT_REMOVE = "system:dict:remove";
    // system:config
    public static final String CONFIG_LIST = "system:config:list";
    public static final String CONFIG_QUERY = "system:config:query";
    public static final String CONFIG_ADD = "system:config:add";
    public static final String CONFIG_EDIT = "system:config:edit";
    public static final String CONFIG_REMOVE = "system:config:remove";
    // system:post
    public static final String POST_LIST = "system:post:list";
    public static final String POST_QUERY = "system:post:query";
    public static final String POST_ADD = "system:post:add";
    public static final String POST_EDIT = "system:post:edit";
    public static final String POST_REMOVE = "system:post:remove";
    // system:notice
    public static final String NOTICE_LIST = "system:notice:list";
    public static final String NOTICE_QUERY = "system:notice:query";
    public static final String NOTICE_ADD = "system:notice:add";
    public static final String NOTICE_EDIT = "system:notice:edit";
    public static final String NOTICE_REMOVE = "system:notice:remove";
    // system:perm
    public static final String PERM_LIST = "system:perm:list";
    public static final String PERM_QUERY = "system:perm:query";
    public static final String PERM_ADD = "system:perm:add";
    public static final String PERM_EDIT = "system:perm:edit";
    public static final String PERM_REMOVE = "system:perm:remove";
    // system:tenant
    public static final String TENANT_LIST = "system:tenant:list";
    public static final String TENANT_QUERY = "system:tenant:query";
    public static final String TENANT_ADD = "system:tenant:add";
    public static final String TENANT_EDIT = "system:tenant:edit";
    public static final String TENANT_REMOVE = "system:tenant:remove";
    // system:job
    public static final String JOB_LIST = "system:job:list";
    public static final String JOB_QUERY = "system:job:query";
    public static final String JOB_ADD = "system:job:add";
    public static final String JOB_EDIT = "system:job:edit";
    public static final String JOB_REMOVE = "system:job:remove";
    // monitor
    public static final String OPERLOG_LIST = "monitor:operlog:list";
    public static final String OPERLOG_QUERY = "monitor:operlog:query";
    public static final String OPERLOG_REMOVE = "monitor:operlog:remove";
    public static final String LOGINLOG_LIST = "monitor:logininfor:list";
    public static final String LOGINLOG_QUERY = "monitor:logininfor:query";
    public static final String LOGINLOG_REMOVE = "monitor:logininfor:remove";
    public static final String ONLINE_QUERY = "monitor:online:query";
    public static final String ONLINE_LOGOUT = "monitor:online:logout";
    public static final String CACHE_QUERY = "monitor:cache:query";
    // tool
    public static final String GEN_CODE = "tool:gen:code";

    // 补充：导出/强制登出等
    public static final String USER_EXPORT = "system:user:export";
    public static final String USER_UPDATE_PWD = "system:user:updatePwd";
    public static final String USER_IMPORT = "system:user:import";
    public static final String ROLE_EXPORT = "system:role:export";
    public static final String OPERLOG_EXPORT = "monitor:operlog:export";
    public static final String LOGINLOG_EXPORT = "monitor:logininfor:export";
    public static final String ONLINE_FORCE_LOGOUT = "monitor:online:forceLogout";
    public static final String SERVER_QUERY = "monitor:server:query";
    public static final String GEN_QUERY = "tool:gen:query";
    public static final String GEN_EDIT = "tool:gen:edit";
    public static final String GEN_REMOVE = "tool:gen:remove";
    public static final String GEN_IMPORT = "tool:gen:import";
    public static final String GEN_PREVIEW = "tool:gen:preview";
    public static final String GEN_DOWNLOAD = "tool:gen:download";
}
