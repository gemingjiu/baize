package com.gem.baize.admin.menu.mapper;

import com.alibaba.cloud.commons.lang.StringUtils;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gem.baize.admin.menu.entity.Menu;
import com.gem.baize.common.core.model.dto.PageParam;

public interface MenuMapper extends BaseMapper<Menu> {
    default Page<Menu> selectPage(PageParam pageParam, Menu menu) {
        // 1. 构建分页对象
        Page<Menu> page = new Page<>(pageParam.getPageNum(), pageParam.getPageSize());

        // 2. 构建查询条件
        LambdaQueryWrapper<Menu> wrapper = new LambdaQueryWrapper<>();


        if (StringUtils.isNotBlank(menu.getStatus())) {
            wrapper.eq(Menu::getStatus, menu.getStatus());
        }
        if (menu.getCreatedTime() != null) {
            wrapper.ge(Menu::getCreatedTime, menu.getCreatedTime());
        }
        // 3. 执行分页查询
        return selectPage(page, wrapper);
    }
}
