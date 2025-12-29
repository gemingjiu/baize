package com.gem.baize.system.post.service.impl;

import com.gem.baize.api.system.post.domain.dto.SysPostDto;
import com.gem.baize.api.system.role.domain.dto.SysRoleDto;
import com.gem.baize.common.core.convert.BaseConvert;
import com.gem.baize.system.post.entity.SysPost;
import com.gem.baize.system.role.entity.SysRole;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

@Component
public class SysPostConvert extends BaseConvert<SysPost, SysPostDto> {
    @Override
    public SysPostDto toDto(SysPost entity) {
        if (entity == null) {
            return null;
        }
        SysPostDto dto = new SysPostDto();
        BeanUtils.copyProperties(entity, dto);
        // 特殊字段处理
        return dto;
    }

    @Override
    public SysPost toEntity(SysPostDto dto) {
        if (dto == null) {
            return null;
        }
        SysPost entity = new SysPost();
        BeanUtils.copyProperties(dto, entity);
        // 初始化Entity特殊字段
        return entity;
    }
}
