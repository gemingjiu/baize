package com.gem.baize.system.config.service.impl;

import com.alibaba.cloud.commons.lang.StringUtils;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gem.baize.api.system.config.domain.dto.SysConfigDto;
import com.gem.baize.common.core.exception.model.DuplicateException;
import com.gem.baize.common.core.exception.model.NotFoundException;
import com.gem.baize.common.webmvc.context.RequestContextHolder;
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

    /** 平台级租户ID（nil UUID），与 CustomTenantHandler 的兑底口径一致 */
    private static final String PLATFORM_TENANT = "00000000-0000-0000-0000-000000000000";


    @Override
    @Cacheable(cacheNames = "sys_config", key = "#id")
    public SysConfigDto getById(String id) {
        SysConfig sysConfig = Optional.ofNullable(super.getById(id)).orElseThrow(() -> new NotFoundException("配置不存在"));
        return SysConfigConvert.INSTANCE.toDto(sysConfig);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @CacheEvict(cacheNames = "sys_config", allEntries = true)
    public void create(SysConfigDto dto) {
        SysConfig sysConfig = SysConfigConvert.INSTANCE.toEntity(dto);
        try {
            super.save(sysConfig);
        } catch (DuplicateKeyException e) {
            throw new DuplicateException("配置键已存在，请更换后重试");
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @CacheEvict(cacheNames = "sys_config", allEntries = true)
    public void updateById(SysConfigDto dto) {
        SysConfig sysConfig = SysConfigConvert.INSTANCE.toEntity(dto);
        boolean success = super.updateById(sysConfig);
        if (!success) {
            throw new NotFoundException("配置不存在或已删除");
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @CacheEvict(cacheNames = "sys_config", allEntries = true)
    public void removeById(String id) {
        // 逻辑删除：只把 deleted 置 '1'（status 同步置 '1' 表示停用），保留行供审计/外键引用。
        // 不调用 super.removeById：MP 的全局 logic-delete 配置对由 DTO 转换的实体无效，
        // 会退化成物理 DELETE。
        LambdaUpdateWrapper<SysConfig> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(SysConfig::getId, id);
        wrapper.eq(SysConfig::getDeleted, "0");
        wrapper.set(SysConfig::getDeleted, "1");
        wrapper.set(SysConfig::getStatus, "1");
        boolean success = this.update(wrapper);
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
        return SysConfigConvert.INSTANCE.toDtoPage(sysConfigPage);
    }

    @Override
    @Cacheable(cacheNames = "sys_config",
            key = "(T(com.gem.baize.common.webmvc.context.RequestContextHolder).getTenantId() ?: '00000000-0000-0000-0000-000000000000') + ':key_' + #configKey")
    public SysConfigDto getByConfigKey(String configKey) {
        LambdaQueryWrapper<SysConfig> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysConfig::getConfigKey, configKey);
        wrapper.eq(SysConfig::getStatus, "0");
        SysConfig sysConfig = getOne(wrapper);
        if (sysConfig == null) {
            return null;
        }
        return SysConfigConvert.INSTANCE.toDto(sysConfig);
    }

    @Override
    @Cacheable(cacheNames = "sys_config",
            key = "(T(com.gem.baize.common.webmvc.context.RequestContextHolder).getTenantId() ?: '00000000-0000-0000-0000-000000000000') + ':value_' + #configKey")
    public String getConfigValueByKey(String configKey) {
        String tenantId = currentTenantId();
        return baseMapper.selectVisibleValue(tenantId, PLATFORM_TENANT, configKey);
    }

    /**
     * 当前租户上下文；缺失时兑底为平台租户（与 CustomTenantHandler 的 fail-safe 口径一致）。
     */
    private String currentTenantId() {
        String tenantId = RequestContextHolder.getTenantId();
        return StringUtils.isBlank(tenantId) ? PLATFORM_TENANT : tenantId;
    }
}
