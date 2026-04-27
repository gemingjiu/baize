package com.gem.baize.system.perm.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.gem.baize.common.database.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 角色权限关联
 */
@EqualsAndHashCode(callSuper = true)
@Data
@TableName("sys_role_perm")
public class SysRolePerm extends BaseEntity {

    /**
     * 角色ID
     */
    @TableField("role_id")
    private String roleId;

    /**
     * 权限ID
     */
    @TableField("perm_id")
    private String permId;

    /**
     * 租户ID
     */
    @TableField("tenant_id")
    private String tenantId;
}
