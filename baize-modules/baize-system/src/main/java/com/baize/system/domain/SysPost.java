package com.baize.system.domain;


import com.baize.common.core.domain.BaseEntity;
import lombok.Data;

/**
 * 岗位表 sys_post
 * 

 */

@Data
public class SysPost extends BaseEntity
{
    private static final Long serialVersionUID = 1L;

    /** 岗位编码 */
    private String postCode;

    /** 岗位名称 */

    private String postName;
    /** 用户是否存在此岗位标识 默认不存在 */
    private boolean flag;
}
