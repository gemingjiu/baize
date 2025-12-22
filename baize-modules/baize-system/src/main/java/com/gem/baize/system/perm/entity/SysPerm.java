package com.gem.baize.system.perm.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.gem.baize.common.database.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
@TableName("sys_perm")
public class SysPerm extends BaseEntity {
    /**
     * 租户ID
     */
    @TableField(value = "tenant_id")
    private String tenantId;

    /**
     * 父级ID
     */
    @TableField(value = "parent_id")
    private String parentId;
    /**
     * 权限编码
     */
    @TableField(value = "perm_code")
    private String permCode;
    /**
     * 权限名称
     */
    @TableField(value = "perm_name")
    private String permName;

}
