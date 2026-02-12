package com.gem.baize.system.menu.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.gem.baize.api.system.menu.domain.dto.SysMenuDto;
import com.gem.baize.system.menu.entity.SysMenu;

/**
 * 部门服务类接口
 */
public interface SysMenuService extends IService<SysMenu> {


    SysMenuDto getById(String id);

    void create(SysMenuDto sysMenuDto);

    void updateById(SysMenuDto sysMenuDto);

    void removeById(String id);

    Page<SysMenuDto> page(Page<SysMenu> page, SysMenuDto sysMenuDto);
}
