package com.gem.baize.admin.menu.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.gem.baize.common.core.id.annotation.GeneratedId;
import com.gem.baize.common.datasource.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
@TableName("sys_menu")
public class Menu extends BaseEntity {
    /**
     * 业务ID
     */
    @GeneratedId(prefix = "menu-")
    @TableField(value = "biz_id", fill = FieldFill.INSERT_UPDATE)
    private String bizId;
    /**
     * 租户ID
     */
    @TableField(value = "tenant_id")
    private Long tenantId;
    /**
     * 父菜单ID
     */
    @TableField(value = "parent_id")
    private Long parentId;
    /**
     * 菜单名称
     */
    @TableField(value = "menu_name")
    private String menuName;
    /**
     * 路由地址
     */
    @TableField(value = "path")
    private String path;
    /**
     * 组件路径
     */
    @TableField(value = "component")
    private String component;
    /**
     * 路由参数
     */
    @TableField(value = "parameters")
    private String parameters;
    /**
     * 是否外链,0:内链,1:外链
     */
    @TableField(value = "external")
    private String external;
    /**
     * 是否缓存,0:不缓存,1:缓存
     */
    @TableField(value = "cacheable")
    private String cacheable;
    /**
     * 菜单类型,0:目录,1:菜单，2:按钮
     */
    @TableField(value = "menu_type")
    private String menuType;
    /**
     * 菜单状态,0:显示1:隐藏
     */
    @TableField(value = "visible")
    private String visible;
    /**
     * 菜单图标
     */
    @TableField(value = "icon")
    private String icon;
    /**
     * 权限字段
     */
    @TableField(value = "perms")
    private String perms;
}
