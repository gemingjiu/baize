package com.gem.baize.admin.dept.service.impl;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gem.baize.admin.dept.entity.Dept;
import com.gem.baize.admin.dept.mapper.DeptMapper;
import com.gem.baize.admin.dept.service.DeptService;
import com.gem.baize.common.core.exception.model.DataCreationException;
import com.gem.baize.common.core.exception.model.IntegrityViolationException;
import com.gem.baize.common.core.exception.model.NotFoundException;
import com.gem.baize.common.core.model.dto.PageParam;
import org.springframework.aop.framework.AopContext;
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
public class DeptServiceImpl extends ServiceImpl<DeptMapper, Dept> implements DeptService {

    @Autowired
    private DeptMapper deptMapper;

    @Override
    @Cacheable(cacheNames = "sys_dept", key = "#bizId", sync = true)
    public Dept getByBizId(String bizId) {
        return Optional.ofNullable(deptMapper.selectByBizId(bizId)).orElseThrow(() -> new NotFoundException("部门不存在"));
    }

    @Override
    @CachePut(cacheNames = "sys_dept", key = "#dept.bizId")
    public Integer create(Dept dept) {
        int result = deptMapper.insert(dept);
        if (result <= 0) {
            throw new DataCreationException("部门创建失败");
        }
        return result;
    }

    @Override
    @CachePut(cacheNames = "sys_dept", key = "#dept.bizId")
    public void updateByBizId(Dept dept) {
        DeptService proxy = (DeptService) AopContext.currentProxy();
        Long innerId = proxy.getByBizId(dept.getBizId()).getId();
        dept.setId(innerId);
        int affectedRows = deptMapper.updateById(dept);

        if (affectedRows <= 0) {
            throw new NotFoundException("部门信息更新失败，记录不存在");
        }

        if (affectedRows > 1) {
            throw new IntegrityViolationException("部门信息更新异常，影响了多条记录");
        }
    }

    @Override
    @CacheEvict(cacheNames = "sys_dept", key = "#bizId")
    public void deleteByBizId(String bizId) {
        DeptService proxy = (DeptService) AopContext.currentProxy();
        Long innerId = proxy.getByBizId(bizId).getId();
        int affectedRows = deptMapper.deleteById(innerId);
        if (affectedRows <= 0) {
            throw new NotFoundException("部门信息删除失败，可能记录不存在");
        }
        if (affectedRows > 1) {
            throw new IntegrityViolationException("部门信息删除异常，影响了多条记录");
        }
    }

    @Override
    public Page<Dept> page(PageParam pageParam, Dept dept) {
        return Optional.ofNullable(deptMapper.selectPage(pageParam, dept))
                .filter(p -> !CollectionUtils.isEmpty(p.getRecords()))
                .orElseThrow(() -> new NotFoundException("未找到部门信息"));
    }
}
