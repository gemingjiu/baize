package com.gem.baize.admin.dept.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.gem.baize.common.core.annotation.GeneratedId;
import com.gem.baize.common.datasource.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
@TableName("sys_dept")
public class Dept extends BaseEntity {
    /**
     * 业务ID
     */
    @GeneratedId(prefix = "dept-")
    @TableField(value = "biz_id", fill = FieldFill.INSERT_UPDATE)
    private String bizId;
    /**
     * 租户ID
     */
    @TableField(value = "tenant_id")
    private long tenantId;
    /**
     * 父部门id
     */
    @TableField(value = "parent_id")
    private long parentId;
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
