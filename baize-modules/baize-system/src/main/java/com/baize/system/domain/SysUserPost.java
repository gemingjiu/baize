package com.baize.system.domain;

import lombok.Data;

/**
 * 用户和岗位关联 sys_user_post
 *
 * @author gemj
 * @since 2024/4/6 10:17:41
 */

@Data
public class SysUserPost {
    /**
     * 用户ID
     */
    private String userId;

    /**
     * 岗位ID
     */
    private String postId;

}
