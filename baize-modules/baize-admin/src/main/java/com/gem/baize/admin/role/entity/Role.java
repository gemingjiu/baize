package com.gem.baize.admin.role.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.gem.baize.common.core.id.annotation.GeneratedId;
import com.gem.baize.common.datasource.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
@TableName("sys_role")
public class Role extends BaseEntity {
    /**
     * 业务ID
     */
    @GeneratedId(prefix = "role-")
    @TableField(value = "biz_id", fill = FieldFill.INSERT_UPDATE)
    private String bizId;
    /**
     * 租户ID
     */
    @TableField(value = "tenant_id")
    private Long tenantId;
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
     * 数据范围,1：全部数据权限 2：自定数据权限 3：本部门数据权限 4：本部门及以下数据权限
     */
    @TableField(value = "data_scope")
    private String dataScope;
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
