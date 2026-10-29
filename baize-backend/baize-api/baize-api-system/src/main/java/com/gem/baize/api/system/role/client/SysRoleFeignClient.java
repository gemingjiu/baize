package com.gem.baize.api.system.role.client;

import com.gem.baize.api.system.role.domain.dto.SysRoleDto;
import com.gem.baize.common.core.model.vo.ApiResult;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@FeignClient(path = "/api/v1/system",name = "baize-system", url = "${baize.system.url:http://localhost:8020}",contextId = "SysRoleFeignClient")
public interface SysRoleFeignClient {

    @GetMapping("/role/user/{userId}")
    ApiResult<SysRoleDto> getRoleByUserId(@PathVariable("userId") String userId);

    /**
     * 查询用户绑定的全部角色（含停用、排除已逻辑删除），供签发时做多角色聚合。
     */
    @GetMapping("/role/user/{userId}/list")
    ApiResult<List<SysRoleDto>> getRolesByUserId(@PathVariable("userId") String userId);
}
