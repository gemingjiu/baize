package com.gem.baize.system.dept.service.impl;


import com.alibaba.cloud.commons.lang.StringUtils;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gem.baize.api.system.dept.domain.dto.SysDeptDto;
import com.gem.baize.common.core.exception.model.DuplicateException;
import com.gem.baize.common.core.exception.model.NotFoundException;
import com.gem.baize.common.core.utils.TreeUtils;
import com.gem.baize.system.dept.entity.SysDept;
import com.gem.baize.system.dept.mapper.SysDeptMapper;
import com.gem.baize.system.dept.service.SysDeptService;
import lombok.extern.slf4j.Slf4j;
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
 * 部门服务类实现
 */
@Slf4j
@Service
public class SysSysDeptServiceImpl extends ServiceImpl<SysDeptMapper, SysDept> implements SysDeptService {


    @Autowired
    private SysDeptConvert sysDeptConvert;

    @Override
    @Cacheable(cacheNames = "sys_dept", key = "#id")
    public SysDeptDto getById(String id) {
        SysDept sysDept = Optional.ofNullable(super.getById(id)).orElseThrow(() -> new NotFoundException("部门不存在"));
        return sysDeptConvert.toDto(sysDept);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void create(SysDeptDto sysDeptDto) {
        SysDept sysDept = sysDeptConvert.toEntity(sysDeptDto);
        try {
            super.save(sysDept);
        } catch (DuplicateKeyException e) {
            throw new DuplicateException("部门名称已存在，请更换后重试");
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @CachePut(cacheNames = "sys_dept", key = "#sysDeptDto.id")
    public void updateById(SysDeptDto sysDeptDto) {
        SysDept sysDept = sysDeptConvert.toEntity(sysDeptDto);
        boolean success = super.updateById(sysDept);
        if (!success) {
            throw new NotFoundException("部门不存在或已删除");
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @CacheEvict(value = "sys_dept", key = "#id")
    public void removeById(String id) {
        // 检查是否有子部门
        LambdaQueryWrapper<SysDept> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysDept::getParentId, id);
        long childCount = count(wrapper);
        if (childCount > 0) {
            throw new DuplicateException("存在子部门，无法删除");
        }
        boolean success = super.removeById(id);
        if (!success) {
            throw new NotFoundException("部门不存在或已删除");
        }
    }

    @Override
    public Page<SysDeptDto> page(Page<SysDept> page, SysDeptDto sysDeptDto) {
        // 构建查询条件
        LambdaQueryWrapper<SysDept> wrapper = new LambdaQueryWrapper<>();

        // 默认排序
        wrapper.orderByAsc(SysDept::getSort);

        // 动态条件查询
        if (StringUtils.isNotBlank(sysDeptDto.getDeptName())) {
            wrapper.like(SysDept::getDeptName, sysDeptDto.getDeptName());
        }

        if (StringUtils.isNotBlank(sysDeptDto.getPhone())) {
            wrapper.eq(SysDept::getPhone, sysDeptDto.getPhone());
        }

        if (StringUtils.isNotBlank(sysDeptDto.getTenantId())) {
            wrapper.eq(SysDept::getTenantId, sysDeptDto.getTenantId());
        }

        Page<SysDept> sysDeptPage = super.page(page, wrapper);
        return sysDeptConvert.toDtoPage(sysDeptPage);
    }

    @Override
    @Cacheable(cacheNames = "sys_dept", key = "'tree_' + #sysDeptDto.tenantId")
    public List<SysDeptDto> tree(SysDeptDto sysDeptDto) {
        // 构建查询条件
        LambdaQueryWrapper<SysDept> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByAsc(SysDept::getSort);

        if (StringUtils.isNotBlank(sysDeptDto.getDeptName())) {
            wrapper.like(SysDept::getDeptName, sysDeptDto.getDeptName());
        }
        if (StringUtils.isNotBlank(sysDeptDto.getTenantId())) {
            wrapper.eq(SysDept::getTenantId, sysDeptDto.getTenantId());
        }

        List<SysDept> deptList = list(wrapper);
        List<SysDeptDto> dtoList = sysDeptConvert.toDtoList(deptList);

        return TreeUtils.buildTree(dtoList, SysDeptDto::getId, SysDeptDto::getParentId, SysDeptDto::setChildren);
    }

    @Override
    public List<SysDeptDto> list(SysDeptDto sysDeptDto) {
        // 构建查询条件
        LambdaQueryWrapper<SysDept> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByAsc(SysDept::getSort);

        if (StringUtils.isNotBlank(sysDeptDto.getDeptName())) {
            wrapper.like(SysDept::getDeptName, sysDeptDto.getDeptName());
        }
        if (StringUtils.isNotBlank(sysDeptDto.getTenantId())) {
            wrapper.eq(SysDept::getTenantId, sysDeptDto.getTenantId());
        }

        List<SysDept> deptList = list(wrapper);
        return sysDeptConvert.toDtoList(deptList);
    }
}
