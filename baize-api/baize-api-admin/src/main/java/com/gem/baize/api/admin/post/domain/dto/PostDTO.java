package com.gem.baize.api.admin.post.domain.dto;

import lombok.Data;

@Data
public class PostDTO {
    private String bizId;

    private String tenantId;

    private String postCode;

    private String postName;
}
