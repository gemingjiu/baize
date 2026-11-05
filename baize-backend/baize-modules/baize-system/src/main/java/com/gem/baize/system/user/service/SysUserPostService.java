package com.gem.baize.system.user.service;

import com.gem.baize.system.user.entity.SysUserPost;

import java.util.List;

/**
 * 用户岗位关联服务（2026-11-05 P6 启用）。
 * <p>
 * 全量替换语义（对齐 SysUserRoleService.assignRoles）：先物理 DELETE 再批量 INSERT；
 * 关系表无 deleted 列，删除即物理删除。
 */
public interface SysUserPostService {

    /**
     * 查询用户的岗位ID列表
     */
    List<String> getPostIdsByUserId(String userId);

    /**
     * 全量替换用户的岗位绑定（空列表 = 清空绑定）
     */
    void assignPosts(String userId, List<String> postIds);

    /**
     * 删除用户的全部岗位绑定（用户删除时清理，防孤儿行）
     */
    void removeByUserId(String userId);

    /**
     * 删除岗位的全部用户绑定（岗位删除时清理，防孤儿行）
     */
    void removeByPostId(String postId);
}