package com.gem.baize.system.post.mapper;

import com.alibaba.cloud.commons.lang.StringUtils;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gem.baize.system.post.entity.SysPost;
import com.gem.baize.common.core.model.dto.PageParam;

public interface SysPostMapper extends BaseMapper<SysPost> {
    default Page<SysPost> selectPage(PageParam pageParam, SysPost sysPost) {
        // 1. 构建分页对象
        Page<SysPost> page = new Page<>(pageParam.getPageNum(), pageParam.getPageSize());

        // 2. 构建查询条件
        LambdaQueryWrapper<SysPost> wrapper = new LambdaQueryWrapper<>();


        if (StringUtils.isNotBlank(sysPost.getStatus())) {
            wrapper.eq(SysPost::getStatus, sysPost.getStatus());
        }
        if (sysPost.getCreatedTime() != null) {
            wrapper.ge(SysPost::getCreatedTime, sysPost.getCreatedTime());
        }
        // 3. 执行分页查询
        return selectPage(page, wrapper);
    }
}
