package com.gem.baize.api.admin.post.client;

import org.springframework.cloud.openfeign.FeignClient;

@FeignClient(path = "/admin",name = "baize-admin", url = "http://localhost:8020",contextId = "PostFeignClient")
public interface PostFeignClient {
}
