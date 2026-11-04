package com.gem.baize.system.dict.service.impl;

import com.alibaba.cloud.commons.lang.StringUtils;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gem.baize.api.system.dict.domain.dto.SysDictDataDto;
import com.gem.baize.common.core.enums.ErrorCode;
import com.gem.baize.common.core.exception.model.BusinessException;
import com.gem.baize.common.core.exception.model.DuplicateException;
import com.gem.baize.common.core.exception.model.NotFoundException;
import com.gem.baize.common.webmvc.context.RequestContextHolder;
import com.gem.baize.system.dict.entity.SysDictData;
import com.gem.baize.system.dict.mapper.SysDictDataMapper;
import com.gem.baize.system.dict.service.SysDictDataService;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 字典数据服务。
 * <p>
 * 2026-11-05 字典模型对齐：读路径按「租户自定义 → 平台内置兜底」合并解析；
 * 写路径内置标记仅平台租户可维护；删除路径内置字典项不可删（对齐角色/租户内置守卫）。
 * 字典数据变更频率极低，写操作对 sys_dict_data 缓存整库清空（allEntries），
 * 避免逐键失效遗漏（此前 type_ 键没有租户维度，曾造成跨租户缓存污染，已修）。
 */
@Service
public class SysDictDataServiceImpl extends ServiceImpl<SysDictDataMapper, SysDictData> implements SysDictDataService {

    /** 平台级租户ID（nil UUID），与 CustomTenantHandler 的兜底口径一致 */
    private static final String PLATFORM_TENANT = "00000000-0000-0000-0000-000000000000";

    @Override
    @Cacheable(cacheNames = "sys_dict_data", key = "#id")
    public SysDictDataDto getById(String id) {
        SysDictData sysDictData = Optional.ofNullable(super.getById(id)).orElseThrow(() -> new NotFoundException("字典数据不存在"));
        return SysDictDataConvert.INSTANCE.toDto(sysDictData);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @CacheEvict(cacheNames = "sys_dict_data", allEntries = true)
    public void create(SysDictDataDto sysDictDataDto) {
        SysDictData sysDictData = SysDictDataConvert.INSTANCE.toEntity(sysDictDataDto);
        // 内置标记仅平台租户可维护：租户上下文下一律落为租户自定义，防伪造内置行
        if (!PLATFORM_TENANT.equals(currentTenantId()) && "1".equals(sysDictData.getIsBuiltin())) {
            sysDictData.setIsBuiltin("0");
        }
        try {
            super.save(sysDictData);
        } catch (DuplicateKeyException e) {
            throw new DuplicateException("字典数据已存在，请更换后重试");
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @CacheEvict(cacheNames = "sys_dict_data", allEntries = true)
    public void updateById(SysDictDataDto sysDictDataDto) {
        SysDictData existing = super.getById(sysDictDataDto.getId());
        if (existing == null || "1".equals(existing.getDeleted())) {
            throw new NotFoundException("字典数据不存在或已删除");
        }
        SysDictData sysDictData = SysDictDataConvert.INSTANCE.toEntity(sysDictDataDto);
        // 非平台租户不允许翻转内置标记（保持原值），其余字段正常更新
        if (!PLATFORM_TENANT.equals(currentTenantId())) {
            sysDictData.setIsBuiltin(existing.getIsBuiltin());
        }
        boolean success = super.updateById(sysDictData);
        if (!success) {
            throw new NotFoundException("字典数据不存在或已删除");
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @CacheEvict(cacheNames = "sys_dict_data", allEntries = true)
    public void removeById(String id) {
        // 守卫：内置字典项（平台维护）不允许删除——它是全租户表单展示的兜底数据；
        // 需要下线请改用停用（status='1'），不要删除。对齐角色/租户内置守卫写法。
        SysDictData existing = super.getById(id);
        if (existing == null || "1".equals(existing.getDeleted())) {
            throw new NotFoundException("字典数据不存在或已删除");
        }
        if ("1".equals(existing.getIsBuiltin())) {
            throw new BusinessException(ErrorCode.BUSINESS_CONFLICT, "内置字典项不允许删除，请使用停用");
        }
        // 逻辑删除：只把 deleted 置 '1'（status 同步置 '1'），保留行供审计/外键引用。
        // 不调用 super.removeById：MP 的全局 logic-delete 配置对由 DTO 转换的实体无效，会退化成物理 DELETE。
        LambdaUpdateWrapper<SysDictData> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(SysDictData::getId, id);
        wrapper.eq(SysDictData::getDeleted, "0");
        wrapper.set(SysDictData::getDeleted, "1");
        wrapper.set(SysDictData::getStatus, "1");
        boolean success = this.update(wrapper);
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
        return SysDictDataConvert.INSTANCE.toDtoPage(sysDictDataPage);
    }

    /**
     * 按类型查询合并视图。
     * <p>
     * 缓存键带租户维度（Elvis 兜底平台租户）：修复此前裸 'type_'+dictType 键在
     * 多租户下 A 租户先查缓存、B 租户命中 A 数据的跨租户污染缺陷。
     */
    @Override
    @Cacheable(cacheNames = "sys_dict_data",
            key = "(T(com.gem.baize.common.webmvc.context.RequestContextHolder).getTenantId() ?: '00000000-0000-0000-0000-000000000000') + ':type_' + #dictType")
    public List<SysDictDataDto> listByDictType(String dictType) {
        String tenantId = currentTenantId();
        List<SysDictData> rows = baseMapper.selectVisible(tenantId, PLATFORM_TENANT, dictType);
        return toDtoList(resolveOverride(rows, tenantId));
    }

    /**
     * 批量获取全部可见字典（/all 接口）：每类型合并解析后按 dictType 分组。
     * 登录后每用户只调一次（前端全局缓存），不加缓存以简化失效链路。
     */
    @Override
    public Map<String, List<SysDictDataDto>> listAll() {
        String tenantId = currentTenantId();
        List<SysDictData> rows = baseMapper.selectVisible(tenantId, PLATFORM_TENANT, null);
        boolean platform = PLATFORM_TENANT.equals(tenantId);

        Map<String, List<SysDictData>> ownByType = new LinkedHashMap<>();
        Map<String, List<SysDictData>> builtinByType = new LinkedHashMap<>();
        for (SysDictData row : rows) {
            // 平台内置行：挂在平台租户且 is_builtin='1'；平台租户自身查询时全部行都算"自有"
            boolean platformBuiltin = PLATFORM_TENANT.equals(row.getTenantId()) && "1".equals(row.getIsBuiltin());
            Map<String, List<SysDictData>> target = (platformBuiltin && !platform) ? builtinByType : ownByType;
            target.computeIfAbsent(row.getDictType(), k -> new ArrayList<>()).add(row);
        }
        Map<String, List<SysDictDataDto>> merged = new LinkedHashMap<>();
        ownByType.forEach((type, list) -> merged.put(type, toDtoList(list)));
        // 租户自定义覆盖平台内置：同类型租户已有自定义项时不再并入内置兜底项
        builtinByType.forEach((type, list) -> merged.putIfAbsent(type, toDtoList(list)));
        return merged;
    }

    /**
     * 单类型合并解析：租户有自有行则整类以其为准（内置行剔除），否则保留平台内置行。
     */
    private List<SysDictData> resolveOverride(List<SysDictData> rows, String tenantId) {
        if (PLATFORM_TENANT.equals(tenantId)) {
            // 平台租户：查询域本身只含平台行（自有 + 内置），全部返回
            return rows;
        }
        boolean hasOwn = rows.stream().anyMatch(r -> tenantId.equals(r.getTenantId()));
        if (hasOwn) {
            return rows.stream().filter(r -> tenantId.equals(r.getTenantId())).collect(Collectors.toList());
        }
        return rows;
    }

    private List<SysDictDataDto> toDtoList(List<SysDictData> list) {
        return list.stream().map(SysDictDataConvert.INSTANCE::toDto).collect(Collectors.toList());
    }

    /**
     * 当前租户上下文；缺失时兜底为平台租户（与 CustomTenantHandler 的 fail-safe 口径一致）。
     */
    private String currentTenantId() {
        String tenantId = RequestContextHolder.getTenantId();
        return StringUtils.isBlank(tenantId) ? PLATFORM_TENANT : tenantId;
    }
}