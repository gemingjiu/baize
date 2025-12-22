package com.gem.baize.system.perm.mapper;

import com.alibaba.cloud.commons.lang.StringUtils;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gem.baize.system.perm.entity.SysPerm;
import com.gem.baize.common.core.model.dto.PageParam;

public interface SysPermMapper extends BaseMapper<SysPerm> {
    default Page<SysPerm> selectPage(PageParam pageParam, SysPerm sysPerm) {
        // 1. 构建分页对象
        Page<SysPerm> page = new Page<>(pageParam.getPageNum(), pageParam.getPageSize());

        // 2. 构建查询条件
        LambdaQueryWrapper<SysPerm> wrapper = new LambdaQueryWrapper<>();


        if (StringUtils.isNotBlank(sysPerm.getStatus())) {
            wrapper.eq(SysPerm::getStatus, sysPerm.getStatus());
        }
        if (sysPerm.getCreatedTime() != null) {
            wrapper.ge(SysPerm::getCreatedTime, sysPerm.getCreatedTime());
        }
        // 3. 执行分页查询
        return selectPage(page, wrapper);
    }
}
