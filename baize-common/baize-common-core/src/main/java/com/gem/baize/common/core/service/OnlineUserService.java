package com.gem.baize.common.core.service;

import java.util.List;

/**
 * 在线用户服务
 */
public interface OnlineUserService {

    /**
     * 用户登录
     * @param userId 用户ID
     * @param tenantId 租户ID
     * @param username 用户名
     */
    void login(String userId, String tenantId, String username);

    /**
     * 用户登出
     * @param userId 用户ID
     * @param tenantId 租户ID
     */
    void logout(String userId, String tenantId);

    /**
     * 更新用户活跃时间
     * @param userId 用户ID
     * @param tenantId 租户ID
     */
    void refresh(String userId, String tenantId);

    /**
     * 获取在线用户数量
     * @param tenantId 租户ID
     * @return 在线用户数量
     */
    long getOnlineCount(String tenantId);

    /**
     * 获取在线用户列表
     * @param tenantId 租户ID
     * @return 在线用户ID列表
     */
    List<String> getOnlineUsers(String tenantId);

    /**
     * 检查用户是否在线
     * @param userId 用户ID
     * @param tenantId 租户ID
     * @return 是否在线
     */
    boolean isOnline(String userId, String tenantId);

    /**
     * 踢出用户
     * @param userId 用户ID
     * @param tenantId 租户ID
     */
    void kickout(String userId, String tenantId);
}
