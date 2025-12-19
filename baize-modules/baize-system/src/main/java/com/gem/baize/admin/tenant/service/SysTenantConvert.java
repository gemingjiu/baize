package com.gem.baize.admin.tenant.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gem.baize.admin.tenant.entity.SysTenant;
import com.gem.baize.api.admin.tenant.domain.dto.SysTenantDto;

import java.util.List;


public interface SysTenantConvert {


    // Entity -> DTO
    SysTenantDto toDto(SysTenant po);

    // DTO -> Entity
    SysTenant toEntity(SysTenantDto dto);

    // 使用DTO更新已存在的PO（忽略null值）
    void updatePoFromDto(SysTenantDto dto, SysTenant po);

    // 分页转换
    default Page<SysTenantDto> toDtoPage(Page<SysTenant> poPage) {
        Page<SysTenantDto> dtoPage = new Page<>();
        dtoPage.setCurrent(poPage.getCurrent());
        dtoPage.setSize(poPage.getSize());
        dtoPage.setTotal(poPage.getTotal());
        dtoPage.setRecords(toDtoList(poPage.getRecords()));
        return dtoPage;
    }

    // 列表转换
    List<SysTenantDto> toDtoList(List<SysTenant> poList);
}
