package com.gem.baize.system.dict.service.impl;

import com.alibaba.cloud.commons.lang.StringUtils;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gem.baize.api.system.dict.domain.dto.SysDictTypeDto;
import com.gem.baize.common.core.enums.ErrorCode;
import com.gem.baize.common.core.exception.model.BusinessException;
import com.gem.baize.common.core.exception.model.DuplicateException;
import com.gem.baize.common.core.exception.model.NotFoundException;
import com.gem.baize.common.webmvc.context.RequestContextHolder;
import com.gem.baize.system.dict.entity.SysDictData;
import com.gem.baize.system.dict.entity.SysDictType;
import com.gem.baize.system.dict.mapper.SysDictDataMapper;
import com.gem.baize.system.dict.mapper.SysDictTypeMapper;
import com.gem.baize.system.dict.service.SysDictTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * 字典类型服务。
 * <p>
 * 2026-11-05 字典模型对齐：
 * - removeById 双守卫（对齐角色模块）：内置类型不可删；类型下仍有字典数据时不可删（防孤儿数据行）。
 * - is_builtin 标记仅平台租户可维护（create/update 防伪造）。
 */
@Service
public class SysDictTypeServiceImpl extends ServiceImpl<SysDictTypeMapper, SysDictType> implements SysDictTypeService {

    /** 平台级租户ID（nil UUID），与 CustomTenantHandler 的兜底口径一致 */
    private static final String PLATFORM_TENANT = "00000000-0000-0000-0000-000000000000";

    @Autowired
    private SysDictDataMapper sysDictDataMapper;

    @Override
    @Cacheable(cacheNames = "sys_dict_type", key = "#id")
    public SysDictTypeDto getById(String id) {
        SysDictType sysDictType = Optional.ofNullable(super.getById(id)).orElseThrow(() -> new NotFoundException("字典类型不存在"));
        return SysDictTypeConvert.INSTANCE.toDto(sysDictType);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void create(SysDictTypeDto sysDictTypeDto) {
        SysDictType sysDictType = SysDictTypeConvert.INSTANCE.toEntity(sysDictTypeDto);
        // 内置标记仅平台租户可维护：租户上下文下一律落为租户自定义，防伪造内置类型
        if (!PLATFORM_TENANT.equals(currentTenantId()) && "1".equals(sysDictType.getIsBuiltin())) {
            sysDictType.setIsBuiltin("0");
        }
        try {
            super.save(sysDictType);
        } catch (DuplicateKeyException e) {
            throw new DuplicateException("字典类型编码已存在，请更换后重试");
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @CacheEvict(cacheNames = "sys_dict_type", key = "#sysDictTypeDto.id")
    public void updateById(SysDictTypeDto sysDictTypeDto) {
        SysDictType existing = super.getById(sysDictTypeDto.getId());
        if (existing == null || "1".equals(existing.getDeleted())) {
            throw new NotFoundException("字典类型不存在或已删除");
        }
        SysDictType sysDictType = SysDictTypeConvert.INSTANCE.toEntity(sysDictTypeDto);
        // 非平台租户不允许翻转内置标记（保持原值），其余字段正常更新
        if (!PLATFORM_TENANT.equals(currentTenantId())) {
            sysDictType.setIsBuiltin(existing.getIsBuiltin());
        }
        boolean success = super.updateById(sysDictType);
        if (!success) {
            throw new NotFoundException("字典类型不存在或已删除");
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @CacheEvict(cacheNames = "sys_dict_type", key = "#id")
    public void removeById(String id) {
        // 守卫一：内置字典类型（平台维护的通用字典）不允许删除——它是全站表单展示的数据源，
        // 删除会让所有引用该类型的页面下拉/回显直接失效。需要下线请改用停用（status='1'）。
        // 注意：this.getById(id) 被重载为返回 DTO，这里直接查实体（对齐角色的守卫写法）。
        SysDictType type = super.getById(id);
        if (type == null || "1".equals(type.getDeleted())) {
            throw new NotFoundException("字典类型不存在或已删除");
        }
        if ("1".equals(type.getIsBuiltin())) {
            throw new BusinessException(ErrorCode.BUSINESS_CONFLICT, "内置字典类型不允许删除，请使用停用");
        }
        // 守卫二：类型下仍有字典数据时拒绝删除，需先清理数据（防孤儿数据行，引用一致性保护）。
        // 计数走租户拦截器，自动限定当前租户范围。
        Long dataCount = sysDictDataMapper.selectCount(
                new LambdaQueryWrapper<SysDictData>().eq(SysDictData::getDictType, type.getDictType()));
        if (dataCount != null && dataCount > 0) {
            throw new BusinessException(ErrorCode.BUSINESS_CONFLICT,
                    "该字典类型下仍有 " + dataCount + " 条字典数据，请先删除数据后再删除类型");
        }
        // 逻辑删除：只把 deleted 置 '1'（status 同步置 '1'），保留行供审计/外键引用。
        // 不调用 super.removeById：MP 的全局 logic-delete 配置对由 DTO 转换的实体无效，会退化成物理 DELETE。
        LambdaUpdateWrapper<SysDictType> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(SysDictType::getId, id);
        wrapper.eq(SysDictType::getDeleted, "0");
        wrapper.set(SysDictType::getDeleted, "1");
        wrapper.set(SysDictType::getStatus, "1");
        boolean success = this.update(wrapper);
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
        return SysDictTypeConvert.INSTANCE.toDtoPage(sysDictTypePage);
    }

    /**
     * 当前租户上下文；缺失时兜底为平台租户（与 CustomTenantHandler 的 fail-safe 口径一致）。
     */
    private String currentTenantId() {
        String tenantId = RequestContextHolder.getTenantId();
        return StringUtils.isBlank(tenantId) ? PLATFORM_TENANT : tenantId;
    }
}