package com.gem.baize.api.system.perm.client;

import org.springframework.cloud.openfeign.FeignClient;

@FeignClient(path = "/system",name = "baize-system", url = "http://localhost:8020",contextId = "SysPermFeignClient")
public interface SysPermFeignClient {
}
