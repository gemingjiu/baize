package com.gem.baize.api.admin.user.domain.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class UserDTO {
    private String bizId;

    private String tenantId;

    private String deptId;

    private String userName;

    private String nickName;

    private String userType;

    private String email;

    private String phone;

    private String gender;

    private String avatar;

    private String password;

    private String loginIp;

    private LocalDateTime loginDate;
}
