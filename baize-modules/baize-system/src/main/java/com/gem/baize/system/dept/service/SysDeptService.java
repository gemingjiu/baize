package com.gem.baize.system.dept.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.gem.baize.system.dept.entity.SysDept;
import com.gem.baize.common.core.model.dto.PageParam;

/**
 * 部门服务类接口
 */
public interface SysDeptService extends IService<SysDept> {

    SysDept getById(String id);

    Integer create(SysDept sysDept);

    void update(SysDept sysDept);

    void deleteById(String id);

    Page<SysDept> page(PageParam pageParam, SysDept sysDept);
}
