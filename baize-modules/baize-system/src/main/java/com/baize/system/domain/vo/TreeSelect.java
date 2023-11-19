package com.baize.system.domain.vo;

import com.baize.system.api.domain.SysDept;
import com.baize.system.domain.SysMenu;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author gemj
 * @since 2023/12/14 23:07
 */
@Data
public class TreeSelect {
    private static final Long serialVersionUID = 1L;
    public TreeSelect(SysDept dept)
    {
        this.id = dept.getId();
        this.label = dept.getDeptName();
        this.children = dept.getChildren().stream().map(TreeSelect::new).collect(Collectors.toList());
    }
    public TreeSelect(SysMenu menu) {
        this.id = menu.getId();
        this.label = menu.getMenuName();
        this.children = menu.getChildren().stream().map(TreeSelect::new).collect(Collectors.toList());
    }

    /** 节点ID */
    private String id;

    /** 节点名称 */
    private String label;

    /** 子节点 */
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private List<TreeSelect> children;


}
