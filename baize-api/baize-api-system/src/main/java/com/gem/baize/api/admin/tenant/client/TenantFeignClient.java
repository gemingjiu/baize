package com.gem.baize.api.admin.tenant.client;

import org.springframework.cloud.openfeign.FeignClient;

@FeignClient(path = "/admin",name = "baize-system", url = "http://localhost:8020",contextId = "TenantFeignClient")
public interface TenantFeignClient {
}
