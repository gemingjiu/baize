package com.gem.baize.system.job.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gem.baize.common.core.annotation.RequiresPermission;
import com.gem.baize.common.core.model.vo.ApiResult;
import com.gem.baize.common.core.model.vo.PageResult;
import com.gem.baize.common.database.convert.PageConvert;
import com.gem.baize.system.job.domain.dto.SysJobDto;
import com.gem.baize.system.job.entity.SysJob;
import com.gem.baize.system.job.service.SysJobService;
import com.gem.baize.system.job.service.impl.SysJobConvert;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
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

    @Autowired
    private SysJobConvert sysJobConvert;

    @GetMapping("/list")
    @Operation(summary = "获取定时任务列表")
    @RequiresPermission("system:job:list")
    public ApiResult<List<SysJobDto>> list(@RequestParam(value = "status", required = false) String status,
                                         @RequestParam(value = "pageNum", defaultValue = "1") int pageNum,
                                         @RequestParam(value = "pageSize", defaultValue = "10") int pageSize) {
        Page<SysJob> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<SysJob> wrapper = new LambdaQueryWrapper<>();
        if (status != null) {
            wrapper.eq(SysJob::getStatus, status);
        }
        wrapper.orderByAsc(SysJob::getCreatedTime);
        return ApiResult.ok(sysJobConvert.toDtoList(sysJobService.page(page, wrapper).getRecords()));
    }

    /**
     * 分页查询定时任务（与其余 13 个控制器统一的 PageResult 契约）。
     * <p>
     * 与 {@code GET /list} 的区别：本端点返回 total/size/current 等分页元数据，
     * 前端翻页条依赖 total 才能算对总页数；{@code /list} 只返回当前页记录，
     * 保留它是为了向后兼容既有调用方。
     */
    @PostMapping("/page")
    @Operation(summary = "分页查询定时任务")
    @RequiresPermission("system:job:list")
    public ApiResult<PageResult<SysJobDto>> page(
            @RequestParam(value = "current", defaultValue = "1") int current,
            @RequestParam(value = "size", defaultValue = "10") int size,
            @RequestBody(required = false) SysJobDto dto) {
        Page<SysJob> page = new Page<>(current, size);
        LambdaQueryWrapper<SysJob> wrapper = new LambdaQueryWrapper<>();
        if (dto != null && dto.getStatus() != null) {
            wrapper.eq(SysJob::getStatus, dto.getStatus());
        }
        if (dto != null && dto.getJobName() != null && !dto.getJobName().isBlank()) {
            wrapper.like(SysJob::getJobName, dto.getJobName());
        }
        wrapper.orderByAsc(SysJob::getCreatedTime);

        Page<SysJob> entityPage = sysJobService.page(page, wrapper);
        Page<SysJobDto> dtoPage = sysJobConvert.toDtoPage(entityPage);

        PageConvert<SysJobDto> pageConvert = new PageConvert<>();
        return ApiResult.ok(pageConvert.toDto(dtoPage));
    }

    @GetMapping("/{id}")
    @Operation(summary = "根据ID获取定时任务")
    @RequiresPermission("system:job:query")
    public ApiResult<SysJobDto> getById(@PathVariable String id) {
        return ApiResult.ok(sysJobConvert.toDto(sysJobService.getById(id)));
    }

    @PostMapping
    @Operation(summary = "新增定时任务")
    @RequiresPermission("system:job:add")
    public ApiResult<Void> create(@Valid @RequestBody SysJobDto dto) {
        sysJobService.save(sysJobConvert.toEntity(dto));
        return ApiResult.ok();
    }

    @PutMapping
    @Operation(summary = "修改定时任务")
    @RequiresPermission("system:job:edit")
    public ApiResult<Void> update(@Valid @RequestBody SysJobDto dto) {
        sysJobService.updateById(sysJobConvert.toEntity(dto));
        return ApiResult.ok();
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除定时任务")
    @RequiresPermission("system:job:remove")
    public ApiResult<Void> delete(@PathVariable String id) {
        sysJobService.removeById(id);
        return ApiResult.ok();
    }

    @PutMapping("/run/{id}")
    @Operation(summary = "立即执行任务")
    @RequiresPermission("system:job:edit")
    public ApiResult<Void> run(@PathVariable String id) throws Exception {
        SysJob job = sysJobService.getById(id);
        if (job != null) {
            sysJobService.run(job);
        }
        return ApiResult.ok();
    }

    @PutMapping("/pause/{id}")
    @Operation(summary = "暂停任务")
    @RequiresPermission("system:job:edit")
    public ApiResult<Void> pause(@PathVariable String id) {
        sysJobService.pauseJob(id);
        return ApiResult.ok();
    }

    @PutMapping("/resume/{id}")
    @Operation(summary = "恢复任务")
    @RequiresPermission("system:job:edit")
    public ApiResult<Void> resume(@PathVariable String id) {
        sysJobService.resumeJob(id);
        return ApiResult.ok();
    }
}
