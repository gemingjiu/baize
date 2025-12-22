package com.gem.baize.system.dept.service.impl;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gem.baize.system.dept.entity.SysDept;
import com.gem.baize.system.dept.mapper.SysDeptMapper;
import com.gem.baize.system.dept.service.SysDeptService;
import com.gem.baize.common.core.exception.model.DataCreationException;
import com.gem.baize.common.core.exception.model.IntegrityViolationException;
import com.gem.baize.common.core.exception.model.NotFoundException;
import com.gem.baize.common.core.model.dto.PageParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.Optional;

/**
 * 部门服务类实现
 */
@Service
public class SysSysDeptServiceImpl extends ServiceImpl<SysDeptMapper, SysDept> implements SysDeptService {

    @Autowired
    private SysDeptMapper sysDeptMapper;

    @Override
    @Cacheable(cacheNames = "sys_dept", key = "#id", sync = true)
    public SysDept getById(String id) {
        return Optional.ofNullable(sysDeptMapper.selectById(id)).orElseThrow(() -> new NotFoundException("部门不存在"));
    }

    @Override
    @CachePut(cacheNames = "sys_dept", key = "#sysDept.id")
    public Integer create(SysDept sysDept) {
        int result = sysDeptMapper.insert(sysDept);
        if (result <= 0) {
            throw new DataCreationException("部门创建失败");
        }
        return result;
    }

    @Override
    @CachePut(cacheNames = "sys_dept", key = "#sysDept.id")
    public void update(SysDept sysDept) {
        int affectedRows = sysDeptMapper.updateById(sysDept);

        if (affectedRows <= 0) {
            throw new NotFoundException("部门信息更新失败，记录不存在");
        }

        if (affectedRows > 1) {
            throw new IntegrityViolationException("部门信息更新异常，影响了多条记录");
        }
    }

    @Override
    @CacheEvict(cacheNames = "sys_dept", key = "#id")
    public void deleteById(String id) {
        int affectedRows = sysDeptMapper.deleteById(id);
        if (affectedRows <= 0) {
            throw new NotFoundException("部门信息删除失败，可能记录不存在");
        }
        if (affectedRows > 1) {
            throw new IntegrityViolationException("部门信息删除异常，影响了多条记录");
        }
    }

    @Override
    public Page<SysDept> page(PageParam pageParam, SysDept sysDept) {
        return Optional.ofNullable(sysDeptMapper.selectPage(pageParam, sysDept))
                .filter(p -> !CollectionUtils.isEmpty(p.getRecords()))
                .orElseThrow(() -> new NotFoundException("未找到部门信息"));
    }
}
