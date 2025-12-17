package com.gem.baize.admin.menu.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.gem.baize.admin.menu.entity.Menu;
import com.gem.baize.common.core.model.dto.PageParam;

/**
 * 部门服务类接口
 */
public interface MenuService extends IService<Menu> {


    Menu getById(String id);

    Integer create(Menu menu);

    void update(Menu menu);

    void deleteById(String id);

    Page<Menu> page(PageParam pageParam, Menu menu);
}
