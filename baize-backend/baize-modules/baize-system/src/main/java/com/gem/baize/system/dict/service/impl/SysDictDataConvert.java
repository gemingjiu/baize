package com.gem.baize.system.dict.service.impl;

import com.gem.baize.api.system.dict.domain.dto.SysDictDataDto;
import com.gem.baize.common.core.convert.BaseConvert;
import com.gem.baize.system.dict.entity.SysDictData;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

@Component
public class SysDictDataConvert extends BaseConvert<SysDictData, SysDictDataDto> {
    @Override
    public SysDictDataDto toDto(SysDictData entity) {
        if (entity == null) {
            return null;
        }
        SysDictDataDto dto = new SysDictDataDto();
        BeanUtils.copyProperties(entity, dto);
        return dto;
    }

    @Override
    public SysDictData toEntity(SysDictDataDto dto) {
        if (dto == null) {
            return null;
        }
        SysDictData entity = new SysDictData();
        BeanUtils.copyProperties(dto, entity);
        return entity;
    }
}
