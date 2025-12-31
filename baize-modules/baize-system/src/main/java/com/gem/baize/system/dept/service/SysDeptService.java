package com.gem.baize.system.dept.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.gem.baize.api.system.dept.domain.dto.SysDeptDto;
import com.gem.baize.system.dept.entity.SysDept;

/**
 * 部门服务类接口
 */
public interface SysDeptService extends IService<SysDept> {

    SysDeptDto getById(String id);

    Integer create(SysDeptDto sysDeptDto);

    void updateById(SysDeptDto sysDeptDto);

    void removeById(String id);


    Page<SysDeptDto> page(Page<SysDept> page, SysDeptDto sysDeptDto);
}
