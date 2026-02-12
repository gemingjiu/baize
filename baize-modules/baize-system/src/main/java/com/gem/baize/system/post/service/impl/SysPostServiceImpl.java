package com.gem.baize.system.post.service.impl;


import com.alibaba.cloud.commons.lang.StringUtils;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gem.baize.api.system.post.domain.dto.SysPostDto;
import com.gem.baize.common.core.exception.model.DuplicateException;
import com.gem.baize.common.core.exception.model.NotFoundException;
import com.gem.baize.system.post.entity.SysPost;
import com.gem.baize.system.post.mapper.SysPostMapper;
import com.gem.baize.system.post.service.SysPostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.Optional;

/**
 * 岗位服务类实现
 */
@Service
public class SysPostServiceImpl extends ServiceImpl<SysPostMapper, SysPost> implements SysPostService {


    @Autowired
    private SysPostConvert sysPostConvert;

    @Override
    @Cacheable(cacheNames = "sys_post", key = "#id")
    public SysPostDto getById(String id) {
        SysPost sysPost = Optional.ofNullable(super.getById(id)).orElseThrow(() -> new NotFoundException("岗位不存在"));
        return sysPostConvert.toDto(sysPost);

    }

    @Override
    public void create(SysPostDto sysPostDto) {
        SysPost sysPost = sysPostConvert.toEntity(sysPostDto);
        try {
            super.save(sysPost);
        } catch (DuplicateKeyException e) {
            throw new DuplicateException("岗位编码已存在，请更换后重试");
        }
    }

    @Override
    @Transactional
    @CachePut(cacheNames = "sys_post", key = "#sysPostDto.id")
    public void updateById(SysPostDto sysPostDto) {
        SysPost sysPost = sysPostConvert.toEntity(sysPostDto);
        boolean success = super.updateById(sysPost);
        if (!success) {
            throw new NotFoundException("岗位不存在或已删除");
        }
    }

    @Override
    @CacheEvict(cacheNames = "sys_post", key = "#id")
    public void removeById(String id) {
        boolean success = super.removeById(id);
        if (!success) {
            throw new NotFoundException("岗位不存在或已删除");
        }
    }


    @Override
    public Page<SysPostDto> page(Page<SysPost> page, SysPostDto sysPostDto) {
        // 构建查询条件
        LambdaQueryWrapper<SysPost> wrapper = new LambdaQueryWrapper<>();

        // 默认排序
        wrapper.orderByAsc(SysPost::getSort);

        // 动态条件查询
        if (StringUtils.isNotBlank(sysPostDto.getPostCode())) {
            wrapper.like(SysPost::getPostCode, sysPostDto.getPostCode());
        }
        if (StringUtils.isNotBlank(sysPostDto.getPostName())) {
            wrapper.like(SysPost::getPostName, sysPostDto.getPostName());
        }
        if (StringUtils.isNotBlank(sysPostDto.getTenantId())) {
            wrapper.eq(SysPost::getTenantId, sysPostDto.getTenantId());
        }

        Page<SysPost> sysPostPage = Optional.ofNullable(super.page(page, wrapper))
                .filter(p -> !CollectionUtils.isEmpty(p.getRecords()))
                .orElseThrow(() -> new NotFoundException("岗位不存在"));
        return sysPostConvert.toDtoPage(sysPostPage);
    }
}
