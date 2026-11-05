package com.gem.baize.system.notice.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gem.baize.api.system.notice.domain.dto.SysNoticeDto;
import com.gem.baize.common.core.annotation.Log;
import com.gem.baize.common.core.annotation.RequiresPermission;
import com.gem.baize.common.core.model.vo.ApiResult;
import com.gem.baize.common.core.model.vo.PageResult;
import com.gem.baize.common.database.convert.PageConvert;
import com.gem.baize.system.notice.entity.SysNotice;
import com.gem.baize.system.notice.service.SysNoticeService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.gem.baize.common.core.constant.PermCodes;
import java.util.List;

@RestController
@RequestMapping("/system/notice")
public class SysNoticeController {

    @Autowired
    private SysNoticeService sysNoticeService;

    /**
     * 最新启用的公告列表（登录即可见，2026-11-05 P3 公告展示闭环）。
     * <p>
     * 故意不加 {@code @RequiresPermission}：公告是面向全体登录用户的展示数据，
     * 普通用户在首页公告栏/登录弹窗也要看到，按 NOTICE_QUERY 设门槛会让公告
     * 展示闭环失效（对齐租户 /list、字典 /all 先例）。只返回启用中的公告，
     * 租户隔离由行级拦截器保证，不含敏感运营数据。
     */
    @GetMapping("/latest")
    @Operation(summary = "最新公告列表", description = "登录即可见：首页公告栏/登录弹窗数据源，默认前 5 条启用公告")
    public ApiResult<List<SysNoticeDto>> latest(
            @RequestParam(value = "limit", defaultValue = "5") int limit) {
        return ApiResult.ok(sysNoticeService.listLatest(limit));
    }

    @GetMapping("/{id}")
    @Operation(summary = "根据ID获取公告")
    @RequiresPermission(PermCodes.NOTICE_QUERY)
    public ApiResult<SysNoticeDto> getById(@PathVariable String id) {
        return ApiResult.ok(sysNoticeService.getById(id));
    }

    @PostMapping
    @Operation(summary = "创建公告")
    @RequiresPermission(PermCodes.NOTICE_ADD)
    @Log(title = "通知公告", businessType = Log.BusinessType.INSERT)
    public ApiResult<Integer> create(@Valid @RequestBody SysNoticeDto dto) {
        sysNoticeService.create(dto);
        return ApiResult.ok();
    }

    @PutMapping("/{id}")
    @Operation(summary = "更新公告")
    @RequiresPermission(PermCodes.NOTICE_EDIT)
    @Log(title = "通知公告", businessType = Log.BusinessType.UPDATE)
    public ApiResult<Void> update(@PathVariable String id, @Valid @RequestBody SysNoticeDto dto) {
        if (!id.equals(dto.getId())) {
            throw new com.gem.baize.common.core.exception.model.ParamException("请求参数id不一致");
        }
        sysNoticeService.updateById(dto);
        return ApiResult.ok();
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除公告")
    @RequiresPermission(PermCodes.NOTICE_REMOVE)
    @Log(title = "通知公告", businessType = Log.BusinessType.DELETE)
    public ApiResult<Void> delete(@PathVariable String id) {
        sysNoticeService.removeById(id);
        return ApiResult.ok();
    }

    @PostMapping("/page")
    @Operation(summary = "分页查询公告")
    @RequiresPermission(PermCodes.NOTICE_LIST)
    public ApiResult<PageResult<SysNoticeDto>> page(@RequestParam(value = "current", defaultValue = "1") int current,
                                               @RequestParam(value = "size", defaultValue = "10") int size,
                                               @RequestBody SysNoticeDto dto) {
        Page<SysNotice> page = new Page<>(current, size);
        PageConvert<SysNoticeDto> pageConvert = new PageConvert<>();
        return ApiResult.ok(pageConvert.toDto(sysNoticeService.page(page, dto)));
    }
}
