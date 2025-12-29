package com.gem.baize.system.post.service.impl;


import com.alibaba.cloud.commons.lang.StringUtils;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gem.baize.api.system.post.domain.dto.SysPostDto;
import com.gem.baize.api.system.tenant.domain.dto.SysTenantDto;
import com.gem.baize.common.core.exception.model.DataCreationException;
import com.gem.baize.common.core.exception.model.DuplicateException;
import com.gem.baize.common.core.exception.model.IntegrityViolationException;
import com.gem.baize.common.core.exception.model.NotFoundException;
import com.gem.baize.common.core.model.dto.PageParam;
import com.gem.baize.system.post.entity.SysPost;
import com.gem.baize.system.post.mapper.SysPostMapper;
import com.gem.baize.system.post.service.SysPostService;
import com.gem.baize.system.tenant.entity.SysTenant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.Optional;

/**
 * 岗位服务类实现
 */
@Service
public class SysPostServiceImpl extends ServiceImpl<SysPostMapper, SysPost> implements SysPostService {

    @Autowired
    private SysPostMapper sysPostMapper;

    @Autowired
    private SysPostConvert sysPostConvert;

    @Override
    public SysPostDto getById(String id) {
        SysPost sysPost = Optional.ofNullable(sysPostMapper.selectById(id)).orElseThrow(() -> new NotFoundException("岗位不存在"));
        return sysPostConvert.toDto(sysPost);

    }

    @Override
    public Integer create(SysPostDto sysPostDto) {
        SysPost sysPost = sysPostConvert.toEntity(sysPostDto);
        boolean exists = sysPostMapper.exists(Wrappers.<SysPost>lambdaQuery()
                .eq(SysPost::getPostCode, sysPostDto.getPostCode()));
        if (exists) {
            throw new DuplicateException("岗位编码已存在，请更换后重试");
        }
        int result = sysPostMapper.insert(sysPost);
        if (result <= 0) {
            throw new DataCreationException("岗位创建失败");
        }
        return result;
    }

    @Override
    public void update(SysPostDto sysPostDto) {
        SysPost sysPost = sysPostConvert.toEntity(sysPostDto);
        boolean exists = sysPostMapper.exists(Wrappers.<SysPost>lambdaQuery()
                .eq(SysPost::getPostCode, sysPostDto.getPostCode()));
        if (!exists) {
            throw new DuplicateException("岗位编码不存在，请更换后重试");
        }
        int affectedRows = sysPostMapper.updateById(sysPost);

        if (affectedRows <= 0) {
            throw new NotFoundException("岗位信息更新失败，记录不存在");
        }

        if (affectedRows > 1) {
            throw new IntegrityViolationException("岗位信息更新异常，影响了多条记录");
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

        Page<SysPost> sysPostPage = Optional.ofNullable(sysPostMapper.selectPage(page, wrapper))
                .filter(p -> !CollectionUtils.isEmpty(p.getRecords()))
                .orElseThrow(() -> new NotFoundException("未找到租户信息"));
        return sysPostConvert.toDtoPage(sysPostPage);
    }
}
