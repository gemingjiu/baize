package com.gem.baize.api.system.role.domain.dto;

import lombok.Data;

@Data
public class SysRoleDto {
    /**
     * ID
     */
    private String id;
    /**
     * 租户ID
     */
    private String tenantId;
    /**
     * 角色名称
     */
    private String roleName;
    /**
     * 角色权限字符串
     */
    private String roleCode;
    /**
     * 数据范围,1：全部数据权限 2：自定数据权限 3：本部门数据权限 4：本部门及以下数据权限 5：仅本人数据权限
     */
    private String dataScope;
    /**
     * 角色类型,0：平台角色 1：租户角色
     */
    private String roleType;
    /**
     * 是否内置角色,0：非内置 1：内置（内置不可删除）
     */
    private String isBuiltin;
    /**
     * 菜单树选择项是否关联显示
     */
    private String menuCheckStrictly;
    /**
     * 部门树选择项是否关联显示
     */
    private String deptCheckStrictly;
}
