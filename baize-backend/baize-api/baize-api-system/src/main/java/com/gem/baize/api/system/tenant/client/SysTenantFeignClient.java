package com.gem.baize.api.system.tenant.client;

import com.gem.baize.api.system.tenant.domain.dto.SysTenantDto;
import com.gem.baize.common.core.model.vo.ApiResult;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(path = "/api/v1/system",name = "baize-system", url = "${baize.system.url:http://localhost:8020}",contextId = "SysTenantFeignClient")
public interface SysTenantFeignClient {

    @GetMapping("/tenant/getByTenant")
    ApiResult<SysTenantDto> getByTenantCodeOrDomain(@RequestParam("tenant") String tenant);

    /**
     * 判断租户ID是否存在（切换租户时校验目标租户，仅服务间调用）
     */
    @GetMapping("/tenant/exists")
    ApiResult<Boolean> existsById(@RequestParam("id") String id);

    /**
     * 按租户ID查询租户行（仅服务间调用，2026-11-05 P4）：
     * 登录/切换租户时校验 expire_time 租户过期。
     */
    @GetMapping("/tenant/inner/get")
    ApiResult<SysTenantDto> getById(@RequestParam("id") String id);

    /**
     * 查询可用租户列表（登录页租户下拉框数据源）
     */
    @GetMapping("/tenant/list")
    ApiResult<List<SysTenantDto>> listAvailable();
}
