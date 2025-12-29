package com.gem.baize.system.dept.service.impl;

import com.gem.baize.api.system.dept.domain.dto.SysDeptDto;
import com.gem.baize.common.core.convert.BaseConvert;
import com.gem.baize.system.dept.entity.SysDept;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

@Component
public class SysDeptConvert extends BaseConvert<SysDept, SysDeptDto> {
    @Override
    public SysDeptDto toDto(SysDept entity) {
        if (entity == null) {
            return null;
        }
        SysDeptDto dto = new SysDeptDto();
        BeanUtils.copyProperties(entity, dto);
        // 特殊字段处理
        return dto;
    }

    @Override
    public SysDept toEntity(SysDeptDto dto) {
        if (dto == null) {
            return null;
        }
        SysDept entity = new SysDept();
        BeanUtils.copyProperties(dto, entity);
        // 初始化Entity特殊字段
        return entity;
    }
}
