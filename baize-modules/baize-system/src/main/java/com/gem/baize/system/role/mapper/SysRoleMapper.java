package com.gem.baize.system.role.mapper;

import com.alibaba.cloud.commons.lang.StringUtils;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gem.baize.system.role.entity.SysRole;
import com.gem.baize.common.core.model.dto.PageParam;

public interface SysRoleMapper extends BaseMapper<SysRole> {
    default Page<SysRole> selectPage(PageParam pageParam, SysRole sysRole) {
        // 1. 构建分页对象
        Page<SysRole> page = new Page<>(pageParam.getPageNum(), pageParam.getPageSize());

        // 2. 构建查询条件
        LambdaQueryWrapper<SysRole> wrapper = new LambdaQueryWrapper<>();


        if (StringUtils.isNotBlank(sysRole.getStatus())) {
            wrapper.eq(SysRole::getStatus, sysRole.getStatus());
        }
        if (sysRole.getCreatedTime() != null) {
            wrapper.ge(SysRole::getCreatedTime, sysRole.getCreatedTime());
        }
        // 3. 执行分页查询
        return selectPage(page, wrapper);
    }
}
