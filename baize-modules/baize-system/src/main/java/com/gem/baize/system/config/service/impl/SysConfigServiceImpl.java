package com.gem.baize.system.config.service.impl;

import com.alibaba.cloud.commons.lang.StringUtils;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gem.baize.api.system.config.domain.dto.SysConfigDto;
import com.gem.baize.common.core.exception.model.DuplicateException;
import com.gem.baize.common.core.exception.model.NotFoundException;
import com.gem.baize.system.config.entity.SysConfig;
import com.gem.baize.system.config.mapper.SysConfigMapper;
import com.gem.baize.system.config.service.SysConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class SysConfigServiceImpl extends ServiceImpl<SysConfigMapper, SysConfig> implements SysConfigService {

    @Autowired
    private SysConfigConvert sysConfigConvert;

    @Override
    @Cacheable(cacheNames = "sys_config", key = "#id")
    public SysConfigDto getById(String id) {
        SysConfig sysConfig = Optional.ofNullable(super.getById(id)).orElseThrow(() -> new NotFoundException("配置不存在"));
        return sysConfigConvert.toDto(sysConfig);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void create(SysConfigDto dto) {
        SysConfig sysConfig = sysConfigConvert.toEntity(dto);
        try {
            super.save(sysConfig);
        } catch (DuplicateKeyException e) {
            throw new DuplicateException("配置键已存在，请更换后重试");
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @CachePut(cacheNames = "sys_config", key = "#dto.id")
    public void updateById(SysConfigDto dto) {
        SysConfig sysConfig = sysConfigConvert.toEntity(dto);
        boolean success = super.updateById(sysConfig);
        if (!success) {
            throw new NotFoundException("配置不存在或已删除");
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @CacheEvict(cacheNames = "sys_config", key = "#id")
    public void removeById(String id) {
        boolean success = super.removeById(id);
        if (!success) {
            throw new NotFoundException("配置不存在或已删除");
        }
    }

    @Override
    public Page<SysConfigDto> page(Page<SysConfig> page, SysConfigDto dto) {
        LambdaQueryWrapper<SysConfig> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByAsc(SysConfig::getSort);

        if (StringUtils.isNotBlank(dto.getConfigName())) {
            wrapper.like(SysConfig::getConfigName, dto.getConfigName());
        }
        if (StringUtils.isNotBlank(dto.getConfigKey())) {
            wrapper.like(SysConfig::getConfigKey, dto.getConfigKey());
        }
        if (StringUtils.isNotBlank(dto.getConfigType())) {
            wrapper.eq(SysConfig::getConfigType, dto.getConfigType());
        }
        if (StringUtils.isNotBlank(dto.getTenantId())) {
            wrapper.eq(SysConfig::getTenantId, dto.getTenantId());
        }

        Page<SysConfig> sysConfigPage = super.page(page, wrapper);
        return sysConfigConvert.toDtoPage(sysConfigPage);
    }

    @Override
    @Cacheable(cacheNames = "sys_config", key = "'key_' + #configKey")
    public SysConfigDto getByConfigKey(String configKey) {
        LambdaQueryWrapper<SysConfig> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysConfig::getConfigKey, configKey);
        SysConfig sysConfig = getOne(wrapper);
        return sysConfigConvert.toDto(sysConfig);
    }
}
