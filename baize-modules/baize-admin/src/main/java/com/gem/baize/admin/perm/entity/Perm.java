package com.gem.baize.admin.perm.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.gem.baize.common.core.id.annotation.GeneratedId;
import com.gem.baize.common.datasource.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
@TableName("sys_perm")
public class Perm extends BaseEntity {
    /**
     * 业务ID
     */
    @GeneratedId(prefix = "perm-")
    @TableField(value = "biz_id", fill = FieldFill.INSERT_UPDATE)
    private String bizId;
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
