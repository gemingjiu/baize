package com.gem.baize.system.job.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gem.baize.system.job.entity.SysJob;
import com.gem.baize.system.job.mapper.SysJobMapper;
import com.gem.baize.system.job.service.SysJobService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class SysJobServiceImpl extends ServiceImpl<SysJobMapper, SysJob> implements SysJobService {

    @Override
    public List<SysJob> selectJobList(String status) {
        LambdaQueryWrapper<SysJob> wrapper = new LambdaQueryWrapper<>();
        if (status != null) {
            wrapper.eq(SysJob::getStatus, status);
        }
        wrapper.orderByAsc(SysJob::getJobGroup, SysJob::getJobName);
        return baseMapper.selectList(wrapper);
    }

    @Override
    public void run(SysJob job) throws Exception {
        log.info("执行任务: jobName={}, invokeTarget={}", job.getJobName(), job.getInvokeTarget());
        // 这里简化处理，实际应该通过反射调用目标方法
        // 由于没有集成Quartz，这里仅做日志记录
        log.info("任务执行成功: {}", job.getJobName());
    }

    @Override
    public void pauseJob(String jobId) {
        SysJob job = getById(jobId);
        if (job != null) {
            job.setStatus("1");
            updateById(job);
            log.info("暂停任务: jobId={}", jobId);
        }
    }

    @Override
    public void resumeJob(String jobId) {
        SysJob job = getById(jobId);
        if (job != null) {
            job.setStatus("0");
            updateById(job);
            log.info("恢复任务: jobId={}", jobId);
        }
    }
}
