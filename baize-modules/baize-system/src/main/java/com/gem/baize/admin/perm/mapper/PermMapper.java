package com.gem.baize.admin.perm.mapper;

import com.alibaba.cloud.commons.lang.StringUtils;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gem.baize.admin.perm.entity.Perm;
import com.gem.baize.common.core.model.dto.PageParam;

public interface PermMapper extends BaseMapper<Perm> {
    default Page<Perm> selectPage(PageParam pageParam, Perm perm) {
        // 1. 构建分页对象
        Page<Perm> page = new Page<>(pageParam.getPageNum(), pageParam.getPageSize());

        // 2. 构建查询条件
        LambdaQueryWrapper<Perm> wrapper = new LambdaQueryWrapper<>();


        if (StringUtils.isNotBlank(perm.getStatus())) {
            wrapper.eq(Perm::getStatus, perm.getStatus());
        }
        if (perm.getCreatedTime() != null) {
            wrapper.ge(Perm::getCreatedTime, perm.getCreatedTime());
        }
        // 3. 执行分页查询
        return selectPage(page, wrapper);
    }
}
