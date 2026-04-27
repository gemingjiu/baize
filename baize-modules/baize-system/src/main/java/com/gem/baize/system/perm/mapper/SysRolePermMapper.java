package com.gem.baize.system.perm.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.gem.baize.system.perm.entity.SysRolePerm;
import org.apache.ibatis.annotations.Mapper;

/**
 * 角色权限关联Mapper
 */
@Mapper
public interface SysRolePermMapper extends BaseMapper<SysRolePerm> {
}
