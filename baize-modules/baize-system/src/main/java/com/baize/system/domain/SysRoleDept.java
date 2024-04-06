package com.baize.system.domain;

import lombok.Data;

/**
 * 角色和部门关联 sys_role_dept
 *
 * @author gemj
 * @since 2024/4/6 10:17:41
 */

@Data
public class SysRoleDept {
    /**
     * 角色ID
     */
    private String roleId;

    /**
     * 部门ID
     */
    private String deptId;
}
