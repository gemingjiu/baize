package com.gem.baize.api.system.tenant.client;

import com.gem.baize.api.system.tenant.domain.dto.SysTenantDto;
import com.gem.baize.common.core.model.vo.ApiResult;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(path = "/system",name = "baize-system", url = "http://localhost:8020",contextId = "SysTenantFeignClient")
public interface SysTenantFeignClient {

    @GetMapping("/tenant/getByTenant")
    ApiResult<SysTenantDto> getByTenantCodeOrDomain(@RequestParam("tenant") String tenant);
}
