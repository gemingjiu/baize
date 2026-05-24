package com.gem.baize.system.dict.service.impl;

import com.alibaba.cloud.commons.lang.StringUtils;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gem.baize.api.system.dict.domain.dto.SysDictTypeDto;
import com.gem.baize.common.core.exception.model.DuplicateException;
import com.gem.baize.common.core.exception.model.NotFoundException;
import com.gem.baize.system.dict.entity.SysDictType;
import com.gem.baize.system.dict.mapper.SysDictTypeMapper;
import com.gem.baize.system.dict.service.SysDictTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class SysDictTypeServiceImpl extends ServiceImpl<SysDictTypeMapper, SysDictType> implements SysDictTypeService {

    @Autowired
    private SysDictTypeConvert sysDictTypeConvert;

    @Override
    @Cacheable(cacheNames = "sys_dict_type", key = "#id")
    public SysDictTypeDto getById(String id) {
        SysDictType sysDictType = Optional.ofNullable(super.getById(id)).orElseThrow(() -> new NotFoundException("字典类型不存在"));
        return sysDictTypeConvert.toDto(sysDictType);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void create(SysDictTypeDto sysDictTypeDto) {
        SysDictType sysDictType = sysDictTypeConvert.toEntity(sysDictTypeDto);
        try {
            super.save(sysDictType);
        } catch (DuplicateKeyException e) {
            throw new DuplicateException("字典类型编码已存在，请更换后重试");
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @CachePut(cacheNames = "sys_dict_type", key = "#sysDictTypeDto.id")
    public void updateById(SysDictTypeDto sysDictTypeDto) {
        SysDictType sysDictType = sysDictTypeConvert.toEntity(sysDictTypeDto);
        boolean success = super.updateById(sysDictType);
        if (!success) {
            throw new NotFoundException("字典类型不存在或已删除");
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @CacheEvict(cacheNames = "sys_dict_type", key = "#id")
    public void removeById(String id) {
        boolean success = super.removeById(id);
        if (!success) {
            throw new NotFoundException("字典类型不存在或已删除");
        }
    }

    @Override
    public Page<SysDictTypeDto> page(Page<SysDictType> page, SysDictTypeDto sysDictTypeDto) {
        LambdaQueryWrapper<SysDictType> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByAsc(SysDictType::getSort);
        if (StringUtils.isNotBlank(sysDictTypeDto.getDictName())) {
            wrapper.like(SysDictType::getDictName, sysDictTypeDto.getDictName());
        }
        if (StringUtils.isNotBlank(sysDictTypeDto.getDictType())) {
            wrapper.eq(SysDictType::getDictType, sysDictTypeDto.getDictType());
        }
        if (StringUtils.isNotBlank(sysDictTypeDto.getTenantId())) {
            wrapper.eq(SysDictType::getTenantId, sysDictTypeDto.getTenantId());
        }
        Page<SysDictType> sysDictTypePage = super.page(page, wrapper);
        return sysDictTypeConvert.toDtoPage(sysDictTypePage);
    }
}
