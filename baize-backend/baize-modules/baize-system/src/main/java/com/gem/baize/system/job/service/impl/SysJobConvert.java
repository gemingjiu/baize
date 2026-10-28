package com.gem.baize.system.job.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gem.baize.system.job.domain.dto.SysJobDto;
import com.gem.baize.system.job.entity.SysJob;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

/**
 * SysJob ↔ SysJobDto 转换（MapStruct 编译期生成，替代 BeanUtils 反射拷贝：
 * 新增实体字段漏加 DTO 时编译期告警/显式映射，不再静默丢字段）。
 */
@Mapper
public interface SysJobConvert {

    SysJobConvert INSTANCE = Mappers.getMapper(SysJobConvert.class);

    SysJobDto toDto(SysJob entity);

    SysJob toEntity(SysJobDto dto);

    List<SysJobDto> toDtoList(List<SysJob> list);

    default Page<SysJobDto> toDtoPage(Page<SysJob> page) {
        Page<SysJobDto> result = new Page<>(page.getCurrent(), page.getSize(), page.getTotal());
        result.setRecords(toDtoList(page.getRecords()));
        return result;
    }
}
