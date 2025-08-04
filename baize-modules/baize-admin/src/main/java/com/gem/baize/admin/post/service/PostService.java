package com.gem.baize.admin.post.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.gem.baize.admin.post.entity.Post;
import com.gem.baize.common.core.model.dto.PageParam;

/**
 * 部门服务类接口
 */
public interface PostService extends IService<Post> {

    Post getByBizId(String bizId);

    Integer create(Post post);

    void update(Post post);

    void deleteByBizId(String bizId);

    Page<Post> page(PageParam pageParam, Post post);
}
