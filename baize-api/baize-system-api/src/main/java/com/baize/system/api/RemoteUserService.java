package com.baize.system.api;

/**
 * @author gemj
 * @since 2023/08/22 14:57
 */

import com.baize.common.core.constant.SecurityConstants;
import com.baize.common.core.constant.ServiceNameConstants;
import com.baize.common.core.domain.Response;
import com.baize.system.api.domain.vo.LoginUser;
import com.baize.system.api.domain.SysUser;
import com.baize.system.api.factory.RemoteUserFallbackFactory;
import org.springframework.cloud.loadbalancer.annotation.LoadBalancerClientConfiguration;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

/**
 * 用户服务
 *
 * @author gemj
 */
@FeignClient(contextId = "remoteUserService",configuration = LoadBalancerClientConfiguration.class,value = ServiceNameConstants.SYSTEM_SERVICE, fallbackFactory = RemoteUserFallbackFactory.class)
public interface RemoteUserService {
    /**
     * 通过用户名查询用户信息
     *
     * @param username 用户名
     * @param source   请求来源
     * @return 结果
     */
    @GetMapping("/user/info/{username}")
    public Response<LoginUser> getUserInfo(@PathVariable("username") String username, @RequestHeader(SecurityConstants.FROM_SOURCE) String source);

    /**
     * 注册用户信息
     *
     * @param sysUser 用户信息
     * @param source  请求来源
     * @return 结果
     */
    @PostMapping("/user/register")
    public Response<Boolean> registerUserInfo(@RequestBody SysUser sysUser, @RequestHeader(SecurityConstants.FROM_SOURCE) String source);
}