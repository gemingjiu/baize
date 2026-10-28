package com.gem.baize.system.loginlog.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gem.baize.api.system.loginlog.domain.dto.SysLoginLogDto;
import com.gem.baize.system.loginlog.entity.SysLoginLog;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

/**
 * SysLoginLog ↔ SysLoginLogDto 转换（MapStruct 编译期生成，替代 BeanUtils 反射拷贝：
 * 新增实体字段漏加 DTO 时编译期告警/显式映射，不再静默丢字段）。
 */
@Mapper
public interface SysLoginLogConvert {

    SysLoginLogConvert INSTANCE = Mappers.getMapper(SysLoginLogConvert.class);

    SysLoginLogDto toDto(SysLoginLog entity);

    SysLoginLog toEntity(SysLoginLogDto dto);

    List<SysLoginLogDto> toDtoList(List<SysLoginLog> list);

    default Page<SysLoginLogDto> toDtoPage(Page<SysLoginLog> page) {
        Page<SysLoginLogDto> result = new Page<>(page.getCurrent(), page.getSize(), page.getTotal());
        result.setRecords(toDtoList(page.getRecords()));
        return result;
    }
}
