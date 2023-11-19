package com.baize.system.service.impl;


import com.baize.common.core.constant.UserConstants;
import com.baize.common.core.exception.BaseException;
import com.baize.common.core.utils.text.StringUtils;
import com.baize.common.core.utils.uuid.UUID;
import com.baize.common.security.utils.SecurityUtils;
import com.baize.system.domain.SysPost;
import com.baize.system.mapper.SysPostMapper;
import com.baize.system.mapper.SysUserPostMapper;
import com.baize.system.service.ISysPostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.baize.common.core.enums.BaizeExceptionEnum.SERVICE_EXCEPTION;

/**
 * 岗位信息 服务层处理
 *
 *
 */
@Service
public class SysPostServiceImpl implements ISysPostService {
    @Autowired
    private SysPostMapper postMapper;

    @Autowired
    private SysUserPostMapper userPostMapper;

    private void initSysPost(SysPost info) {
        info.setId(UUID.fastUUID().toString(true));
        info.setCreatedBy(SecurityUtils.getLoginUser().getUsername());
        info.setModifiedBy(SecurityUtils.getLoginUser().getUsername());
    }

    /**
     * 查询岗位信息集合
     *
     * @param post 岗位信息
     * @return 岗位信息集合
     */
    @Override
    public List<SysPost> selectPostList(SysPost post) {
        return postMapper.selectPostList(post);
    }

    /**
     * 查询所有岗位
     * 
     * @return 岗位列表
     */
    @Override
    public List<SysPost> selectPostAll()
    {
        return postMapper.selectPostAll();
    }

    /**
     * 通过岗位ID查询岗位信息
     * 
     * @param postId 岗位ID
     * @return 角色对象信息
     */
    @Override
    public SysPost selectPostById(String postId)
    {
        return postMapper.selectPostById(postId);
    }

    /**
     * 根据用户ID获取岗位选择框列表
     * 
     * @param userId 用户ID
     * @return 选中岗位ID列表
     */
    @Override
    public List<String> selectPostListByUserId(String userId)
    {
        return postMapper.selectPostListByUserId(userId);
    }

    /**
     * 校验岗位名称是否唯一
     * 
     * @param post 岗位信息
     * @return 结果
     */
    @Override
    public boolean checkPostNameUnique(SysPost post)
    {
        String postId = StringUtils.isNull(post.getId()) ? "" : post.getId();
        SysPost info = postMapper.checkPostNameUnique(post.getPostName());
        if (StringUtils.isNotNull(info) && StringUtils.equals(info.getId(),postId))
        {
            return UserConstants.NOT_UNIQUE;
        }
        return UserConstants.UNIQUE;
    }

    /**
     * 校验岗位编码是否唯一
     * 
     * @param post 岗位信息
     * @return 结果
     */
    @Override
    public boolean checkPostCodeUnique(SysPost post)
    {
        String postId = StringUtils.isNull(post.getId()) ? "" : post.getId();
        SysPost info = postMapper.checkPostCodeUnique(post.getPostCode());
        if (StringUtils.isNotNull(info) && StringUtils.equals(info.getId(),postId))
        {
            return UserConstants.NOT_UNIQUE;
        }
        return UserConstants.UNIQUE;
    }

    /**
     * 通过岗位ID查询岗位使用数量
     * 
     * @param postId 岗位ID
     * @return 结果
     */
    @Override
    public int countUserPostById(String postId)
    {
        return userPostMapper.countUserPostById(postId);
    }

    /**
     * 删除岗位信息
     * 
     * @param postId 岗位ID
     * @return 结果
     */
    @Override
    public int deletePostById(String postId)
    {
        return postMapper.deletePostById(postId);
    }

    /**
     * 批量删除岗位信息
     * 
     * @param postIds 需要删除的岗位ID
     * @return 结果
     */
    @Override
    public int deletePostByIds(String[] postIds)
    {
        for (String postId : postIds)
        {
            SysPost post = selectPostById(postId);
            if (countUserPostById(postId) > 0)
            {
                throw new BaseException(SERVICE_EXCEPTION, String.format("%1$s已分配,不能删除", post.getPostName()));
            }
        }
        return postMapper.deletePostByIds(postIds);
    }

    /**
     * 新增保存岗位信息
     * 
     * @param post 岗位信息
     * @return 结果
     */
    @Override
    public int insertPost(SysPost post)
    {
        initSysPost(post);
        return postMapper.insertPost(post);
    }

    /**
     * 修改保存岗位信息
     * 
     * @param post 岗位信息
     * @return 结果
     */
    @Override
    public int updatePost(SysPost post)
    {
        return postMapper.updatePost(post);
    }
}
