package com.gem.baize.system.menu.mapper;

import com.alibaba.cloud.commons.lang.StringUtils;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gem.baize.system.menu.entity.SysMenu;
import com.gem.baize.common.core.model.dto.PageParam;

public interface SysMenuMapper extends BaseMapper<SysMenu> {
    default Page<SysMenu> selectPage(PageParam pageParam, SysMenu sysMenu) {
        // 1. 构建分页对象
        Page<SysMenu> page = new Page<>(pageParam.getPageNum(), pageParam.getPageSize());

        // 2. 构建查询条件
        LambdaQueryWrapper<SysMenu> wrapper = new LambdaQueryWrapper<>();


        if (StringUtils.isNotBlank(sysMenu.getStatus())) {
            wrapper.eq(SysMenu::getStatus, sysMenu.getStatus());
        }
        if (sysMenu.getCreatedTime() != null) {
            wrapper.ge(SysMenu::getCreatedTime, sysMenu.getCreatedTime());
        }
        // 3. 执行分页查询
        return selectPage(page, wrapper);
    }
}
