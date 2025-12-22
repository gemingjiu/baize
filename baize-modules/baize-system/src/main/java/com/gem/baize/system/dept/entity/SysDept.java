package com.gem.baize.system.dept.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.gem.baize.common.database.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
@TableName("sys_dept")
public class SysDept extends BaseEntity {
    /**
     * 租户ID
     */
    @TableField(value = "tenant_id")
    private String tenantId;
    /**
     * 父部门id
     */
    @TableField(value = "parent_id")
    private String parentId;
    /**
     * 部门名称
     */
    @TableField(value = "dept_name")
    private String deptName;
    /**
     * 负责人
     */
    @TableField(value = "leader")
    private String leader;
    /**
     * 电话
     */
    @TableField(value = "phone")
    private String phone;
    /**
     * 邮箱
     */
    @TableField(value = "email")
    private String email;
}
