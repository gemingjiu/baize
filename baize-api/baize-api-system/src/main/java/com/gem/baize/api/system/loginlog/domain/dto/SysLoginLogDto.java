package com.gem.baize.api.system.loginlog.domain.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class SysLoginLogDto {
    private String id;
    private String tenantId;
    private String userId;
    private String userName;
    private String ipaddr;
    private String loginLocation;
    private String browser;
    private String os;
    private String status;
    private String msg;
    private LocalDateTime loginTime;
}
