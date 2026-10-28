package com.gem.baize.system.notice.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gem.baize.api.system.notice.domain.dto.SysNoticeDto;
import com.gem.baize.system.notice.entity.SysNotice;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

/**
 * SysNotice ↔ SysNoticeDto 转换（MapStruct 编译期生成，替代 BeanUtils 反射拷贝：
 * 新增实体字段漏加 DTO 时编译期告警/显式映射，不再静默丢字段）。
 */
@Mapper
public interface SysNoticeConvert {

    SysNoticeConvert INSTANCE = Mappers.getMapper(SysNoticeConvert.class);

    SysNoticeDto toDto(SysNotice entity);

    SysNotice toEntity(SysNoticeDto dto);

    List<SysNoticeDto> toDtoList(List<SysNotice> list);

    default Page<SysNoticeDto> toDtoPage(Page<SysNotice> page) {
        Page<SysNoticeDto> result = new Page<>(page.getCurrent(), page.getSize(), page.getTotal());
        result.setRecords(toDtoList(page.getRecords()));
        return result;
    }
}
