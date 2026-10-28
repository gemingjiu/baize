package com.gem.baize.system.menu.service.impl;


import com.alibaba.cloud.commons.lang.StringUtils;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
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



    @Override
    @Cacheable(cacheNames = "sys_menu", key = "#id")
    public SysMenuDto getById(String id) {
        SysMenu sysMenu = Optional.ofNullable(super.getById(id)).orElseThrow(() -> new NotFoundException("菜单不存在"));
        return SysMenuConvert.INSTANCE.toDto(sysMenu);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @CacheEvict(cacheNames = "sys_menu", allEntries = true)
    public void create(SysMenuDto sysMenuDto) {
        SysMenu sysMenu = SysMenuConvert.INSTANCE.toEntity(sysMenuDto);
        try {
            super.save(sysMenu);
        } catch (DuplicateKeyException e) {
            throw new DuplicateException("菜单名称已存在，请更换后重试");
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    // updateById 返回 void，@CachePut 会把 null 写进缓存，导致随后 @Cacheable 的 getById 命中缓存返回 null；
    // 改为 @CacheEvict：更新后清掉旧缓存，下次读取直接查库。
    @CacheEvict(cacheNames = "sys_menu", allEntries = true)
    public void updateById(SysMenuDto sysMenuDto) {
        SysMenu sysMenu = SysMenuConvert.INSTANCE.toEntity(sysMenuDto);
        boolean success = super.updateById(sysMenu);
        if (!success) {
            throw new NotFoundException("菜单不存在或已删除");
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @CacheEvict(cacheNames = "sys_menu", allEntries = true)
    public void removeById(String id) {
        // 逻辑删除：只把 deleted 置 '1'，保留行供审计/外键引用。
        // 不调用 super.removeById：MyBatis-Plus 的全局 logic-delete 配置对本项目无效——
        // 因为这里的实体由 DTO 转换而来，且 Service 层的泛型擦除让 MP 的 LogicDeleteField
        // 解析拿不到 deleted 字段，实际会退化成物理 DELETE。
        LambdaUpdateWrapper<SysMenu> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(SysMenu::getId, id);
        wrapper.eq(SysMenu::getDeleted, "0");
        wrapper.set(SysMenu::getDeleted, "1");
        wrapper.set(SysMenu::getStatus, "1");
        boolean success = this.update(wrapper);
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
        return SysMenuConvert.INSTANCE.toDtoPage(sysMenuPage);
    }

    @Override
    // key 里 tenantId 为空时兜底 '0'（与 BaseEntityObjectHandler 租户兜底一致），避免出现 tree_null 这类无意义 key
    @Cacheable(cacheNames = "sys_menu", key = "'tree_' + (#sysMenuDto.tenantId ?: '0')")
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
        List<SysMenuDto> dtoList = SysMenuConvert.INSTANCE.toDtoList(menuList);

        return TreeUtils.buildTree(dtoList, SysMenuDto::getId, SysMenuDto::getParentId, SysMenuDto::setChildren);
    }

    @Override
    public List<SysMenuDto> getMenuTreeByUserId(String userId) {
        // 由SysRoleMenuService实现，避免循环依赖
        SysMenuDto dto = new SysMenuDto();
        return tree(dto);
    }
}
