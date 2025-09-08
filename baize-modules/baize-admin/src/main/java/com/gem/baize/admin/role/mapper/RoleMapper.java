package com.gem.baize.admin.role.mapper;

import com.alibaba.cloud.commons.lang.StringUtils;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gem.baize.admin.role.entity.Role;
import com.gem.baize.common.core.model.dto.PageParam;

public interface RoleMapper extends BaseMapper<Role> {
    default Page<Role> selectPage(PageParam pageParam, Role role) {
        // 1. 构建分页对象
        Page<Role> page = new Page<>(pageParam.getPageNum(), pageParam.getPageSize());

        // 2. 构建查询条件
        LambdaQueryWrapper<Role> wrapper = new LambdaQueryWrapper<>();


        if (StringUtils.isNotBlank(role.getStatus())) {
            wrapper.eq(Role::getStatus, role.getStatus());
        }
        if (role.getCreatedTime() != null) {
            wrapper.ge(Role::getCreatedTime, role.getCreatedTime());
        }
        // 3. 执行分页查询
        return selectPage(page, wrapper);
    }
}
