package com.gem.baize.system.menu.service;

import com.gem.baize.api.system.menu.domain.dto.SysMenuDto;
import com.gem.baize.system.menu.entity.SysRoleMenu;

import java.util.List;

/**
 * 角色菜单关联服务
 */
public interface SysRoleMenuService {

    /**
     * 根据角色ID获取菜单ID列表
     */
    List<String> getMenuIdsByRoleId(String roleId);

    /**
     * 根据角色ID列表获取菜单树
     */
    List<SysMenuDto> getMenuTreeByRoleIds(List<String> roleIds);

    /**
     * 根据用户ID获取权限码集合（用户 → 角色 → 角色菜单 → 菜单 perms）。
     * <p>
     * {@code sys_perm} / {@code sys_role_perm} 目前没有初始化数据，权限码的唯一来源是
     * {@code sys_menu.perms}，因此这里按菜单聚合，供认证服务写入 JWT 的 role claim。
     *
     * @param userId 用户ID
     * @return 去重后的权限码列表，未分配角色或角色无菜单时返回空列表
     */
    List<String> getPermCodesByUserId(String userId);

    /**
     * 为角色分配菜单
     */
    void assignMenus(String roleId, List<String> menuIds, String tenantId);

    /**
     * 删除角色所有菜单关联
     */
    void removeByRoleId(String roleId);

    /**
     * 根据用户ID获取菜单树
     */
    List<SysMenuDto> getMenuTreeByUserId(String userId);
}
