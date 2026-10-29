package com.gem.baize.system.role.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.gem.baize.common.database.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
@TableName("sys_role")
public class SysRole extends BaseEntity {
    /**
     * 角色名称
     */
    @TableField(value = "role_name")
    private String roleName;
    /**
     * 角色权限字符串
     */
    @TableField(value = "role_code")
    private String roleCode;
    /**
     * 数据范围,1：全部数据权限 2：自定数据权限 3：本部门数据权限 4：本部门及以下数据权限 5：仅本人数据权限
     */
    @TableField(value = "data_scope")
    private String dataScope;
    /**
     * 角色类型,0：平台角色 1：租户角色
     */
    @TableField(value = "role_type")
    private String roleType;
    /**
     * 是否内置角色,0：非内置 1：内置（内置不可删除）
     */
    @TableField(value = "is_builtin")
    private String isBuiltin;
    /**
     * 菜单树选择项是否关联显示
     */
    @TableField(value = "menu_check_strictly")
    private String menuCheckStrictly;
    /**
     * 部门树选择项是否关联显示
     */
    @TableField(value = "dept_check_strictly")
    private String deptCheckStrictly;
}
