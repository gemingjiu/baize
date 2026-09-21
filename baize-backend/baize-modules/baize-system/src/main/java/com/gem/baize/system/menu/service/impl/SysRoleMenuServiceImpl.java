package com.gem.baize.system.menu.service.impl;

import com.alibaba.cloud.commons.lang.StringUtils;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.gem.baize.api.system.menu.domain.dto.SysMenuDto;
import com.gem.baize.system.menu.entity.SysMenu;
import com.gem.baize.system.menu.entity.SysRoleMenu;
import com.gem.baize.system.menu.mapper.SysMenuMapper;
import com.gem.baize.system.menu.mapper.SysRoleMenuMapper;
import com.gem.baize.system.menu.service.SysRoleMenuService;
import com.gem.baize.system.user.service.SysUserRoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 角色菜单关联服务实现
 */
@Service
public class SysRoleMenuServiceImpl implements SysRoleMenuService {

    @Autowired
    private SysRoleMenuMapper sysRoleMenuMapper;

    @Autowired
    private SysMenuMapper sysMenuMapper;

    @Autowired
    private SysMenuConvert sysMenuConvert;

    @Lazy
    @Autowired
    private SysUserRoleService sysUserRoleService;

    @Override
    public List<String> getMenuIdsByRoleId(String roleId) {
        LambdaQueryWrapper<SysRoleMenu> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysRoleMenu::getRoleId, roleId);
        return sysRoleMenuMapper.selectList(wrapper).stream()
                .map(SysRoleMenu::getMenuId)
                .collect(Collectors.toList());
    }

    @Override
    public List<SysMenuDto> getMenuTreeByRoleIds(List<String> roleIds) {
        if (roleIds == null || roleIds.isEmpty()) {
            return Collections.emptyList();
        }

        // 获取所有关联的菜单ID
        LambdaQueryWrapper<SysRoleMenu> wrapper = new LambdaQueryWrapper<>();
        wrapper.in(SysRoleMenu::getRoleId, roleIds);
        List<String> menuIds = sysRoleMenuMapper.selectList(wrapper).stream()
                .map(SysRoleMenu::getMenuId)
                .distinct()
                .collect(Collectors.toList());

        if (menuIds.isEmpty()) {
            return Collections.emptyList();
        }

        // 获取菜单列表
        LambdaQueryWrapper<SysMenu> menuWrapper = new LambdaQueryWrapper<>();
        menuWrapper.in(SysMenu::getId, menuIds);
        menuWrapper.orderByAsc(SysMenu::getSort);
        List<SysMenu> menus = sysMenuMapper.selectList(menuWrapper);
        List<SysMenuDto> menuDtos = sysMenuConvert.toDtoList(menus);

        // 构建菜单树
        return buildTree(menuDtos);
    }

    /**
     * 根据用户ID获取菜单树
     */
    public List<SysMenuDto> getMenuTreeByUserId(String userId) {
        // 根据用户ID查询角色
        List<String> roleIds = sysUserRoleService.getRoleIdsByUserId(userId);
        if (CollectionUtils.isEmpty(roleIds)) {
            return Collections.emptyList();
        }
        // 根据角色ID查询菜单树
        return getMenuTreeByRoleIds(roleIds);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void assignMenus(String roleId, List<String> menuIds, String tenantId) {
        // 先删除角色现有菜单
        removeByRoleId(roleId);

        // 批量添加新菜单
        if (menuIds != null && !menuIds.isEmpty()) {
            for (String menuId : menuIds) {
                SysRoleMenu roleMenu = new SysRoleMenu();
                roleMenu.setRoleId(roleId);
                roleMenu.setMenuId(menuId);
                sysRoleMenuMapper.insert(roleMenu);
            }
        }
    }

    @Override
    public void removeByRoleId(String roleId) {
        LambdaQueryWrapper<SysRoleMenu> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysRoleMenu::getRoleId, roleId);
        sysRoleMenuMapper.delete(wrapper);
    }

    /**
     * 构建菜单树
     */
    private List<SysMenuDto> buildTree(List<SysMenuDto> menuList) {
        if (menuList == null || menuList.isEmpty()) {
            return Collections.emptyList();
        }

        // 按parentId分组
        Map<String, List<SysMenuDto>> parentIdMap = menuList.stream()
                .filter(menu -> StringUtils.isNotBlank(menu.getParentId()))
                .collect(Collectors.groupingBy(SysMenuDto::getParentId));

        // 设置子菜单
        menuList.forEach(menu -> menu.setChildren(parentIdMap.get(menu.getId())));

        // 返回根节点（parentId为空或"0"的节点）
        return menuList.stream()
                .filter(menu -> StringUtils.isBlank(menu.getParentId()) || "0".equals(menu.getParentId()))
                .collect(Collectors.toList());
    }
}
