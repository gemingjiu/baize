package com.gem.baize.system.user.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gem.baize.api.system.user.domain.dto.SysUserDto;
import com.gem.baize.system.user.entity.SysUser;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

/**
 * SysUser ↔ SysUserDto 转换（MapStruct 编译期生成，替代 BeanUtils 反射拷贝）。
 *
 * <p>deptId 实体与 DTO 均为 String（UUID 主键），MapStruct 自动同型映射，无需桥接。</p>
 */
@Mapper
public interface SysUserConvert {

    SysUserConvert INSTANCE = Mappers.getMapper(SysUserConvert.class);

    SysUserDto toDto(SysUser entity);

    List<SysUserDto> toDtoList(List<SysUser> list);

    default Page<SysUserDto> toDtoPage(Page<SysUser> page) {
        Page<SysUserDto> result = new Page<>(page.getCurrent(), page.getSize(), page.getTotal());
        result.setRecords(toDtoList(page.getRecords()));
        return result;
    }

    /**
     * DTO → 实体：deptId 为空白时按「未指定部门」置 null（NOT NULL 约束由上层
     * 校验兜底报业务错误，而不是 500）。
     */
    default SysUser toEntity(SysUserDto dto) {
        if (dto == null) {
            return null;
        }
        SysUser entity = new SysUser();
        entity.setId(dto.getId());
        entity.setTenantId(dto.getTenantId());
        if (dto.getDeptId() != null && !dto.getDeptId().isBlank()) {
            entity.setDeptId(dto.getDeptId().trim());
        }
        entity.setUserName(dto.getUserName());
        entity.setNickName(dto.getNickName());
        entity.setUserType(dto.getUserType());
        entity.setEmail(dto.getEmail());
        entity.setPhone(dto.getPhone());
        entity.setGender(dto.getGender());
        entity.setAvatar(dto.getAvatar());
        entity.setPassword(dto.getPassword());
        entity.setStatus(dto.getStatus());
        return entity;
    }
}
