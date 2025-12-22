package com.gem.baize.system.dept.mapper;

import com.alibaba.cloud.commons.lang.StringUtils;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gem.baize.system.dept.entity.SysDept;
import com.gem.baize.common.core.model.dto.PageParam;

public interface SysDeptMapper extends BaseMapper<SysDept> {
    default Page<SysDept> selectPage(PageParam pageParam, SysDept sysDept) {
        // 1. 构建分页对象
        Page<SysDept> page = new Page<>(pageParam.getPageNum(), pageParam.getPageSize());

        // 2. 构建查询条件
        LambdaQueryWrapper<SysDept> wrapper = new LambdaQueryWrapper<>();


        if (StringUtils.isNotBlank(sysDept.getStatus())) {
            wrapper.eq(SysDept::getStatus, sysDept.getStatus());
        }
        if (sysDept.getCreatedTime() != null) {
            wrapper.ge(SysDept::getCreatedTime, sysDept.getCreatedTime());
        }
        // 3. 执行分页查询
        return selectPage(page, wrapper);
    }
}
