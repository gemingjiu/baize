package com.gem.baize.admin.dept.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.gem.baize.admin.dept.entity.Dept;
import com.gem.baize.common.core.model.dto.PageParam;

/**
 * 部门服务类接口
 */
public interface DeptService extends IService<Dept> {

    Dept getById(String id);

    Integer create(Dept dept);

    void update(Dept dept);

    void deleteById(String id);

    Page<Dept> page(PageParam pageParam, Dept dept);
}
