package com.gem.baize.system.role.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.gem.baize.api.system.role.domain.dto.SysRoleDto;
import com.gem.baize.system.role.entity.SysRole;

import java.util.List;

/**
 * 角色服务类接口
 */
public interface SysRoleService extends IService<SysRole> {

    SysRoleDto getById(String id);

    void create(SysRoleDto sysRoleDto);

    void updateById(SysRoleDto sysRoleDto);

    void removeById(String id);

    Page<SysRoleDto> page(Page<SysRole> page, SysRoleDto sysRoleDto);

    SysRoleDto getRoleByUserId(String userId);

    /**
     * 查询用户绑定的全部角色（含停用，口径与 {@code getRoleIdsByUserId} 一致，
     * 已逻辑删除的角色自动排除），供认证签发做多角色聚合。
     *
     * @return 角色列表；用户未绑定任何角色时返回空列表
     */
    List<SysRoleDto> getRolesByUserId(String userId);
}
