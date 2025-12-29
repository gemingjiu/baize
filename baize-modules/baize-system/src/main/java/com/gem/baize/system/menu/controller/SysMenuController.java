package com.gem.baize.system.menu.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gem.baize.api.system.menu.domain.dto.SysMenuDto;
import com.gem.baize.common.core.exception.model.BadRequestException;
import com.gem.baize.common.core.model.vo.Result;
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
    public Result<SysMenuDto> getById(@PathVariable String id) {
        if (StringUtils.isBlank(id)) {
            throw new BadRequestException("请求参数id不能为空");
        }
        return Result.success(sysMenuService.getById(id));
    }

    @PostMapping
    @Operation(summary = "创建菜单")
    public Result<Integer> create(@Valid @RequestBody SysMenuDto dto) {
        return Result.success(sysMenuService.create(dto));
    }

    @PutMapping("/{id}")
    @Operation(summary = "更新菜单")
    public Result<Void> update(@PathVariable String id, @Valid @RequestBody SysMenuDto dto) {
        // 双重验证
        if (!id.equals(dto.getId())) {
            throw new BadRequestException("请求参数id不一致");
        }

        sysMenuService.update(dto);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除菜单")
    public Result<Void> delete(@PathVariable String id) {
        sysMenuService.removeById(id);
        return Result.success();
    }

    @PostMapping("/page")
    @Operation(summary = "分页查询菜单")
    public Result<Page<SysMenuDto>> page(@RequestParam(value = "pageNum", defaultValue = "1") int pageNum, @RequestParam(value = "pageSize", defaultValue = "10") int pageSize, @Valid @RequestBody SysMenuDto dto) {
        Page<SysMenu> page = new Page<>(pageNum, pageSize);
        return Result.success(sysMenuService.page(page, dto));
    }
}
