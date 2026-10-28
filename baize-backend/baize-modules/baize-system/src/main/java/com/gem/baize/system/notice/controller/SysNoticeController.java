package com.gem.baize.system.notice.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gem.baize.api.system.notice.domain.dto.SysNoticeDto;
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
@RestController
@RequestMapping("/system/notice")
public class SysNoticeController {

    @Autowired
    private SysNoticeService sysNoticeService;

    @GetMapping("/{id}")
    @Operation(summary = "根据ID获取公告")
    @RequiresPermission(PermCodes.NOTICE_QUERY)
    public ApiResult<SysNoticeDto> getById(@PathVariable String id) {
        return ApiResult.ok(sysNoticeService.getById(id));
    }

    @PostMapping
    @Operation(summary = "创建公告")
    @RequiresPermission(PermCodes.NOTICE_ADD)
    public ApiResult<Integer> create(@Valid @RequestBody SysNoticeDto dto) {
        sysNoticeService.create(dto);
        return ApiResult.ok();
    }

    @PutMapping("/{id}")
    @Operation(summary = "更新公告")
    @RequiresPermission(PermCodes.NOTICE_EDIT)
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
