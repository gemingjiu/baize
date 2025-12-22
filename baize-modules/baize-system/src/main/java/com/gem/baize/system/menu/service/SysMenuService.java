package com.gem.baize.system.menu.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.gem.baize.system.menu.entity.SysMenu;
import com.gem.baize.common.core.model.dto.PageParam;

/**
 * 部门服务类接口
 */
public interface SysMenuService extends IService<SysMenu> {


    SysMenu getById(String id);

    Integer create(SysMenu sysMenu);

    void update(SysMenu sysMenu);

    void deleteById(String id);

    Page<SysMenu> page(PageParam pageParam, SysMenu sysMenu);
}
