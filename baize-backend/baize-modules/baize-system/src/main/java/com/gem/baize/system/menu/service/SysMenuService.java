package com.gem.baize.system.menu.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.gem.baize.api.system.menu.domain.dto.SysMenuDto;
import com.gem.baize.system.menu.entity.SysMenu;

import java.util.List;

/**
 * 菜单服务类接口
 */
public interface SysMenuService extends IService<SysMenu> {


    SysMenuDto getById(String id);

    void create(SysMenuDto sysMenuDto);

    void updateById(SysMenuDto sysMenuDto);

    void removeById(String id);

    Page<SysMenuDto> page(Page<SysMenu> page, SysMenuDto sysMenuDto);

    /**
     * 查询菜单树
     * @param sysMenuDto 查询条件
     * @return 菜单树
     */
    List<SysMenuDto> tree(SysMenuDto sysMenuDto);

    /**
     * 根据用户ID查询菜单树
     * @param userId 用户ID
     * @return 菜单树
     */
    List<SysMenuDto> getMenuTreeByUserId(String userId);
}
