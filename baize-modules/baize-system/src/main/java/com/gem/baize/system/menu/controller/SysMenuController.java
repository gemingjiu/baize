package com.gem.baize.system.menu.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gem.baize.api.system.menu.domain.dto.SysMenuDto;
import com.gem.baize.common.core.annotation.RequiresPermission;
import com.gem.baize.common.core.exception.model.ParamException;
import com.gem.baize.common.core.model.vo.ApiResult;
import com.gem.baize.system.menu.entity.SysMenu;
import com.gem.baize.system.menu.service.SysMenuService;
import com.gem.baize.system.menu.service.SysRoleMenuService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/system/menu")
public class SysMenuController {
    @Autowired
    private SysMenuService sysMenuService;

    @Autowired
    private SysRoleMenuService sysRoleMenuService;

    @GetMapping("/{id}")
    @Operation(summary = "根据ID获取菜单", description = "根据ID查询菜单信息")
    @RequiresPermission("system:menu:query")
    public ApiResult<SysMenuDto> getById(@PathVariable String id) {
        if (StringUtils.isBlank(id)) {
            throw new ParamException("请求参数id不能为空");
        }
        return ApiResult.ok(sysMenuService.getById(id));
    }

    @PostMapping
    @Operation(summary = "创建菜单")
    @RequiresPermission("system:menu:add")
    public ApiResult<Integer> create(@Valid @RequestBody SysMenuDto dto) {
        sysMenuService.create(dto);
        return ApiResult.ok();
    }

    @PutMapping("/{id}")
    @Operation(summary = "更新菜单")
    @RequiresPermission("system:menu:edit")
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
    @RequiresPermission("system:menu:remove")
    public ApiResult<Void> delete(@PathVariable String id) {
        sysMenuService.removeById(id);
        return ApiResult.ok();
    }

    @PostMapping("/page")
    @Operation(summary = "分页查询菜单")
    @RequiresPermission("system:menu:list")
    public ApiResult<Page<SysMenuDto>> page(@RequestParam(value = "current", defaultValue = "1") int current, @RequestParam(value = "size", defaultValue = "10") int size, @Valid @RequestBody SysMenuDto dto) {
        Page<SysMenu> page = new Page<>(current, size);
        return ApiResult.ok(sysMenuService.page(page, dto));
    }

    @PostMapping("/tree")
    @Operation(summary = "查询菜单树")
    @RequiresPermission("system:menu:list")
    public ApiResult<List<SysMenuDto>> tree(@RequestBody(required = false) SysMenuDto dto) {
        if (dto == null) {
            dto = new SysMenuDto();
        }
        return ApiResult.ok(sysMenuService.tree(dto));
    }

    @GetMapping("/user/tree")
    @Operation(summary = "根据用户ID查询菜单树")
    public ApiResult<List<SysMenuDto>> getUserMenuTree(@RequestHeader("X-User-Id") String userId) {
        return ApiResult.ok(sysRoleMenuService.getMenuTreeByUserId(userId));
    }

    @GetMapping("/treeselect")
    @Operation(summary = "获取菜单树选择框数据")
    @RequiresPermission("system:menu:query")
    public ApiResult<List<SysMenuDto>> treeselect(@RequestParam(required = false) String tenantId) {
        SysMenuDto dto = new SysMenuDto();
        dto.setTenantId(tenantId);
        return ApiResult.ok(sysMenuService.tree(dto));
    }

    @GetMapping("/roleMenuTreeselect/{roleId}")
    @Operation(summary = "获取角色菜单树选择框数据")
    @RequiresPermission("system:menu:query")
    public ApiResult<List<SysMenuDto>> roleMenuTreeselect(@PathVariable String roleId,
                                                          @RequestParam(required = false) String tenantId) {
        // 获取所有菜单树
        SysMenuDto dto = new SysMenuDto();
        dto.setTenantId(tenantId);
        return ApiResult.ok(sysMenuService.tree(dto));
    }
}
