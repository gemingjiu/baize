package com.gem.baize.system.menu.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gem.baize.api.system.menu.domain.dto.SysMenuDto;
import com.gem.baize.common.core.exception.model.ParamException;
import com.gem.baize.common.core.model.vo.ApiResult;
import com.gem.baize.system.menu.entity.SysMenu;
import com.gem.baize.system.menu.service.SysMenuService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/system/menu")
public class SysMenuController {
    @Autowired
    private SysMenuService sysMenuService;

    @GetMapping("/{id}")
    @Operation(summary = "根据ID获取菜单", description = "根据ID查询菜单信息")
    public ApiResult<SysMenuDto> getById(@PathVariable String id) {
        if (StringUtils.isBlank(id)) {
            throw new ParamException("请求参数id不能为空");
        }
        return ApiResult.ok(sysMenuService.getById(id));
    }

    @PostMapping
    @Operation(summary = "创建菜单")
    public ApiResult<Integer> create(@Valid @RequestBody SysMenuDto dto) {
        sysMenuService.create(dto);
        return ApiResult.ok();
    }

    @PutMapping("/{id}")
    @Operation(summary = "更新菜单")
    public ApiResult<Void> update(@PathVariable String id, @Valid @RequestBody SysMenuDto dto) {
        // 双重验证
        if (!id.equals(dto.getId())) {
            throw new ParamException("请求参数id不一致");
        }

        sysMenuService.updateById(dto);
        return ApiResult.ok();
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除菜单")
    public ApiResult<Void> delete(@PathVariable String id) {
        sysMenuService.removeById(id);
        return ApiResult.ok();
    }

    @PostMapping("/page")
    @Operation(summary = "分页查询菜单")
    public ApiResult<Page<SysMenuDto>> page(@RequestParam(value = "pageNum", defaultValue = "1") int pageNum, @RequestParam(value = "pageSize", defaultValue = "10") int pageSize, @Valid @RequestBody SysMenuDto dto) {
        Page<SysMenu> page = new Page<>(pageNum, pageSize);
        return ApiResult.ok(sysMenuService.page(page, dto));
    }
}
