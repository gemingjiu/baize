package com.gem.baize.api.system.perm.client;

import com.gem.baize.api.system.perm.domain.dto.SysPermDto;
import com.gem.baize.common.core.model.vo.ApiResult;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

@FeignClient(path = "/system", name = "baize-system", url = "${baize.system.url:http://localhost:8020}", contextId = "SysPermFeignClient")
public interface SysPermFeignClient {

    /**
     * 根据ID获取权限
     */
    @GetMapping("/perm/{id}")
    ApiResult<SysPermDto> getById(@PathVariable("id") String id);

    /**
     * 创建权限
     */
    @PostMapping("/perm")
    ApiResult<Integer> create(@RequestBody SysPermDto dto);

    /**
     * 更新权限
     */
    @PutMapping("/perm/{id}")
    ApiResult<Void> update(@PathVariable("id") String id, @RequestBody SysPermDto dto);

    /**
     * 删除权限
     */
    @DeleteMapping("/perm/{id}")
    ApiResult<Void> delete(@PathVariable("id") String id);

    /**
     * 分页查询权限
     */
    @PostMapping("/perm/page")
    ApiResult<com.baomidou.mybatisplus.extension.plugins.pagination.Page<SysPermDto>> page(@RequestParam("current") int current,
                                                                                           @RequestParam("size") int size,
                                                                                           @RequestBody SysPermDto dto);
}
