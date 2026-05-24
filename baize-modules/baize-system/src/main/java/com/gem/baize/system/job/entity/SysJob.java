package com.gem.baize.system.job.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.gem.baize.common.database.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 定时任务实体
 */
@EqualsAndHashCode(callSuper = true)
@Data
@TableName("sys_job")
public class SysJob extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /** 租户ID */
    @TableField(value = "tenant_id")
    private String tenantId;

    /** 任务名称 */
    @TableField(value = "job_name")
    private String jobName;

    /** 任务组名 */
    @TableField(value = "job_group")
    private String jobGroup;

    /** 调用目标字符串（类全路径#方法名） */
    @TableField(value = "invoke_target")
    private String invokeTarget;

    /** cron执行表达式 */
    @TableField(value = "cron_expression")
    private String cronExpression;

    /** 计划执行策略（0=立即执行 1=执行一次） */
    @TableField(value = "misfire_policy")
    private String misfirePolicy;

    /** 是否并发执行（0=允许 1=禁止） */
    @TableField(value = "concurrent")
    private String concurrent;

    /** 状态（0=正常 1=暂停） */
    @TableField(value = "status")
    private String status;

    /** 备注 */
    @TableField(value = "remark")
    private String remark;
}
