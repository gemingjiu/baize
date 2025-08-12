package com.gem.baize.admin.post.service.impl;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gem.baize.admin.post.entity.Post;
import com.gem.baize.admin.post.mapper.PostMapper;
import com.gem.baize.admin.post.service.PostService;
import com.gem.baize.common.core.exception.model.DataCreationException;
import com.gem.baize.common.core.exception.model.IntegrityViolationException;
import com.gem.baize.common.core.exception.model.NotFoundException;
import com.gem.baize.common.core.model.dto.PageParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.Optional;

/**
 * 部门服务类实现
 */
@Service
public class PostServiceImpl extends ServiceImpl<PostMapper, Post> implements PostService {

    @Autowired
    private PostMapper postMapper;

    @Override
    public Post getByBizId(String bizId) {
        return Optional.ofNullable(postMapper.selectByBizId(bizId)).orElseThrow(() -> new NotFoundException("部门不存在"));

    }

    @Override
    public Integer create(Post post) {
        int result = postMapper.insert(post);
        if (result <= 0) {
            throw new DataCreationException("部门创建失败");
        }
        return result;
    }

    @Override
    public void update(Post post) {
        int affectedRows = postMapper.updateByBizId(post);

        if (affectedRows <= 0) {
            throw new NotFoundException("部门信息更新失败，记录不存在");
        }

        if (affectedRows > 1) {
            throw new IntegrityViolationException("部门信息更新异常，影响了多条记录");
        }
    }

    @Override
    public void deleteByBizId(String bizId) {
        int affectedRows = postMapper.deleteByBizId(bizId);
        if (affectedRows <= 0) {
            throw new NotFoundException("部门信息删除失败，可能记录不存在");
        }
        if (affectedRows > 1) {
            throw new IntegrityViolationException("部门信息删除异常，影响了多条记录");
        }
    }

    @Override
    public Page<Post> page(PageParam pageParam, Post post) {
        return Optional.ofNullable(postMapper.selectPage(pageParam, post))
                .filter(p -> !CollectionUtils.isEmpty(p.getRecords()))
                .orElseThrow(() -> new NotFoundException("未找到部门信息"));
    }
}
