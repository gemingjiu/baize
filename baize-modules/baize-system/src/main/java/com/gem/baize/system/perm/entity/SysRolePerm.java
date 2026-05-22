package com.gem.baize.system.perm.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;

/**
 * 角色权限关联
 */
@Data
@TableName("sys_role_perm")
public class SysRolePerm implements Serializable {

    /**
     * 角色ID
     */
    @TableField("role_id")
    private String roleId;

    /**
     * 权限ID
     */
    @TableField("perm_id")
    private String permId;

}
