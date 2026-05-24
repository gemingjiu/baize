package com.gem.baize.system.job.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gem.baize.common.core.model.vo.ApiResult;
import com.gem.baize.system.job.entity.SysJob;
import com.gem.baize.system.job.service.SysJobService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 定时任务Controller
 */
@Tag(name = "定时任务管理")
@RestController
@RequestMapping("/system/job")
public class SysJobController {

    @Autowired
    private SysJobService sysJobService;

    @GetMapping("/list")
    @Operation(summary = "获取定时任务列表")
    public ApiResult<List<SysJob>> list(@RequestParam(value = "status", required = false) String status,
                                         @RequestParam(value = "pageNum", defaultValue = "1") int pageNum,
                                         @RequestParam(value = "pageSize", defaultValue = "10") int pageSize) {
        Page<SysJob> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<SysJob> wrapper = new LambdaQueryWrapper<>();
        if (status != null) {
            wrapper.eq(SysJob::getStatus, status);
        }
        wrapper.orderByAsc(SysJob::getCreatedTime);
        return ApiResult.ok(sysJobService.page(page, wrapper).getRecords());
    }

    @GetMapping("/{id}")
    @Operation(summary = "根据ID获取定时任务")
    public ApiResult<SysJob> getById(@PathVariable String id) {
        return ApiResult.ok(sysJobService.getById(id));
    }

    @PostMapping
    @Operation(summary = "新增定时任务")
    public ApiResult<Void> create(@RequestBody SysJob sysJob) {
        sysJobService.save(sysJob);
        return ApiResult.ok();
    }

    @PutMapping
    @Operation(summary = "修改定时任务")
    public ApiResult<Void> update(@RequestBody SysJob sysJob) {
        sysJobService.updateById(sysJob);
        return ApiResult.ok();
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除定时任务")
    public ApiResult<Void> delete(@PathVariable String id) {
        sysJobService.removeById(id);
        return ApiResult.ok();
    }

    @PutMapping("/run/{id}")
    @Operation(summary = "立即执行任务")
    public ApiResult<Void> run(@PathVariable String id) throws Exception {
        SysJob job = sysJobService.getById(id);
        if (job != null) {
            sysJobService.run(job);
        }
        return ApiResult.ok();
    }

    @PutMapping("/pause/{id}")
    @Operation(summary = "暂停任务")
    public ApiResult<Void> pause(@PathVariable String id) {
        sysJobService.pauseJob(id);
        return ApiResult.ok();
    }

    @PutMapping("/resume/{id}")
    @Operation(summary = "恢复任务")
    public ApiResult<Void> resume(@PathVariable String id) {
        sysJobService.resumeJob(id);
        return ApiResult.ok();
    }
}
