package com.gem.baize.system.perm.service.impl;


import com.alibaba.cloud.commons.lang.StringUtils;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gem.baize.api.system.perm.domain.dto.SysPermDto;
import com.gem.baize.common.core.exception.model.DuplicateException;
import com.gem.baize.common.core.exception.model.NotFoundException;
import com.gem.baize.common.core.utils.TreeUtils;
import com.gem.baize.system.perm.entity.SysPerm;
import com.gem.baize.system.perm.mapper.SysPermMapper;
import com.gem.baize.system.perm.service.SysPermService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Optional;

/**
 * 权限服务类实现
 */
@Service
public class SysPermServiceImpl extends ServiceImpl<SysPermMapper, SysPerm> implements SysPermService {




    @Override
    @Cacheable(cacheNames = "sys_perm", key = "#id")
    public SysPermDto getById(String id) {
        SysPerm sysPerm = Optional.ofNullable(super.getById(id)).orElseThrow(() -> new NotFoundException("权限不存在"));
        return SysPermConvert.INSTANCE.toDto(sysPerm);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void create(SysPermDto sysPermDto) {
        SysPerm sysPerm = SysPermConvert.INSTANCE.toEntity(sysPermDto);
        try {
            super.save(sysPerm);
        } catch (DuplicateException e) {
            throw new DuplicateException("权限编码已存在，请更换后重试");
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @CacheEvict(cacheNames = "sys_perm", key = "#sysPermDto.id")
    public void updateById(SysPermDto sysPermDto) {
        SysPerm sysPerm = SysPermConvert.INSTANCE.toEntity(sysPermDto);
        boolean success = super.updateById(sysPerm);
        if (!success) {
            throw new NotFoundException("权限不存在或已删除");
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @CacheEvict(cacheNames = "sys_perm", key = "#id")
    public void removeById(String id) {
        // 逻辑删除：只把 deleted 置 '1'（status 同步置 '1'），保留行供审计/外键引用。
        // 不调用 super.removeById：MP 的全局 logic-delete 配置对由 DTO 转换的实体无效，会退化成物理 DELETE。
        LambdaUpdateWrapper<SysPerm> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(SysPerm::getId, id);
        wrapper.eq(SysPerm::getDeleted, "0");
        wrapper.set(SysPerm::getDeleted, "1");
        wrapper.set(SysPerm::getStatus, "1");
        boolean success = this.update(wrapper);
        if (!success) {
            throw new NotFoundException("权限不存在或已删除");
        }
    }


    @Override
    public Page<SysPermDto> page(Page<SysPerm> page, SysPermDto sysPermDto) {
        // 构建查询条件
        LambdaQueryWrapper<SysPerm> wrapper = new LambdaQueryWrapper<>();

        // 默认排序
        wrapper.orderByAsc(SysPerm::getSort);

        // 动态条件查询
        if (StringUtils.isNotBlank(sysPermDto.getPermCode())) {
            wrapper.like(SysPerm::getPermCode, sysPermDto.getPermCode());
        }
        if (StringUtils.isNotBlank(sysPermDto.getPermName())) {
            wrapper.like(SysPerm::getPermName, sysPermDto.getPermName());
        }
        if (StringUtils.isNotBlank(sysPermDto.getParentId())) {
            wrapper.eq(SysPerm::getParentId, sysPermDto.getParentId());
        }

        Page<SysPerm> sysPermPage = super.page(page, wrapper);
        return SysPermConvert.INSTANCE.toDtoPage(sysPermPage);
    }

    @Override
    public List<SysPermDto> tree() {
        LambdaQueryWrapper<SysPerm> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByAsc(SysPerm::getSort);

        List<SysPerm> permList = list(wrapper);
        List<SysPermDto> dtoList = SysPermConvert.INSTANCE.toDtoList(permList);

        return TreeUtils.buildTree(dtoList, SysPermDto::getId, SysPermDto::getParentId, SysPermDto::setChildren);
    }
}
