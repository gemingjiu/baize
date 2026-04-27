package com.gem.baize.system.operlog.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gem.baize.api.system.operlog.domain.dto.SysOperLogDto;
import com.gem.baize.system.operlog.entity.SysOperLog;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.List;

/**
 * 操作日志对象转换
 */
@Mapper
public interface SysOperLogConvert {

    SysOperLogConvert INSTANCE = Mappers.getMapper(SysOperLogConvert.class);

    @Mapping(target = "status", source = "operStatus")
    SysOperLogDto toDto(SysOperLog sysOperLog);

    @Mapping(target = "operStatus", source = "status")
    SysOperLog toEntity(SysOperLogDto sysOperLogDto);

    List<SysOperLogDto> toDtoList(List<SysOperLog> list);

    default Page<SysOperLogDto> toDtoPage(Page<SysOperLog> page) {
        Page<SysOperLogDto> result = new Page<>(page.getCurrent(), page.getSize(), page.getTotal());
        result.setRecords(toDtoList(page.getRecords()));
        return result;
    }
}
