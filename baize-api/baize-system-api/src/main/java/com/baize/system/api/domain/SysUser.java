package com.baize.system.api.domain;

import java.util.Date;
import java.util.List;

import com.baize.common.core.domain.BaseEntity;
import com.baize.common.core.utils.text.StringUtils;

import lombok.Data;

/**
 * 系统用户信息
 *
 * @author gemj
 * @since 2023/08/22 11:27
 */
@Data
public class SysUser extends BaseEntity {
    private static final long serialVersionUID = 1L;
    private static final String AdminId = "1";
    /**
     * 用户ID
     */
    private String id;
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

    /**
     * 密码
     */
    private String password;

    /**
     * 最后登录IP
     */

    private String loginIp;

    /**
     * 最后登录时间
     */

    private Date loginDate;

    /**
     * 部门对象
     */
    private SysDept dept;

    /**
     * 角色对象
     */
    private List<SysRole> roles;

    /**
     * 角色组
     */
    private String[] roleIds;

    /**
     * 岗位组
     */
    private String[] postIds;

    /**
     * 角色ID
     */
    private String roleId;

    public SysUser(String userId) {
        this.id = userId;
    }

    public SysUser() {

    }

    public boolean isAdmin() {
        return isAdmin(getId());
    }

    public static boolean isAdmin(String userId) {
        return StringUtils.isNotBlank(userId) && StringUtils.equals(userId, AdminId);
    }
}
