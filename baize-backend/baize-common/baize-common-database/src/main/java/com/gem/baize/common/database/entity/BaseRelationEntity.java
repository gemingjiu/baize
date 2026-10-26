package com.gem.baize.common.database.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;

import java.io.Serializable;

/**
 * 关系表实体基类。
 * <p>
 * 关系表（sys_user_role / sys_role_menu / sys_role_perm 等）只有 tenant_id + 关联ID，
 * 没有 BaseEntity 里的 id/version/sort/deleted 等审计字段，因此单独抽象，不继承 BaseEntity。
 * <p>
 * tenant_id 由 {@code BaseEntityObjectHandler.insertFill} 自动填充（租户上下文缺失时兜底为平台租户 "0"），
 * 同时由 {@code CustomTenantHandler} 参与查询/更新/删除的租户过滤。
 */
@Data
public abstract class BaseRelationEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 租户ID
     */
    @TableField(value = "tenant_id", fill = FieldFill.INSERT)
    private String tenantId;
}
