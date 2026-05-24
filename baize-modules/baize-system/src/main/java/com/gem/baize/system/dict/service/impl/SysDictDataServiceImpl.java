package com.gem.baize.system.dict.service.impl;

import com.alibaba.cloud.commons.lang.StringUtils;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gem.baize.api.system.dict.domain.dto.SysDictDataDto;
import com.gem.baize.common.core.exception.model.DuplicateException;
import com.gem.baize.common.core.exception.model.NotFoundException;
import com.gem.baize.system.dict.entity.SysDictData;
import com.gem.baize.system.dict.mapper.SysDictDataMapper;
import com.gem.baize.system.dict.service.SysDictDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class SysDictDataServiceImpl extends ServiceImpl<SysDictDataMapper, SysDictData> implements SysDictDataService {

    @Autowired
    private SysDictDataConvert sysDictDataConvert;

    @Override
    @Cacheable(cacheNames = "sys_dict_data", key = "#id")
    public SysDictDataDto getById(String id) {
        SysDictData sysDictData = Optional.ofNullable(super.getById(id)).orElseThrow(() -> new NotFoundException("字典数据不存在"));
        return sysDictDataConvert.toDto(sysDictData);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void create(SysDictDataDto sysDictDataDto) {
        SysDictData sysDictData = sysDictDataConvert.toEntity(sysDictDataDto);
        try {
            super.save(sysDictData);
        } catch (DuplicateKeyException e) {
            throw new DuplicateException("字典数据已存在，请更换后重试");
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @CachePut(cacheNames = "sys_dict_data", key = "#sysDictDataDto.id")
    public void updateById(SysDictDataDto sysDictDataDto) {
        SysDictData sysDictData = sysDictDataConvert.toEntity(sysDictDataDto);
        boolean success = super.updateById(sysDictData);
        if (!success) {
            throw new NotFoundException("字典数据不存在或已删除");
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @CacheEvict(cacheNames = "sys_dict_data", key = "#id")
    public void removeById(String id) {
        boolean success = super.removeById(id);
        if (!success) {
            throw new NotFoundException("字典数据不存在或已删除");
        }
    }

    @Override
    public Page<SysDictDataDto> page(Page<SysDictData> page, SysDictDataDto sysDictDataDto) {
        LambdaQueryWrapper<SysDictData> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByAsc(SysDictData::getSort);
        if (StringUtils.isNotBlank(sysDictDataDto.getDictType())) {
            wrapper.eq(SysDictData::getDictType, sysDictDataDto.getDictType());
        }
        if (StringUtils.isNotBlank(sysDictDataDto.getDictLabel())) {
            wrapper.like(SysDictData::getDictLabel, sysDictDataDto.getDictLabel());
        }
        if (StringUtils.isNotBlank(sysDictDataDto.getTenantId())) {
            wrapper.eq(SysDictData::getTenantId, sysDictDataDto.getTenantId());
        }
        Page<SysDictData> sysDictDataPage = super.page(page, wrapper);
        return sysDictDataConvert.toDtoPage(sysDictDataPage);
    }

    @Override
    @Cacheable(cacheNames = "sys_dict_data", key = "'type_' + #dictType")
    public List<SysDictDataDto> listByDictType(String dictType) {
        LambdaQueryWrapper<SysDictData> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysDictData::getDictType, dictType);
        wrapper.orderByAsc(SysDictData::getSort);
        List<SysDictData> list = list(wrapper);
        return list.stream().map(sysDictDataConvert::toDto).collect(Collectors.toList());
    }
}
