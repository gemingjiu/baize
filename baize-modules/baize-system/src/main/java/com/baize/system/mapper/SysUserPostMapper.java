package com.baize.system.mapper;

import com.baize.system.domain.SysUserPost;

import java.util.List;

/**
 * 用户与岗位关联表 数据层
 * 
 * 
 */
public interface SysUserPostMapper
{
    /**
     * 通过用户ID删除用户和岗位关联
     * 
     * @param userId 用户ID
     * @return 结果
     */
    public int deleteUserPostByUserId(String userId);

    /**
     * 通过岗位ID查询岗位使用数量
     * 
     * @param postId 岗位ID
     * @return 结果
     */
    public int countUserPostById(String postId);

    /**
     * 批量删除用户和岗位关联
     * 
     * @param userIds 需要删除的数据ID
     * @return 结果
     */
    public int deleteUserPost(String[] userIds);

    /**
     * 批量新增用户岗位信息
     * 
     * @param userPostList 用户岗位列表
     * @return 结果
     */
    public int batchUserPost(List<SysUserPost> userPostList);
}
