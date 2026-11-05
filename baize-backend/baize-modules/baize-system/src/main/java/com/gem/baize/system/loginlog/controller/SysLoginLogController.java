package com.gem.baize.system.loginlog.controller;

import com.gem.baize.common.core.annotation.InnerAuth;
import com.gem.baize.common.core.annotation.Log;
import com.gem.baize.common.core.annotation.RequiresPermission;
import com.gem.baize.common.core.model.vo.ApiResult;
import com.gem.baize.common.core.model.vo.PageResult;
import com.gem.baize.common.database.convert.PageConvert;
import com.gem.baize.common.excel.ExcelUtil;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gem.baize.api.system.loginlog.domain.dto.SysLoginLogDto;
import com.gem.baize.system.loginlog.entity.SysLoginLog;
import com.gem.baize.system.loginlog.service.SysLoginLogService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedHashMap;
import java.util.List;

import com.gem.baize.common.core.constant.PermCodes;
@RestController
@RequestMapping("/system/loginLog")
public class SysLoginLogController {

    private static final int EXPORT_MAX_ROWS = 10000;

    private static final java.util.Map<String, String> LOGIN_STATUS_MAP = java.util.Map.of(
            "0", "成功", "1", "失败");

    @Autowired
    private SysLoginLogService sysLoginLogService;

    /**
     * 记录登录日志。
     * <p>
     * 内部写日志端点，仅允许 baize-auth 通过 Feign（SysLoginLogFeignClient）调用。
     * 加 {@code @InnerAuth} 用于把本端点纳入内部令牌校验：当前 baize.inner.token 已配置生效，
     * 外部直接调用会被拦截并返回"非法内部调用"。
     * 网关侧 /api/v1/system/loginLog/record 亦已在 denylist 中，属双保险。
     */
    @InnerAuth
    @PostMapping("/record")
    @Operation(summary = "记录登录日志")
    @Log(title = "登录日志", businessType = Log.BusinessType.INSERT)
    public ApiResult<Void> record(@RequestBody SysLoginLogDto dto) {
        sysLoginLogService.recordLoginLog(dto);
        return ApiResult.ok();
    }

    @PostMapping("/page")
    @Operation(summary = "分页查询登录日志")
    @RequiresPermission(PermCodes.LOGINLOG_LIST)
    public ApiResult<PageResult<SysLoginLogDto>> page(@RequestParam(value = "current", defaultValue = "1") int current,
                                                 @RequestParam(value = "size", defaultValue = "10") int size,
                                                 @Valid @RequestBody SysLoginLogDto dto) {
        Page<SysLoginLog> page = new Page<>(current, size);
        PageConvert<SysLoginLogDto> pageConvert = new PageConvert<>();
        return ApiResult.ok(pageConvert.toDto(sysLoginLogService.page(page, dto)));
    }

    /**
     * 导出登录日志，复用 page 的查询条件（不分页取全量，上限 EXPORT_MAX_ROWS 行）。
     */
    @PostMapping("/export")
    @Operation(summary = "导出登录日志", description = "按查询条件导出登录日志为 Excel")
    @RequiresPermission(PermCodes.LOGINLOG_EXPORT)
    @Log(title = "登录日志", businessType = Log.BusinessType.EXPORT)
    public void export(@RequestBody(required = false) SysLoginLogDto dto, HttpServletResponse response) {
        Page<SysLoginLog> page = new Page<>(1, EXPORT_MAX_ROWS);
        page.setSearchCount(false);
        List<SysLoginLogDto> records = sysLoginLogService.page(page, dto).getRecords();

        LinkedHashMap<String, java.util.function.Function<SysLoginLogDto, Object>> columns = new LinkedHashMap<>();
        columns.put("访问编号", SysLoginLogDto::getId);
        columns.put("用户名称", SysLoginLogDto::getUserName);
        columns.put("登录地址", SysLoginLogDto::getIpaddr);
        columns.put("登录地点", SysLoginLogDto::getLoginLocation);
        columns.put("浏览器", SysLoginLogDto::getBrowser);
        columns.put("操作系统", SysLoginLogDto::getOs);
        columns.put("登录状态", l -> LOGIN_STATUS_MAP.getOrDefault(l.getStatus(), "未知"));
        columns.put("操作信息", SysLoginLogDto::getMsg);
        columns.put("登录时间", SysLoginLogDto::getLoginTime);

        ExcelUtil.export(response, "登录日志", "登录日志",
                ExcelUtil.head(columns.keySet().toArray(new String[0])),
                ExcelUtil.buildRows(records, columns));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除登录日志")
    @RequiresPermission(PermCodes.LOGINLOG_REMOVE)
    @Log(title = "登录日志", businessType = Log.BusinessType.DELETE)
    public ApiResult<Void> delete(@PathVariable String id) {
        sysLoginLogService.removeById(id);
        return ApiResult.ok();
    }

    @DeleteMapping("/clean")
    @Operation(summary = "清空登录日志")
    @RequiresPermission(PermCodes.LOGINLOG_REMOVE)
    @Log(title = "登录日志", businessType = Log.BusinessType.CLEAN)
    public ApiResult<Void> clean() {
        sysLoginLogService.cleanLoginLog();
        return ApiResult.ok();
    }
}
