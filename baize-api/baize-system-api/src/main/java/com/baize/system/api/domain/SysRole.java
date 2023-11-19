package com.baize.system.api.domain;


import com.baize.common.core.domain.BaseEntity;
import com.baize.common.core.utils.text.StringUtils;
import lombok.Data;

import java.util.Set;

/**
 * 角色信息
 *
 * @author gemj
 * @since 2023/08/22 11:32
 */
@Data
public class SysRole extends BaseEntity {
    private static final long serialVersionUID = 1L;
    private static final String AdminId = "1";
    /**
     * 角色名称
     */
    private String roleName;

    /**
     * 角色权限
     */
    private String roleKey;

    /**
     * 数据范围（1：所有数据权限；2：自定义数据权限；3：本部门数据权限；4：本部门及以下数据权限；5：仅本人数据权限）
     */
    private String dataScope;

    /**
     * 菜单树选择项是否关联显示（ 0：父子不互相关联显示 1：父子互相关联显示）
     */
    private String menuCheckStrictly;

    /**
     * 部门树选择项是否关联显示（0：父子不互相关联显示 1：父子互相关联显示 ）
     */
    private String deptCheckStrictly;

    /**
     * 用户是否存在此角色标识 默认不存在
     */
    private boolean flag = false;

    /**
     * 菜单组
     */
    private String[] menuIds;

    /**
     * 部门组（数据权限）
     */
    private String[] ids;

    /**
     * 角色菜单权限
     */
    private Set<String> permissions;

    public SysRole(String roleId) {
        super.setId(roleId);
    }

    public SysRole() {

    }

    public boolean isAdmin() {
        return isAdmin(this.getId());
    }

    public static boolean isAdmin(String roleId) {
        return StringUtils.isNotBlank(roleId) && StringUtils.equals(roleId, AdminId);
    }
}
