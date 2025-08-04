package com.gem.baize.admin.post.mapper;

import com.alibaba.cloud.commons.lang.StringUtils;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gem.baize.admin.post.entity.Post;
import com.gem.baize.common.core.exception.BadRequestException;
import com.gem.baize.common.core.model.dto.PageParam;

import java.time.LocalDateTime;

public interface PostMapper extends BaseMapper<Post> {
    default Post selectByBizId(String bizId) {
        LambdaQueryWrapper<Post> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Post::getBizId, bizId);
        return selectOne(queryWrapper);
    }


    default int updateByBizId(Post post) {
        // 1. 参数校验
        if (post == null || StringUtils.isBlank(post.getBizId())) {
            throw new BadRequestException("业务ID不能为空");
        }

        // 2. 构建更新条件
        LambdaUpdateWrapper<Post> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(Post::getBizId, post.getBizId());

        // 继续添加其他需要更新的字段...

        updateWrapper.set(Post::getModifiedTime, LocalDateTime.now());


        // 5. 执行更新
        return update(updateWrapper);

    }

    default int deleteByBizId(String bizId) {
        LambdaQueryWrapper<Post> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Post::getBizId, bizId);
        return delete(queryWrapper);
    }

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
