package com.gem.baize.system.user.service.impl;

import com.gem.baize.api.system.user.domain.dto.SysUserDto;
import com.gem.baize.common.core.convert.BaseConvert;
import com.gem.baize.system.user.entity.SysUser;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

@Component
public class SysUserConvert extends BaseConvert<SysUser, SysUserDto> {
    @Override
    public SysUserDto toDto(SysUser entity) {
        if (entity == null) {
            return null;
        }
        SysUserDto dto = new SysUserDto();
        BeanUtils.copyProperties(entity, dto);
        // 特殊字段处理：deptId 在实体里是 Long、在 DTO 里是 String，BeanUtils 因类型不匹配会静默跳过，
        // 必须显式转换，否则查询结果里的部门 ID 永远为空（前端「部门」列与编辑回显都会丢失）。
        if (entity.getDeptId() != null) {
            dto.setDeptId(String.valueOf(entity.getDeptId()));
        }
        return dto;
    }

    @Override
    public SysUser toEntity(SysUserDto dto) {
        if (dto == null) {
            return null;
        }
        SysUser entity = new SysUser();
        BeanUtils.copyProperties(dto, entity);
        // 初始化Entity特殊字段：同理，DTO 的 deptId 是 String，不显式转换的话新增/编辑用户时
        // dept_id 永远写不进去，而 sys_user.dept_id 是 NOT NULL，新增会直接违反约束报 500。
        // 前端传的是雪花 ID 字符串（超出 JS Number 安全整数范围，所以 DTO 用 String 承载），
        // 这里按 long 解析；空白或非法值按「未指定部门」处理，由上层校验兜底。
        if (dto.getDeptId() != null && !dto.getDeptId().isBlank()) {
            try {
                entity.setDeptId(Long.parseLong(dto.getDeptId().trim()));
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("部门ID格式非法: " + dto.getDeptId(), e);
            }
        }
        return entity;
    }
}
