package com.gem.baize.system.menu.service.impl;


import com.alibaba.cloud.commons.lang.StringUtils;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gem.baize.api.system.menu.domain.dto.SysMenuDto;
import com.gem.baize.common.core.exception.model.DataCreationException;
import com.gem.baize.common.core.exception.model.DuplicateException;
import com.gem.baize.common.core.exception.model.IntegrityViolationException;
import com.gem.baize.common.core.exception.model.NotFoundException;
import com.gem.baize.system.menu.entity.SysMenu;
import com.gem.baize.system.menu.mapper.SysMenuMapper;
import com.gem.baize.system.menu.service.SysMenuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.Optional;

/**
 * 部门服务类实现
 */
@Service
public class SysMenuServiceImpl extends ServiceImpl<SysMenuMapper, SysMenu> implements SysMenuService {

    @Autowired
    private SysMenuMapper sysMenuMapper;

    @Autowired
    private SysMenuConvert sysMenuConvert;

    @Override
    @Cacheable(cacheNames = "sys_menu",key ="#id")
    public SysMenuDto getById(String id) {
        SysMenu sysMenu = Optional.ofNullable(sysMenuMapper.selectById(id)).orElseThrow(() -> new NotFoundException("菜单不存在"));
        return sysMenuConvert.toDto(sysMenu);
    }

    @Override
    @Transactional
    public Integer create(SysMenuDto sysMenuDto) {
        SysMenu sysMenu = sysMenuConvert.toEntity(sysMenuDto);

        boolean exists = sysMenuMapper.exists(Wrappers.<SysMenu>lambdaQuery()
                .eq(SysMenu::getMenuName, sysMenuDto.getMenuName()));

        if (exists) {
            throw new DuplicateException("菜单名称已存在，请更换后重试");
        }
        int result = sysMenuMapper.insert(sysMenu);
        if (result <= 0) {
            throw new DataCreationException("菜单创建失败");
        }
        return result;
    }

    @Override
    @Transactional
    @CachePut(cacheNames = "sys_menu", key = "#sysMenuDto.id")
    public void updateById(SysMenuDto sysMenuDto) {
        SysMenu sysMenu = sysMenuConvert.toEntity(sysMenuDto);

        boolean exists = sysMenuMapper.exists(Wrappers.<SysMenu>lambdaQuery()
                .eq(SysMenu::getMenuName, sysMenuDto.getMenuName()));

        if (!exists) {
            throw new DuplicateException("菜单名称不存在，请更换后重试");
        }
        int affectedRows = sysMenuMapper.updateById(sysMenu);

        if (affectedRows <= 0) {
            throw new NotFoundException("菜单信息更新失败，记录不存在");
        }

        if (affectedRows > 1) {
            throw new IntegrityViolationException("菜单信息更新异常，影响了多条记录");
        }
    }

    @Override
    @CacheEvict(cacheNames = "sys_menu",key ="#id")
    public void removeById(String id) {
        super.removeById(id);
    }

    @Override
    public Page<SysMenuDto> page(Page<SysMenu> page, SysMenuDto sysMenuDto) {
        // 构建查询条件
        LambdaQueryWrapper<SysMenu> wrapper = new LambdaQueryWrapper<>();

        // 默认排序
        wrapper.orderByAsc(SysMenu::getSort);

        // 动态条件查询
        if (StringUtils.isNotBlank(sysMenuDto.getMenuName())) {
            wrapper.like(SysMenu::getMenuName, sysMenuDto.getMenuName());
        }
        if (StringUtils.isNotBlank(sysMenuDto.getMenuType())) {
            wrapper.like(SysMenu::getMenuType, sysMenuDto.getMenuType());
        }
        if (StringUtils.isNotBlank(sysMenuDto.getParentId())) {
            wrapper.eq(SysMenu::getParentId, sysMenuDto.getParentId());
        }

        Page<SysMenu> sysPermPage = Optional.ofNullable(sysMenuMapper.selectPage(page, wrapper))
                .filter(p -> !CollectionUtils.isEmpty(p.getRecords()))
                .orElseThrow(() -> new NotFoundException("未找到菜单信息"));
        return sysMenuConvert.toDtoPage(sysPermPage);
    }
}
