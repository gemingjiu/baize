package com.gem.baize.system.menu.controller;

import com.gem.baize.api.system.menu.domain.dto.SysMenuDto;
import com.gem.baize.common.core.annotation.InnerAuth;
import com.gem.baize.common.core.annotation.RequiresPermission;
import com.gem.baize.common.core.model.vo.ApiResult;
import com.gem.baize.system.menu.service.SysRoleMenuService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import com.gem.baize.common.core.constant.PermCodes;
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
    @RequiresPermission(PermCodes.ROLE_QUERY)
    public ApiResult<List<String>> getMenuIdsByRoleId(@PathVariable String roleId) {
        return ApiResult.ok(sysRoleMenuService.getMenuIdsByRoleId(roleId));
    }

    @GetMapping("/roles/tree")
    @Operation(summary = "根据角色ID列表获取菜单树")
    @RequiresPermission(PermCodes.MENU_LIST)
    public ApiResult<List<SysMenuDto>> getMenuTreeByRoleIds(@RequestParam List<String> roleIds) {
        return ApiResult.ok(sysRoleMenuService.getMenuTreeByRoleIds(roleIds));
    }

    /**
     * 服务间调用：认证服务签发票据时读取用户权限码。
     * 加 {@code @InnerAuth} 防止经网关被外部直接调用。
     */
    @InnerAuth
    @GetMapping("/user/{userId}/perms")
    @Operation(summary = "根据用户ID获取权限码列表（服务间调用）")
    public ApiResult<List<String>> getPermCodesByUserId(@PathVariable String userId) {
        return ApiResult.ok(sysRoleMenuService.getPermCodesByUserId(userId));
    }

    /**
     * 为角色分配菜单。
     * 权限码与前端 Authorized 一致（system:role:edit），
     * 历史上缺 @RequiresPermission，任何登录用户都能给自己角色挂菜单。
     */
    @PostMapping("/{roleId}/assign")
    @Operation(summary = "为角色分配菜单")
    @RequiresPermission(PermCodes.ROLE_EDIT)
    public ApiResult<Void> assignMenus(@PathVariable String roleId,
                                       @RequestBody List<String> menuIds,
                                       @RequestHeader(value = "X-Tenant-Id", required = false) String tenantId) {
        sysRoleMenuService.assignMenus(roleId, menuIds, tenantId);
        return ApiResult.ok();
    }

    @DeleteMapping("/{roleId}")
    @Operation(summary = "删除角色所有菜单关联")
    @RequiresPermission(PermCodes.ROLE_EDIT)
    public ApiResult<Void> removeByRoleId(@PathVariable String roleId) {
        sysRoleMenuService.removeByRoleId(roleId);
        return ApiResult.ok();
    }
}
