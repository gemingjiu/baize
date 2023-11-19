package com.baize.system.api.domain;


import com.baize.common.core.domain.BaseEntity;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;


/**
 * 部门信息
 *
 * @author gemj
 * @since 2023/08/22 11:31
 */
@Data
public class SysDept extends BaseEntity {
    private static final long serialVersionUID = 1L;
    /** 父部门ID */
    private String parentId;

    /** 祖级列表 */
    private String ancestors;

    /** 部门名称 */
    private String deptName;

    /** 显示顺序 */
    private Integer orderNum;

    /** 负责人 */
    private String leader;

    /** 联系电话 */
    private String phone;

    /** 邮箱 */
    private String email;

    /** 父部门名称 */
    private String parentName;

    /** 子部门 */
    private List<SysDept> children = new ArrayList<SysDept>();
}
