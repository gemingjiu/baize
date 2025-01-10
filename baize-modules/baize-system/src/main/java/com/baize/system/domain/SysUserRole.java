package com.baize.system.domain;

import lombok.Getter;
import lombok.Setter;

/**
 * 用户和角色关联 sys_user_role
 *
 * @author gemj
 * @since 2024/4/6 10:17:41
 */
@Setter
@Getter
public class SysUserRole {
    /**
     * 用户ID
     */
    private String userId;

    /**
     * 角色ID
     */
    private String roleId;

}
