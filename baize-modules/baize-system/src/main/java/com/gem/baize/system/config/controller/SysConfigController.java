package com.gem.baize.system.config.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gem.baize.api.system.config.domain.dto.SysConfigDto;
import com.gem.baize.common.core.annotation.RequiresPermission;
import com.gem.baize.common.core.exception.model.ParamException;
import com.gem.baize.common.core.model.vo.ApiResult;
import com.gem.baize.system.config.entity.SysConfig;
import com.gem.baize.system.config.service.SysConfigService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/system/config")
public class SysConfigController {

    @Autowired
    private SysConfigService sysConfigService;

    @GetMapping("/{id}")
    @Operation(summary = "根据ID获取参数配置")
    @RequiresPermission("system:config:query")
    public ApiResult<SysConfigDto> getById(@PathVariable String id) {
        if (StringUtils.isBlank(id)) {
            throw new ParamException("请求参数id不能为空");
        }
        return ApiResult.ok(sysConfigService.getById(id));
    }

    @PostMapping
    @Operation(summary = "创建参数配置")
    @RequiresPermission("system:config:add")
    public ApiResult<Integer> create(@Valid @RequestBody SysConfigDto dto) {
        sysConfigService.create(dto);
        return ApiResult.ok();
    }

    @PutMapping("/{id}")
    @Operation(summary = "更新参数配置")
    @RequiresPermission("system:config:edit")
    public ApiResult<Void> update(@PathVariable String id, @Valid @RequestBody SysConfigDto dto) {
        if (!id.equals(dto.getId())) {
            throw new ParamException("请求参数id不一致");
        }
        sysConfigService.updateById(dto);
        return ApiResult.ok();
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除参数配置")
    @RequiresPermission("system:config:remove")
    public ApiResult<Void> delete(@PathVariable String id) {
        sysConfigService.removeById(id);
        return ApiResult.ok();
    }

    @PostMapping("/page")
    @Operation(summary = "分页查询参数配置")
    @RequiresPermission("system:config:list")
    public ApiResult<Page<SysConfigDto>> page(@RequestParam(value = "current", defaultValue = "1") int current,
                                               @RequestParam(value = "size", defaultValue = "10") int size,
                                               @Valid @RequestBody SysConfigDto dto) {
        Page<SysConfig> page = new Page<>(current, size);
        return ApiResult.ok(sysConfigService.page(page, dto));
    }

    @GetMapping("/key/{configKey}")
    @Operation(summary = "根据Key获取参数配置")
    @RequiresPermission("system:config:query")
    public ApiResult<SysConfigDto> getByConfigKey(@PathVariable String configKey) {
        if (StringUtils.isBlank(configKey)) {
            throw new ParamException("请求参数configKey不能为空");
        }
        return ApiResult.ok(sysConfigService.getByConfigKey(configKey));
    }
}
