package com.gem.baize.api.system.role.client;

import com.gem.baize.api.system.role.domain.dto.SysRoleDto;
import com.gem.baize.common.core.model.vo.ApiResult;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(path = "/system",name = "baize-system", url = "http://localhost:8020",contextId = "SysRoleFeignClient")
public interface SysRoleFeignClient {

    @GetMapping("/role/user/{userId}")
    ApiResult<SysRoleDto> getRoleByUserId(@PathVariable("userId") String userId);
}
