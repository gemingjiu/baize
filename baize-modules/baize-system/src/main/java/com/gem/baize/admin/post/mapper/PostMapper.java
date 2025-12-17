package com.gem.baize.admin.post.mapper;

import com.alibaba.cloud.commons.lang.StringUtils;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gem.baize.admin.post.entity.Post;
import com.gem.baize.common.core.model.dto.PageParam;

public interface PostMapper extends BaseMapper<Post> {
    default Page<Post> selectPage(PageParam pageParam, Post post) {
        // 1. 构建分页对象
        Page<Post> page = new Page<>(pageParam.getPageNum(), pageParam.getPageSize());

        // 2. 构建查询条件
        LambdaQueryWrapper<Post> wrapper = new LambdaQueryWrapper<>();


        if (StringUtils.isNotBlank(post.getStatus())) {
            wrapper.eq(Post::getStatus, post.getStatus());
        }
        if (post.getCreatedTime() != null) {
            wrapper.ge(Post::getCreatedTime, post.getCreatedTime());
        }
        // 3. 执行分页查询
        return selectPage(page, wrapper);
    }
}
