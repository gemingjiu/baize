package com.gem.baize.system.operlog.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gem.baize.api.system.operlog.domain.dto.SysOperLogDto;
import com.gem.baize.common.core.model.vo.ApiResult;
import com.gem.baize.system.operlog.entity.SysOperLog;
import com.gem.baize.system.operlog.service.SysOperLogService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 操作日志控制器
 */
@Tag(name = "操作日志管理")
@RestController
@RequestMapping("/system/operlog")
public class SysOperLogController {

    @Autowired
    private SysOperLogService sysOperLogService;

    @GetMapping("/{id}")
    @Operation(summary = "根据ID获取操作日志详情", description = "根据ID查询操作日志信息")
    public ApiResult<SysOperLogDto> getById(@PathVariable String id) {
        return ApiResult.ok(sysOperLogService.getById(id));
    }

    @PostMapping("/page")
    @Operation(summary = "分页查询操作日志", description = "分页查询操作日志列表")
    public ApiResult<Page<SysOperLogDto>> page(
            @RequestParam(value = "current", defaultValue = "1") int current,
            @RequestParam(value = "size", defaultValue = "10") int size,
            @RequestBody(required = false) SysOperLogDto dto) {
        Page<SysOperLog> page = new Page<>(current, size);
        return ApiResult.ok(sysOperLogService.page(page, dto));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除操作日志", description = "根据ID删除操作日志")
    public ApiResult<Void> delete(@PathVariable String id) {
        sysOperLogService.removeById(id);
        return ApiResult.ok();
    }

    @DeleteMapping("/clean")
    @Operation(summary = "清空操作日志", description = "清空所有操作日志")
    public ApiResult<Void> clean() {
        sysOperLogService.cleanOperLog();
        return ApiResult.ok();
    }
}
