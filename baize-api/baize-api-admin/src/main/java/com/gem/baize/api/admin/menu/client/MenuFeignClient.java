package com.gem.baize.api.admin.menu.client;

import com.gem.baize.api.admin.dept.factory.DeptFeignClientFallbackFactory;
import org.springframework.cloud.openfeign.FeignClient;

@FeignClient(path = "/admin",name = "baize-admin", url = "http://localhost:8020",contextId = "MenuFeignClient")
public interface MenuFeignClient {
}
