package com.baize.system.domain;


import com.baize.common.core.domain.BaseEntity;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * 菜单权限表 sys_menu
 *
 */
@Data
public class SysMenu extends BaseEntity
{
    private static final Long serialVersionUID = 1L;


    /** 菜单名称 */
    private String menuName;

    /** 父菜单名称 */
    private String parentName;

    /** 父菜单ID */
    private String parentId;

    /** 显示顺序 */
    private Integer orderNum;

    /** 路由地址 */
    private String path;

    /** 组件路径 */
    private String component;

    /** 路由参数 */
    private String parameters;

    /** 是否为外链（0是 1否） */
    private String external;

    /** 是否缓存（0缓存 1不缓存） */
    private String cacheable;

    /** 类型（M目录 C菜单 F按钮） */
    private String menuType;

    /** 显示状态（0显示 1隐藏） */
    private String visible;
    
    /** 菜单状态（0正常 1停用） */
    private String status;

    /** 权限字符串 */
    private String perms;

    /** 菜单图标 */
    private String icon;

    /** 子菜单 */
    private List<SysMenu> children = new ArrayList<SysMenu>();
}
