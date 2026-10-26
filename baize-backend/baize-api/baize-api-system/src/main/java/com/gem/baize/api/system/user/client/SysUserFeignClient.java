package com.gem.baize.api.system.user.client;

import com.gem.baize.api.system.user.domain.dto.SysUserDto;
import com.gem.baize.common.core.model.vo.ApiResult;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(path = "/api/v1/system",name = "baize-system", url = "${baize.system.url:http://localhost:8020}",contextId = "SysUserFeignClient")
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
     * 查询用户密码哈希（仅供认证服务校验口令使用）
     * <p>
     * SysUserDto.password 标注了 @JsonProperty(access = WRITE_ONLY)，序列化时会被丢弃，
     * 因此不能从上面的查询接口拿到密码，必须单独走这个内网接口。
     */
    @GetMapping("/user/getPasswordByUsername")
    ApiResult<String> getPasswordByUsername(@RequestParam("username") String username);

    /**
     * 更新用户最后登录时间
     */
    @PutMapping("/user/updateLastLoginTime")
    ApiResult<Void> updateLastLoginTime(@RequestParam("userId") String userId,
                                        @RequestParam(value = "loginIp", required = false) String loginIp);
}
