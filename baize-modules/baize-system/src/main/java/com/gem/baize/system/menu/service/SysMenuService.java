package com.gem.baize.system.menu.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.gem.baize.api.system.menu.domain.dto.SysMenuDto;
import com.gem.baize.system.menu.entity.SysMenu;
import com.gem.baize.common.core.model.dto.PageParam;
import com.gem.baize.system.tenant.entity.SysTenant;

/**
 * 部门服务类接口
 */
public interface SysMenuService extends IService<SysMenu> {


    SysMenuDto getById(String id);

    Integer create(SysMenuDto sysMenuDto);

    void update(SysMenuDto sysMenuDto);

    Page<SysMenuDto> page(Page<SysMenu> page, SysMenuDto sysMenuDto);
}
