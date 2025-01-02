package com.baize.system.domain;

import com.baize.common.core.domain.BaseEntity;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 岗位表 sys_post
 *
 * @author gemj
 * @since 2024/4/6 10:17:41
 */

@EqualsAndHashCode(callSuper = true)
@Data
public class SysPost extends BaseEntity {
    private static final Long serialVersionUID = 1L;
    /**
     * 岗位ID
     */
    private String id;
    /**
     * 岗位编码
     */
    private String postCode;

    /**
     * 岗位名称
     */

    private String postName;
    /**
     * 用户是否存在此岗位标识 默认不存在
     */
    private boolean flag;
}
