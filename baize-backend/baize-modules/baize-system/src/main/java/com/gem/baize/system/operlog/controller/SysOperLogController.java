package com.gem.baize.system.operlog.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gem.baize.api.system.operlog.domain.dto.SysOperLogDto;
import com.gem.baize.common.core.annotation.Log;
import com.gem.baize.common.core.annotation.RequiresPermission;
import com.gem.baize.common.core.model.vo.ApiResult;
import com.gem.baize.common.core.model.vo.PageResult;
import com.gem.baize.common.database.convert.PageConvert;
import com.gem.baize.common.excel.ExcelUtil;
import com.gem.baize.system.operlog.entity.SysOperLog;
import com.gem.baize.system.operlog.service.SysOperLogService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedHashMap;
import java.util.List;

import com.gem.baize.common.core.constant.PermCodes;
/**
 * 操作日志控制器
 */
@Tag(name = "操作日志管理")
@RestController
@RequestMapping("/system/operlog")
public class SysOperLogController {

    private static final int EXPORT_MAX_ROWS = 10000;

    private static final java.util.Map<Integer, String> BUSINESS_TYPE_MAP = java.util.Map.of(
            0, "其它", 1, "新增", 2, "修改", 3, "删除",
            4, "授权", 5, "导出", 6, "导入", 7, "强退", 8, "生成代码", 9, "清空数据");

    private static final java.util.Map<Integer, String> OPER_STATUS_MAP = java.util.Map.of(
            0, "成功", 1, "失败");

    @Autowired
    private SysOperLogService sysOperLogService;

    @GetMapping("/{id}")
    @Operation(summary = "根据ID获取操作日志详情", description = "根据ID查询操作日志信息")
    @RequiresPermission(PermCodes.OPERLOG_QUERY)
    public ApiResult<SysOperLogDto> getById(@PathVariable String id) {
        return ApiResult.ok(sysOperLogService.getById(id));
    }

    @PostMapping("/page")
    @Operation(summary = "分页查询操作日志", description = "分页查询操作日志列表")
    @RequiresPermission(PermCodes.OPERLOG_LIST)
    public ApiResult<PageResult<SysOperLogDto>> page(
            @RequestParam(value = "current", defaultValue = "1") int current,
            @RequestParam(value = "size", defaultValue = "10") int size,
            @RequestBody(required = false) SysOperLogDto dto) {
        Page<SysOperLog> page = new Page<>(current, size);
        PageConvert<SysOperLogDto> pageConvert = new PageConvert<>();
        return ApiResult.ok(pageConvert.toDto(sysOperLogService.page(page, dto)));
    }

    /**
     * 导出操作日志，复用 page 的查询条件（不分页取全量，上限 EXPORT_MAX_ROWS 行）。
     */
    @PostMapping("/export")
    @Operation(summary = "导出操作日志", description = "按查询条件导出操作日志为 Excel")
    @RequiresPermission(PermCodes.OPERLOG_EXPORT)
    @Log(title = "操作日志", businessType = Log.BusinessType.EXPORT)
    public void export(@RequestBody(required = false) SysOperLogDto dto, HttpServletResponse response) {
        Page<SysOperLog> page = new Page<>(1, EXPORT_MAX_ROWS);
        page.setSearchCount(false);
        List<SysOperLogDto> records = sysOperLogService.page(page, dto).getRecords();

        LinkedHashMap<String, java.util.function.Function<SysOperLogDto, Object>> columns = new LinkedHashMap<>();
        columns.put("日志编号", SysOperLogDto::getId);
        columns.put("操作模块", SysOperLogDto::getTitle);
        columns.put("业务类型", l -> BUSINESS_TYPE_MAP.getOrDefault(l.getBusinessType(), "其它"));
        columns.put("请求方式", SysOperLogDto::getRequestMethod);
        columns.put("操作人员", SysOperLogDto::getUserName);
        columns.put("操作地址", SysOperLogDto::getOperIp);
        columns.put("操作地点", SysOperLogDto::getOperLocation);
        columns.put("操作状态", l -> OPER_STATUS_MAP.getOrDefault(l.getStatus(), "未知"));
        columns.put("操作时间", SysOperLogDto::getOperTime);
        columns.put("消耗时间(ms)", SysOperLogDto::getCostTime);
        columns.put("错误消息", SysOperLogDto::getErrorMsg);

        ExcelUtil.export(response, "操作日志", "操作日志",
                ExcelUtil.head(columns.keySet().toArray(new String[0])),
                ExcelUtil.buildRows(records, columns));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除操作日志", description = "根据ID删除操作日志")
    @RequiresPermission(PermCodes.OPERLOG_REMOVE)
    @Log(title = "操作日志", businessType = Log.BusinessType.DELETE)
    public ApiResult<Void> delete(@PathVariable String id) {
        sysOperLogService.removeById(id);
        return ApiResult.ok();
    }

    @DeleteMapping("/clean")
    @Operation(summary = "清空操作日志", description = "清空所有操作日志")
    @RequiresPermission(PermCodes.OPERLOG_REMOVE)
    @Log(title = "操作日志", businessType = Log.BusinessType.CLEAN)
    public ApiResult<Void> clean() {
        sysOperLogService.cleanOperLog();
        return ApiResult.ok();
    }
}
