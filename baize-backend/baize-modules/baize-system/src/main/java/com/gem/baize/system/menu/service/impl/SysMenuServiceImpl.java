package com.gem.baize.system.menu.service.impl;


import com.alibaba.cloud.commons.lang.StringUtils;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gem.baize.api.system.menu.domain.dto.SysMenuDto;
import com.gem.baize.common.core.exception.model.DuplicateException;
import com.gem.baize.common.core.exception.model.NotFoundException;
import com.gem.baize.common.core.utils.TreeUtils;
import com.gem.baize.system.menu.entity.SysMenu;
import com.gem.baize.system.menu.mapper.SysMenuMapper;
import com.gem.baize.system.menu.service.SysMenuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.*;

/**
 * 菜单服务类实现
 */
@Service
public class SysMenuServiceImpl extends ServiceImpl<SysMenuMapper, SysMenu> implements SysMenuService {


    @Autowired
    private SysMenuConvert sysMenuConvert;

    @Override
    @Cacheable(cacheNames = "sys_menu", key = "#id")
    public SysMenuDto getById(String id) {
        SysMenu sysMenu = Optional.ofNullable(super.getById(id)).orElseThrow(() -> new NotFoundException("菜单不存在"));
        return sysMenuConvert.toDto(sysMenu);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void create(SysMenuDto sysMenuDto) {
        SysMenu sysMenu = sysMenuConvert.toEntity(sysMenuDto);
        try {
            super.save(sysMenu);
        } catch (DuplicateKeyException e) {
            throw new DuplicateException("菜单名称已存在，请更换后重试");
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @CachePut(cacheNames = "sys_menu", key = "#sysMenuDto.id")
    public void updateById(SysMenuDto sysMenuDto) {
        SysMenu sysMenu = sysMenuConvert.toEntity(sysMenuDto);
        boolean success = super.updateById(sysMenu);
        if (!success) {
            throw new NotFoundException("菜单不存在或已删除");
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @CacheEvict(cacheNames = "sys_menu", key = "#id")
    public void removeById(String id) {
        boolean success = super.removeById(id);
        if (!success) {
            throw new NotFoundException("菜单不存在或已删除");
        }
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
            wrapper.eq(SysMenu::getMenuType, sysMenuDto.getMenuType());
        }
        if (StringUtils.isNotBlank(sysMenuDto.getParentId())) {
            wrapper.eq(SysMenu::getParentId, sysMenuDto.getParentId());
        }

        Page<SysMenu> sysMenuPage = super.page(page, wrapper);
        return sysMenuConvert.toDtoPage(sysMenuPage);
    }

    @Override
    @Cacheable(cacheNames = "sys_menu", key = "'tree_' + #sysMenuDto.tenantId")
    public List<SysMenuDto> tree(SysMenuDto sysMenuDto) {
        // 构建查询条件
        LambdaQueryWrapper<SysMenu> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByAsc(SysMenu::getSort);

        if (StringUtils.isNotBlank(sysMenuDto.getMenuName())) {
            wrapper.like(SysMenu::getMenuName, sysMenuDto.getMenuName());
        }
        if (StringUtils.isNotBlank(sysMenuDto.getTenantId())) {
            wrapper.eq(SysMenu::getTenantId, sysMenuDto.getTenantId());
        }

        List<SysMenu> menuList = list(wrapper);
        List<SysMenuDto> dtoList = sysMenuConvert.toDtoList(menuList);

        return TreeUtils.buildTree(dtoList, SysMenuDto::getId, SysMenuDto::getParentId, SysMenuDto::setChildren);
    }

    @Override
    public List<SysMenuDto> getMenuTreeByUserId(String userId) {
        // 由SysRoleMenuService实现，避免循环依赖
        SysMenuDto dto = new SysMenuDto();
        return tree(dto);
    }
}
