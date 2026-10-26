package com.gem.baize.system.job.domain.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 定时任务出参对象
 * <p>
 * 只暴露对外契约需要的字段，避免把 SysJob 实体（version/deleted/createdBy/modifiedBy 等
 * ORM 审计字段）直接返回给调用方。
 */
@Data
public class SysJobDto implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 主键ID */
    private String id;

    /** 租户ID */
    private String tenantId;

    /** 任务名称 */
    @NotBlank(message = "任务名称不能为空")
    private String jobName;

    /** 任务组名 */
    @NotBlank(message = "任务组名不能为空")
    private String jobGroup;

    /** 调用目标文本（类全路径#方法名） */
    @NotBlank(message = "调用目标不能为空")
    private String invokeTarget;

    /** cron执行表达式 */
    @NotBlank(message = "cron执行表达式不能为空")
    private String cronExpression;

    /** 计划执行策略（0=立即执行 1=执行一次） */
    private String misfirePolicy;

    /** 是否并发执行（0=允许 1=禁止） */
    private String concurrent;

    /** 状态（0=正常 1=暂停） */
    private String status;

    /** 备注 */
    private String remark;

    /** 创建时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdTime;
}
