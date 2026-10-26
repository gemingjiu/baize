package com.gem.baize.system.role.service.impl;


import com.alibaba.cloud.commons.lang.StringUtils;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gem.baize.api.system.role.domain.dto.SysRoleDto;
import com.gem.baize.common.core.exception.model.DuplicateException;
import com.gem.baize.common.core.exception.model.NotFoundException;
import com.gem.baize.system.role.entity.SysRole;
import com.gem.baize.system.role.mapper.SysRoleMapper;
import com.gem.baize.system.role.service.SysRoleService;
import com.gem.baize.system.user.service.SysUserRoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Optional;

/**
 * 角色服务类实现
 */
@Service
public class SysRoleServiceImpl extends ServiceImpl<SysRoleMapper, SysRole> implements SysRoleService {


    @Autowired
    private SysRoleConvert sysRoleConvert;

    @Autowired
    private SysUserRoleService sysUserRoleService;

    @Override
    @Cacheable(cacheNames = "sys_role", key = "#id")
    public SysRoleDto getById(String id) {
        SysRole sysRole = Optional.ofNullable(super.getById(id)).orElseThrow(() -> new NotFoundException("角色不存在"));
        return sysRoleConvert.toDto(sysRole);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @CacheEvict(cacheNames = "sys_role", allEntries = true)
    public void create(SysRoleDto sysRoleDto) {
        SysRole sysRole = sysRoleConvert.toEntity(sysRoleDto);
        try {
            super.save(sysRole);
        } catch (DuplicateKeyException e) {
            throw new DuplicateException("角色编码已存在，请更换后重试");
        }
    }

    @Override
    @Transactional
    @CacheEvict(cacheNames = "sys_role", key = "#sysRoleDto.id")
    public void updateById(SysRoleDto sysRoleDto) {

        SysRole sysRole = sysRoleConvert.toEntity(sysRoleDto);

        boolean success = super.updateById(sysRole);

        if (!success) {
            throw new NotFoundException("角色不存在或已删除");
        }


    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @CacheEvict(cacheNames = "sys_role", key = "#id")
    public void removeById(String id) {
        // 逻辑删除：只把 deleted 置 '1'（status 同步置 '1'），保留行供审计/外键引用。
        // 不调用 super.removeById：MP 的全局 logic-delete 配置对由 DTO 转换的实体无效，会退化成物理 DELETE。
        LambdaUpdateWrapper<SysRole> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(SysRole::getId, id);
        wrapper.eq(SysRole::getDeleted, "0");
        wrapper.set(SysRole::getDeleted, "1");
        wrapper.set(SysRole::getStatus, "1");
        boolean success = this.update(wrapper);
        if (!success) {
            throw new NotFoundException("角色不存在或已删除");
        }
    }


    @Override
    public Page<SysRoleDto> page(Page<SysRole> page, SysRoleDto sysRoleDto) {
        // 构建查询条件
        LambdaQueryWrapper<SysRole> wrapper = new LambdaQueryWrapper<>();

        // 默认排序
        wrapper.orderByAsc(SysRole::getSort);

        // 动态条件查询
        if (StringUtils.isNotBlank(sysRoleDto.getRoleName())) {
            wrapper.like(SysRole::getRoleName, sysRoleDto.getRoleName());
        }
        if (StringUtils.isNotBlank(sysRoleDto.getRoleCode())) {
            wrapper.like(SysRole::getRoleCode, sysRoleDto.getRoleCode());
        }
        if (StringUtils.isNotBlank(sysRoleDto.getTenantId())) {
            wrapper.eq(SysRole::getTenantId, sysRoleDto.getTenantId());
        }

        Page<SysRole> sysRolePage = super.page(page, wrapper);
        return sysRoleConvert.toDtoPage(sysRolePage);
    }

    @Override
    public SysRoleDto getRoleByUserId(String userId) {
        List<String> roleIds = sysUserRoleService.getRoleIdsByUserId(userId);
        if (CollectionUtils.isEmpty(roleIds)) {
            return null;
        }
        SysRole sysRole = super.getById(roleIds.get(0));
        return sysRoleConvert.toDto(sysRole);
    }
}
