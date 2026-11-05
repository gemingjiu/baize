package com.gem.baize.system.user.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.gem.baize.system.user.entity.SysUserPost;
import com.gem.baize.system.user.mapper.SysUserPostMapper;
import com.gem.baize.system.user.service.SysUserPostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 用户岗位关联服务实现（2026-11-05 P6）。
 * <p>
 * 实现对齐 SysUserRoleServiceImpl：tenant_id 由 BaseEntityObjectHandler 自动填充，
 * 查询/删除由租户拦截器自动限定租户范围。
 */
@Service
public class SysUserPostServiceImpl implements SysUserPostService {

    @Autowired
    private SysUserPostMapper sysUserPostMapper;

    @Override
    public List<String> getPostIdsByUserId(String userId) {
        LambdaQueryWrapper<SysUserPost> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysUserPost::getUserId, userId);
        return sysUserPostMapper.selectList(wrapper).stream()
                .map(SysUserPost::getPostId)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void assignPosts(String userId, List<String> postIds) {
        removeByUserId(userId);
        if (postIds == null || postIds.isEmpty()) {
            return;
        }
        for (String postId : postIds) {
            SysUserPost userPost = new SysUserPost();
            userPost.setUserId(userId);
            userPost.setPostId(postId);
            sysUserPostMapper.insert(userPost);
        }
    }

    @Override
    public void removeByUserId(String userId) {
        LambdaQueryWrapper<SysUserPost> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysUserPost::getUserId, userId);
        sysUserPostMapper.delete(wrapper);
    }

    @Override
    public void removeByPostId(String postId) {
        LambdaQueryWrapper<SysUserPost> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysUserPost::getPostId, postId);
        sysUserPostMapper.delete(wrapper);
    }
}