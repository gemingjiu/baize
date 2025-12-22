package com.gem.baize.system.menu.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gem.baize.system.menu.entity.SysMenu;
import com.gem.baize.system.menu.service.SysMenuService;
import com.gem.baize.api.system.menu.domain.dto.SysMenuDTO;
import com.gem.baize.common.core.exception.model.BadRequestException;
import com.gem.baize.common.core.model.dto.PageParam;
import com.gem.baize.common.core.model.vo.Result;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/system/menu")
public class SysMenuController {
    @Autowired
    private SysMenuService sysMenuService;

    @GetMapping("/{id}")
    @Operation(summary = "根据ID获取菜单", description = "根据ID查询菜单信息")
    public Result<SysMenuDTO> getById(@PathVariable String id) {
        if (StringUtils.isBlank(id)) {
            throw new BadRequestException("请求参数id不能为空");
        }
        SysMenu sysMenu = sysMenuService.getById(id);
        SysMenuDTO dto = new SysMenuDTO();
        BeanUtils.copyProperties(sysMenu, dto);
        return Result.success(dto);
    }

    @PostMapping
    @Operation(summary = "创建菜单")
    public Result<Integer> create(@Valid @RequestBody SysMenuDTO dto) {
        SysMenu sysMenu = new SysMenu();
        BeanUtils.copyProperties(dto, sysMenu);
        return Result.success(sysMenuService.create(sysMenu));
    }

    @PutMapping("/{id}")
    @Operation(summary = "更新菜单")
    public Result<Void> update(@PathVariable String id, @Valid @RequestBody SysMenuDTO dto) {
        // 双重验证
        if(!id.equals(dto.getId())) {
            throw new BadRequestException("请求参数id不一致");
        }
        SysMenu sysMenu = new SysMenu();
        BeanUtils.copyProperties(dto, sysMenu);
        sysMenuService.update(sysMenu);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除菜单")
    public Result<Void> delete(@PathVariable String id) {
        sysMenuService.deleteById(id);
        return Result.success();
    }

    @PostMapping("/search")
    @Operation(summary = "分页查询菜单")
    public Result<Page<SysMenuDTO>> page(@RequestParam(value = "current", defaultValue = "1") int current, @RequestParam(value = "size", defaultValue = "10") int size, @Valid @RequestBody SysMenuDTO dto) {
        PageParam pageParam = new PageParam(current, size);
        SysMenu sysMenu = new SysMenu();
        BeanUtils.copyProperties(dto, sysMenu);
        Page<SysMenu> page = sysMenuService.page(pageParam, sysMenu);
        Page<SysMenuDTO> dtoPage = new Page<>();
        BeanUtils.copyProperties(page, dtoPage);
        return Result.success(dtoPage);
    }
}
