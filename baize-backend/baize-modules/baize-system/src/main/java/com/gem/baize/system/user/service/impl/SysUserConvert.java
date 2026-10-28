package com.gem.baize.system.user.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gem.baize.api.system.user.domain.dto.SysUserDto;
import com.gem.baize.system.user.entity.SysUser;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.List;

/**
 * SysUser ↔ SysUserDto 转换（MapStruct 编译期生成，替代 BeanUtils 反射拷贝）。
 *
 * <p>deptId 特殊处理：实体是 Long、DTO 是 String（雪花 ID 超出 JS Number 安全整数范围，
 * DTO 用 String 承载）。MapStruct 对 Long↔String 自动生成 String.valueOf / Long.parseLong
 * 等价转换，这里仍显式声明 @Mapping 以固定语义并留档。</p>
 */
@Mapper
public interface SysUserConvert {

    SysUserConvert INSTANCE = Mappers.getMapper(SysUserConvert.class);

    @Mapping(target = "deptId", expression = "java(entity.getDeptId() == null ? null : String.valueOf(entity.getDeptId()))")
    SysUserDto toDto(SysUser entity);

    List<SysUserDto> toDtoList(List<SysUser> list);

    default Page<SysUserDto> toDtoPage(Page<SysUser> page) {
        Page<SysUserDto> result = new Page<>(page.getCurrent(), page.getSize(), page.getTotal());
        result.setRecords(toDtoList(page.getRecords()));
        return result;
    }

    /**
     * DTO → 实体：deptId 为空白或非法值时按「未指定部门」置 null（NOT NULL 约束由上层
     * 校验兜底报业务错误，而不是 500），非法格式抛 IllegalArgumentException。
     */
    default SysUser toEntity(SysUserDto dto) {
        if (dto == null) {
            return null;
        }
        SysUser entity = new SysUser();
        entity.setId(dto.getId());
        entity.setTenantId(dto.getTenantId());
        if (dto.getDeptId() != null && !dto.getDeptId().isBlank()) {
            try {
                entity.setDeptId(Long.parseLong(dto.getDeptId().trim()));
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("部门ID格式非法: " + dto.getDeptId(), e);
            }
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
