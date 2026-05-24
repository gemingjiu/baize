package com.gem.baize.api.system.loginlog.client;

import com.gem.baize.api.system.loginlog.domain.dto.SysLoginLogDto;
import com.gem.baize.common.core.model.vo.ApiResult;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(path = "/system", name = "baize-system", url = "http://localhost:8020", contextId = "SysLoginLogFeignClient")
public interface SysLoginLogFeignClient {

    /**
     * 记录登录日志
     */
    @PostMapping("/loginLog/record")
    ApiResult<Void> record(@RequestBody SysLoginLogDto dto);
}
