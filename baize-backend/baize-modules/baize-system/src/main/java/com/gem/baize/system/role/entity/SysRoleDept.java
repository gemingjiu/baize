package com.gem.baize.system.role.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.gem.baize.common.database.entity.BaseRelationEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 角色部门关联（自定义数据权限 scope=2 的配置载体）
 * <p>
 * 对应表 {@code sys_role_dept}，结构为 (tenant_id, role_id, dept_id)，
 * 无独立主键——与 {@code sys_role_menu} 同构，因此继承 {@code BaseRelationEntity}。
 */
@EqualsAndHashCode(callSuper = true)
@Data
@TableName("sys_role_dept")
public class SysRoleDept extends BaseRelationEntity {

    /**
     * 角色ID
     */
    @TableField("role_id")
    private String roleId;

    /**
     * 部门ID
     */
    @TableField("dept_id")
    private String deptId;
}
