package com.gem.baize.system.menu.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.gem.baize.api.system.menu.domain.dto.SysMenuDto;
import com.gem.baize.system.menu.entity.SysRoleMenu;

import java.util.List;

/**
 * 角色菜单关联服务
 */
public interface SysRoleMenuService extends IService<SysRoleMenu> {

    /**
     * 根据角色ID获取菜单ID列表
     */
    List<String> getMenuIdsByRoleId(String roleId);

    /**
     * 根据角色ID列表获取菜单树
     */
    List<SysMenuDto> getMenuTreeByRoleIds(List<String> roleIds);

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
