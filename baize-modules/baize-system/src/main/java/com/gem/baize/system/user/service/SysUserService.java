package com.gem.baize.system.user.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.gem.baize.api.system.user.domain.dto.SysUserDto;
import com.gem.baize.common.security.domain.vo.UserVO;
import com.gem.baize.system.user.entity.SysUser;

public interface SysUserService extends IService<SysUser> {
    SysUserDto getById(String id);

    void create(SysUserDto sysUserDto);

    void updateById(SysUserDto sysUserDto);

    void removeById(String id);

    Page<SysUserDto> page(Page<SysUser> page, SysUserDto sysUserDto);

    /**
     * 根据用户名查询用户
     */
    SysUserDto getByUsername(String username);

    /**
     * 根据用户名和租户ID查询用户
     */
    SysUserDto getByUsernameAndTenantId(String username, String tenantId);

    /**
     * 更新用户最后登录时间
     */
    void updateLastLoginTime(String userId, String loginIp);

    /**
     * 重置密码
     */
    void resetPassword(String userId, String newPassword);

    /**
     * 更新密码
     */
    void updatePassword(String userId, String oldPassword, String newPassword);

    /**
     * 更新用户状态
     */
    void changeStatus(String userId, String status);

    /**
     * 获取当前登录用户信息
     */
    UserVO getCurrentUser(String userId);
}
