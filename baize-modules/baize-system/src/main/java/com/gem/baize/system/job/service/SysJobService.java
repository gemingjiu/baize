package com.gem.baize.system.job.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.gem.baize.system.job.entity.SysJob;

import java.util.List;

public interface SysJobService extends IService<SysJob> {

    /**
     * 根据状态查询任务列表
     */
    List<SysJob> selectJobList(String status);

    /**
     * 执行任务
     */
    void run(SysJob job) throws Exception;

    /**
     * 暂停任务
     */
    void pauseJob(String jobId);

    /**
     * 恢复任务
     */
    void resumeJob(String jobId);
}
