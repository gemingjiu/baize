package com.gem.baize.system.job.quartz;

import com.gem.baize.system.job.entity.SysJob;
import com.gem.baize.system.job.service.SysJobService;
import lombok.extern.slf4j.Slf4j;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.quartz.QuartzJobBean;

/**
 * Quartz 任务适配器：从 JobDataMap 取任务ID，加载数据库任务并执行
 */
@Slf4j
public class SysJobQuartzJob extends QuartzJobBean {

    public static final String JOB_ID_KEY = "jobId";

    @Autowired
    private SysJobService sysJobService;

    @Override
    protected void executeInternal(JobExecutionContext context) {
        JobDataMap dataMap = context.getMergedJobDataMap();
        String jobId = dataMap.getString(JOB_ID_KEY);
        if (jobId == null) {
            log.warn("[job] Quartz 任务缺少 jobId，跳过执行");
            return;
        }
        try {
            SysJob job = sysJobService.getById(jobId);
            if (job == null) {
                log.warn("[job] 任务不存在或已删除，jobId={}", jobId);
                return;
            }
            if (!"0".equals(job.getStatus())) {
                log.debug("[job] 任务已暂停，跳过执行: {}", job.getJobName());
                return;
            }
            sysJobService.run(job);
        } catch (Exception e) {
            log.error("[job] 任务执行异常, jobId={}", jobId, e);
        }
    }
}
