package com.gem.baize.system.dict.service.impl;

import com.gem.baize.api.system.dict.domain.dto.SysDictTypeDto;
import com.gem.baize.common.core.convert.BaseConvert;
import com.gem.baize.system.dict.entity.SysDictType;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

@Component
public class SysDictTypeConvert extends BaseConvert<SysDictType, SysDictTypeDto> {
    @Override
    public SysDictTypeDto toDto(SysDictType entity) {
        if (entity == null) {
            return null;
        }
        SysDictTypeDto dto = new SysDictTypeDto();
        BeanUtils.copyProperties(entity, dto);
        return dto;
    }

    @Override
    public SysDictType toEntity(SysDictTypeDto dto) {
        if (dto == null) {
            return null;
        }
        SysDictType entity = new SysDictType();
        BeanUtils.copyProperties(dto, entity);
        return entity;
    }
}
