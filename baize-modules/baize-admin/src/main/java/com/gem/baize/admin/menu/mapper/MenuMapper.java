package com.gem.baize.admin.menu.mapper;

import com.alibaba.cloud.commons.lang.StringUtils;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gem.baize.admin.menu.entity.Menu;
import com.gem.baize.common.core.exception.BadRequestException;
import com.gem.baize.common.core.model.dto.PageParam;

import java.time.LocalDateTime;

public interface MenuMapper extends BaseMapper<Menu> {
    default Menu selectByBizId(String bizId) {
        LambdaQueryWrapper<Menu> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Menu::getBizId, bizId);
        return selectOne(queryWrapper);
    }


    default int updateByBizId(Menu menu) {
        // 1. 参数校验
        if (menu == null || StringUtils.isBlank(menu.getBizId())) {
            throw new BadRequestException("业务ID不能为空");
        }

        // 2. 构建更新条件
        LambdaUpdateWrapper<Menu> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(Menu::getBizId, menu.getBizId());

        // 继续添加其他需要更新的字段...

        updateWrapper.set(Menu::getModifiedTime, LocalDateTime.now());


        // 5. 执行更新
        return update(updateWrapper);

    }

    default int deleteByBizId(String bizId) {
        LambdaQueryWrapper<Menu> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Menu::getBizId, bizId);
        return delete(queryWrapper);
    }

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
