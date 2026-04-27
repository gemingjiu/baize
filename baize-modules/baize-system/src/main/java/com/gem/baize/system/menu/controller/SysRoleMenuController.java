package com.gem.baize.system.menu.controller;

import com.gem.baize.api.system.menu.domain.dto.SysMenuDto;
import com.gem.baize.common.core.model.vo.ApiResult;
import com.gem.baize.system.menu.service.SysRoleMenuService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 角色菜单关联控制器
 */
@RestController
@RequestMapping("/system/role/menu")
public class SysRoleMenuController {

    @Autowired
    private SysRoleMenuService sysRoleMenuService;

    @GetMapping("/{roleId}/menus")
    @Operation(summary = "获取角色菜单ID列表")
    public ApiResult<List<String>> getMenuIdsByRoleId(@PathVariable String roleId) {
        return ApiResult.ok(sysRoleMenuService.getMenuIdsByRoleId(roleId));
    }

    @GetMapping("/roles/tree")
    @Operation(summary = "根据角色ID列表获取菜单树")
    public ApiResult<List<SysMenuDto>> getMenuTreeByRoleIds(@RequestParam List<String> roleIds) {
        return ApiResult.ok(sysRoleMenuService.getMenuTreeByRoleIds(roleIds));
    }

    @PostMapping("/{roleId}/assign")
    @Operation(summary = "为角色分配菜单")
    public ApiResult<Void> assignMenus(@PathVariable String roleId,
                                       @RequestBody List<String> menuIds,
                                       @RequestHeader(value = "X-Tenant-Id", required = false) String tenantId) {
        sysRoleMenuService.assignMenus(roleId, menuIds, tenantId);
        return ApiResult.ok();
    }

    @DeleteMapping("/{roleId}")
    @Operation(summary = "删除角色所有菜单关联")
    public ApiResult<Void> removeByRoleId(@PathVariable String roleId) {
        sysRoleMenuService.removeByRoleId(roleId);
        return ApiResult.ok();
    }
}
