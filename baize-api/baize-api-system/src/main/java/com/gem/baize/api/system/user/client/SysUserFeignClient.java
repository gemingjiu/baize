package com.gem.baize.api.system.user.client;

import com.gem.baize.api.system.user.domain.dto.SysUserDto;
import com.gem.baize.common.core.model.vo.ApiResult;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(path = "/system",name = "baize-system", url = "http://localhost:8020",contextId = "SysUserFeignClient")
public interface SysUserFeignClient {

    /**
     * 根据用户名查询用户
     */
    @GetMapping("/user/getByUsername")
    ApiResult<SysUserDto> getByUsername(@RequestParam("username") String username);

    /**
     * 根据用户名和租户ID查询用户
     */
    @GetMapping("/user/getByUsernameAndTenant")
    ApiResult<SysUserDto> getByUsernameAndTenantId(@RequestParam("username") String username,
                                                    @RequestParam("tenantId") String tenantId);

    /**
     * 更新用户最后登录时间
     */
    @PutMapping("/user/updateLastLoginTime")
    ApiResult<Void> updateLastLoginTime(@RequestParam("userId") String userId,
                                        @RequestParam(value = "loginIp", required = false) String loginIp);
}
