package com.gem.baize.admin.user.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.gem.baize.common.core.annotation.GeneratedId;
import com.gem.baize.common.datasource.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 用户信息
 */
@EqualsAndHashCode(callSuper = true)
@Data
@TableName("sys_user")
public class User extends BaseEntity {
    /**
     * 业务ID
     */
    @GeneratedId(prefix = "user-")
    @TableField(value = "biz_id", fill = FieldFill.INSERT_UPDATE)
    private String bizId;
    /**
     * 租户ID
     */
    @TableField(value = "tenant_id")
    private long tenantId;
    /**
     * 部门ID
     */
    @TableField(value = "dept_id")
    private long deptId;
    /**
     * 用户账号
     */
    @TableField(value = "user_name")
    private String userName;
    /**
     * 用户昵称
     */
    @TableField(value = "nick_name")
    private String nickName;
    /**
     * 用户类型
     */
    @TableField(value = "user_type")
    private String userType;
    /**
     * 用户邮箱
     */
    @TableField(value = "email")
    private String email;
    /**
     * 手机号码
     */
    @TableField(value = "phone")
    private String phone;
    /**
     * 用户性别
     */
    @TableField(value = "gender")
    private String gender;
    /**
     * 用户头像
     */
    @TableField(value = "avatar")
    private String avatar;
    /**
     * 密码
     */
    @TableField(value = "password")
    private String password;
    /**
     * 显示顺序
     */
    @TableField(value = "sort")
    private int sort;
    /**
     * 最后登录IP
     */
    @TableField(value = "login_ip")
    private String loginIp;
    /**
     * 最后登录时间
     */
    @TableField(value = "login_date")
    private LocalDateTime loginDate;
}
