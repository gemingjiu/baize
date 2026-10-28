package com.gem.baize.system.post.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gem.baize.api.system.post.domain.dto.SysPostDto;
import com.gem.baize.system.post.entity.SysPost;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

/**
 * SysPost ↔ SysPostDto 转换（MapStruct 编译期生成，替代 BeanUtils 反射拷贝：
 * 新增实体字段漏加 DTO 时编译期告警/显式映射，不再静默丢字段）。
 */
@Mapper
public interface SysPostConvert {

    SysPostConvert INSTANCE = Mappers.getMapper(SysPostConvert.class);

    SysPostDto toDto(SysPost entity);

    SysPost toEntity(SysPostDto dto);

    List<SysPostDto> toDtoList(List<SysPost> list);

    default Page<SysPostDto> toDtoPage(Page<SysPost> page) {
        Page<SysPostDto> result = new Page<>(page.getCurrent(), page.getSize(), page.getTotal());
        result.setRecords(toDtoList(page.getRecords()));
        return result;
    }
}
