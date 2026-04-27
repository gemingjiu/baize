package com.gem.baize.system.menu.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.gem.baize.common.database.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 角色菜单关联
 */
@EqualsAndHashCode(callSuper = true)
@Data
@TableName("sys_role_menu")
public class SysRoleMenu extends BaseEntity {

    /**
     * 角色ID
     */
    @TableField("role_id")
    private String roleId;

    /**
     * 菜单ID
     */
    @TableField("menu_id")
    private String menuId;

    /**
     * 租户ID
     */
    @TableField("tenant_id")
    private String tenantId;
}
