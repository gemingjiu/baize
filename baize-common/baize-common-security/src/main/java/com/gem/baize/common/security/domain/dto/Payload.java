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
}
