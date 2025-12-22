package com.gem.baize.api.system.menu.domain.dto;

import lombok.Data;

@Data
public class SysMenuDTO {
    /**
     * 业务ID
     */
    private String id;
    /**
     * 租户ID
     */
    private String tenantId;
    /**
     * 父菜单ID
     */
    private String parentId;
    /**
     * 菜单名称
     */
    private String menuName;
    /**
     * 路由地址
     */
    private String path;
    /**
     * 组件路径
     */
    private String component;
    /**
     * 路由参数
     */
    private String parameters;
    /**
     * 是否外链,0:内链,1:外链
     */
    private String external;
    /**
     * 是否缓存,0:不缓存,1:缓存
     */
    private String cacheable;
    /**
     * 菜单类型,0:目录,1:菜单，2:按钮
     */
    private String menuType;
    /**
     * 菜单状态,0:显示1:隐藏
     */
    private String visible;
    /**
     * 菜单图标
     */
    private String icon;
    /**
     * 权限字段
     */
    private String perms;
}
