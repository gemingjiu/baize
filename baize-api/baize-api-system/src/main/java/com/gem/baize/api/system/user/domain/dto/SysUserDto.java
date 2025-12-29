package com.gem.baize.api.system.user.domain.dto;

import lombok.Data;

@Data
public class SysUserDto {
    /**
     * 业务ID
     */
    private String id;
    /**
     * 租户ID
     */
    private String tenantId;
    /**
     * 部门ID
     */
    private String deptId;
    /**
     * 用户账号
     */
    private String userName;
    /**
     * 用户昵称
     */
    private String nickName;
    /**
     * 用户类型
     */
    private String userType;
    /**
     * 用户邮箱
     */
    private String email;
    /**
     * 手机号码
     */
    private String phone;
    /**
     * 用户性别
     */
    private String gender;
    /**
     * 用户头像
     */
    private String avatar;
}
