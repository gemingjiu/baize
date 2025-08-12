package com.gem.baize.api.admin.menu.domain.dto;

import lombok.Data;

@Data
public class MenuDTO {
    private String bizId;

    private String tenantId;

    private String parentId;

    private String menuName;

    private String path;

    private String component;

    private String parameters;

    private String external;

    private String cacheable;

    private String menuType;

    private String visible;

    private String icon;

    private String perms;
}
