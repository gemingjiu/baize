package com.gem.baize.system.job.quartz;

import com.gem.baize.system.job.entity.SysJob;
import com.gem.baize.system.job.service.SysJobService;
import lombok.extern.slf4j.Slf4j;
import org.quartz.*;
import org.quartz.impl.matchers.GroupMatcher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 定时任务调度管理器：将数据库启用的任务同步到 Quartz，
 * 支持新增/修改/暂停/恢复的自动同步（每 60s 刷新一次）
 */
@Slf4j
@Component
public class SysJobScheduler implements ApplicationRunner {

    @Autowired
    private Scheduler scheduler;

    @Autowired
    private SysJobService sysJobService;

    @Value("${baize.job.scheduler-enabled:true}")
    private boolean schedulerEnabled;

    /** 已注册到 Quartz 的任务ID集合，用于差异同步 */
    private final Set<String> registeredJobIds = new HashSet<>();

    @Override
    public void run(ApplicationArguments args) {
        if (!schedulerEnabled) {
            log.info("[job] 定时调度已禁用（baize.job.scheduler-enabled=false）");
            return;
        }
        syncJobs();
    }

    @Scheduled(fixedDelay = 60000, initialDelay = 30000)
    public void refresh() {
        if (!schedulerEnabled) {
            return;
        }
        syncJobs();
    }

    /**
     * 同步数据库任务到 Quartz：新增/变更的任务注册，失效的任务移除
     */
    private synchronized void syncJobs() {
        try {
            List<SysJob> jobs = sysJobService.selectJobList("0");
            Set<String> dbJobIds = new HashSet<>();

            for (SysJob job : jobs) {
                if (job.getId() == null || job.getCronExpression() == null || job.getCronExpression().isBlank()) {
                    continue;
                }
                dbJobIds.add(job.getId());
                try {
                    if (registeredJobIds.contains(job.getId())) {
                        rescheduleIfChanged(job);
                    } else {
                        registerJob(job);
                        registeredJobIds.add(job.getId());
                    }
                } catch (Exception e) {
                    log.error("[job] 任务注册失败, jobId={}, jobName={}", job.getId(), job.getJobName(), e);
                }
            }

            // 移除数据库中已删除/停用的任务
            Set<String> toRemove = new HashSet<>(registeredJobIds);
            toRemove.removeAll(dbJobIds);
            for (String jobId : toRemove) {
                unregisterJob(jobId);
                registeredJobIds.remove(jobId);
            }
        } catch (Exception e) {
            log.error("[job] 任务同步异常", e);
        }
    }

    private void registerJob(SysJob job) throws SchedulerException {
        JobKey jobKey = buildJobKey(job);
        JobDetail jobDetail = JobBuilder.newJob(SysJobQuartzJob.class)
                .withIdentity(jobKey)
                .usingJobData(SysJobQuartzJob.JOB_ID_KEY, job.getId())
                .storeDurably()
                .build();
        Trigger trigger = buildTrigger(job);
        scheduler.scheduleJob(jobDetail, trigger);
        log.info("[job] 任务已注册: {} ({}) cron={}", job.getJobName(), job.getId(), job.getCronExpression());
    }

    private void rescheduleIfChanged(SysJob job) throws SchedulerException {
        JobKey jobKey = buildJobKey(job);
        if (!scheduler.checkExists(jobKey)) {
            registeredJobIds.remove(job.getId());
            registerJob(job);
            registeredJobIds.add(job.getId());
            return;
        }
        // cron 变化则重建触发器
        Trigger oldTrigger = scheduler.getTrigger(TriggerKey.triggerKey(job.getId(), job.getJobGroup()));
        if (oldTrigger == null) {
            scheduler.scheduleJob(buildTrigger(job));
            return;
        }
        String oldCron = ((CronTrigger) oldTrigger).getCronExpression();
        if (!job.getCronExpression().equals(oldCron)) {
            scheduler.rescheduleJob(oldTrigger.getKey(), buildTrigger(job));
            log.info("[job] 任务 cron 已更新: {} {}", job.getJobName(), job.getCronExpression());
        }
    }

    private void unregisterJob(String jobId) throws SchedulerException {
        // 按 jobId 反查 jobKey 比较繁琐，这里通过存储的 JobDetail 移除
        // 简化：遍历删除匹配 jobData 的任务
        for (JobKey jobKey : scheduler.getJobKeys(GroupMatcher.anyGroup())) {
            JobDetail detail = scheduler.getJobDetail(jobKey);
            if (detail != null && jobId.equals(detail.getJobDataMap().getString(SysJobQuartzJob.JOB_ID_KEY))) {
                scheduler.deleteJob(jobKey);
                log.info("[job] 任务已移除: jobId={}", jobId);
            }
        }
    }

    private JobKey buildJobKey(SysJob job) {
        return JobKey.jobKey(job.getId(), job.getJobGroup());
    }

    private Trigger buildTrigger(SysJob job) {
        return TriggerBuilder.newTrigger()
                .withIdentity(job.getId(), job.getJobGroup())
                .withSchedule(CronScheduleBuilder.cronSchedule(job.getCronExpression()))
                .build();
    }
}
