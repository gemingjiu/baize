package com.gem.baize.system.user.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.gem.baize.common.database.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 用户角色关联
 */
@EqualsAndHashCode(callSuper = true)
@Data
@TableName("sys_user_role")
public class SysUserRole extends BaseEntity {

    /**
     * 用户ID
     */
    @TableField("user_id")
    private String userId;

    /**
     * 角色ID
     */
    @TableField("role_id")
    private String roleId;

    /**
     * 租户ID
     */
    @TableField("tenant_id")
    private String tenantId;
}
