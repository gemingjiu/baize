package com.gem.baize.api.admin.perm.client;

import org.springframework.cloud.openfeign.FeignClient;

@FeignClient(path = "/admin",name = "baize-system", url = "http://localhost:8020",contextId = "PermFeignClient")
public interface PermFeignClient {
}
