package com.gem.baize.system.dept.service.impl;


import com.alibaba.cloud.commons.lang.StringUtils;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gem.baize.api.system.dept.domain.dto.SysDeptDto;
import com.gem.baize.common.core.exception.model.DataCreationException;
import com.gem.baize.common.core.exception.model.DuplicateException;
import com.gem.baize.common.core.exception.model.IntegrityViolationException;
import com.gem.baize.common.core.exception.model.NotFoundException;
import com.gem.baize.system.dept.entity.SysDept;
import com.gem.baize.system.dept.mapper.SysDeptMapper;
import com.gem.baize.system.dept.service.SysDeptService;
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
public class SysSysDeptServiceImpl extends ServiceImpl<SysDeptMapper, SysDept> implements SysDeptService {

    @Autowired
    private SysDeptMapper sysDeptMapper;

    @Autowired
    private SysDeptConvert sysDeptConvert;

    @Override
    @Cacheable(cacheNames = "sys_dept", key = "#id")
    public SysDeptDto getById(String id) {
        SysDept sysDept = Optional.ofNullable(sysDeptMapper.selectById(id)).orElseThrow(() -> new NotFoundException("部门不存在"));
        return sysDeptConvert.toDto(sysDept);
    }

    @Override
    @Transactional
    public Integer create(SysDeptDto sysDeptDto) {
        SysDept sysDept = sysDeptConvert.toEntity(sysDeptDto);
        boolean exists = sysDeptMapper.exists(Wrappers.<SysDept>lambdaQuery()
                .eq(SysDept::getDeptName, sysDeptDto.getDeptName()));

        if (exists) {
            throw new DuplicateException("部门名称已存在，请更换后重试");
        }
        int result = sysDeptMapper.insert(sysDept);
        if (result <= 0) {
            throw new DataCreationException("部门创建失败");
        }
        return result;
    }

    @Override
    @Transactional
    @CachePut(cacheNames = "sys_dept", key = "#sysDeptDto.id")
    public void updateById(SysDeptDto sysDeptDto) {
        SysDept sysDept = sysDeptConvert.toEntity(sysDeptDto);
        boolean exists = sysDeptMapper.exists(Wrappers.<SysDept>lambdaQuery()
                .eq(SysDept::getDeptName, sysDeptDto.getDeptName()));
        if (exists) {
            throw new DuplicateException("部门名称已存在，请更换后重试");
        }

        int affectedRows = sysDeptMapper.updateById(sysDept);

        if (affectedRows <= 0) {
            throw new NotFoundException("部门信息更新失败，记录不存在");
        }

        if (affectedRows > 1) {
            throw new IntegrityViolationException("部门信息更新异常，影响了多条记录");
        }
    }

    @Override
    @CacheEvict(value = "sys_dept", key = "#id")  // 删除缓存
    public void removeById(String id) {
        super.removeById(id);
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

        Page<SysDept> sysDeptPage = Optional.ofNullable(sysDeptMapper.selectPage(page, wrapper))
                .filter(p -> !CollectionUtils.isEmpty(p.getRecords()))
                .orElseThrow(() -> new NotFoundException("未找到租户信息"));
        return sysDeptConvert.toDtoPage(sysDeptPage);
    }
}
