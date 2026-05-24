package com.gem.baize.system.loginlog.controller;

import com.gem.baize.common.core.annotation.RequiresPermission;
import com.gem.baize.common.core.model.vo.ApiResult;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gem.baize.api.system.loginlog.domain.dto.SysLoginLogDto;
import com.gem.baize.system.loginlog.entity.SysLoginLog;
import com.gem.baize.system.loginlog.service.SysLoginLogService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/system/loginLog")
public class SysLoginLogController {

    @Autowired
    private SysLoginLogService sysLoginLogService;

    @PostMapping("/record")
    @Operation(summary = "记录登录日志")
    public ApiResult<Void> record(@RequestBody SysLoginLogDto dto) {
        sysLoginLogService.recordLoginLog(dto);
        return ApiResult.ok();
    }

    @PostMapping("/page")
    @Operation(summary = "分页查询登录日志")
    @RequiresPermission("system:loginlog:list")
    public ApiResult<Page<SysLoginLogDto>> page(@RequestParam(value = "current", defaultValue = "1") int current,
                                                 @RequestParam(value = "size", defaultValue = "10") int size,
                                                 @Valid @RequestBody SysLoginLogDto dto) {
        Page<SysLoginLog> page = new Page<>(current, size);
        return ApiResult.ok(sysLoginLogService.page(page, dto));
    }
}
