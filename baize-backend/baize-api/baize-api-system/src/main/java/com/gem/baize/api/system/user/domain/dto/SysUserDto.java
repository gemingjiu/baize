package com.gem.baize.api.system.user.domain.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
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

    /**
     * 状态（0=正常，1=停用），来自 BaseEntity。
     * 注意：DTO 必须显式声明该字段，否则 BeanUtils.copyProperties 不会拷贝，
     * 导致前端「状态」列永远为空，且 changeUserStatus 修改后列表看不到变化。
     */
    private String status;

    /**
     * 密码（仅允许反序列化写入，序列化响应时忽略，避免接口泄露密码哈希）
     */
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String password;

    /**
     * 岗位ID列表（2026-11-05 P6 用户-岗位绑定；创建/更新时全量替换绑定关系）
     */
    private java.util.List<String> postIds;
}
