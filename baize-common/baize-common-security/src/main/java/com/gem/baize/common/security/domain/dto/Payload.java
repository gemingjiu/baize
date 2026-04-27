package com.gem.baize.common.security.domain.dto;

import lombok.Data;

@Data
public class Payload {
    private String tenantId;
    private String userId;
    private String traceId;
    private String subject;
    private String userName;
    private String role;

    public String getTenantId() { return tenantId; }
    public void setTenantId(String tenantId) { this.tenantId = tenantId; }
    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }
    public String getTraceId() { return traceId; }
    public void setTraceId(String traceId) { this.traceId = traceId; }
    public String getSubject() { return subject; }
    public void setSubject(String subject) { this.subject = subject; }
    public String getUserName() { return userName; }
    public void setUserName(String userName) { this.userName = userName; }
    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
}
