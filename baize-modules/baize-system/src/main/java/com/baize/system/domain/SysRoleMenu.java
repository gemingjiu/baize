package com.baize.system.domain;

import lombok.Getter;
import lombok.Setter;

/**
 * 角色和菜单关联 sys_role_menu
 *
 * @author gemj
 * @since 2024/4/6 10:17:41
 */
@Setter
@Getter
public class SysRoleMenu {
    /**
     * 角色ID
     */
    private String roleId;

    /**
     * 菜单ID
     */
    private String menuId;

}
