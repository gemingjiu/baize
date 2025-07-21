package com.gem.baize.common.security.vo;

import lombok.Data;

// 用户简档VO（避免暴露敏感字段）
@Data
public class UserVO {
    private Long userId;
    private String username;
    private String avatar;
    private String tenantName;
}