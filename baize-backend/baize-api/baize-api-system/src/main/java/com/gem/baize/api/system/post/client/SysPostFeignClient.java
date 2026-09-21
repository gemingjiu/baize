package com.gem.baize.api.system.post.client;

import com.gem.baize.api.system.post.domain.dto.SysPostDto;
import com.gem.baize.common.core.model.vo.ApiResult;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

@FeignClient(path = "/system", name = "baize-system", url = "${baize.system.url:http://localhost:8020}", contextId = "SysPostFeignClient")
public interface SysPostFeignClient {

    /**
     * 根据ID获取岗位
     */
    @GetMapping("/post/{id}")
    ApiResult<SysPostDto> getById(@PathVariable("id") String id);

    /**
     * 创建岗位
     */
    @PostMapping("/post")
    ApiResult<Integer> create(@RequestBody SysPostDto dto);

    /**
     * 更新岗位
     */
    @PutMapping("/post/{id}")
    ApiResult<Void> update(@PathVariable("id") String id, @RequestBody SysPostDto dto);

    /**
     * 删除岗位
     */
    @DeleteMapping("/post/{id}")
    ApiResult<Void> delete(@PathVariable("id") String id);

    /**
     * 分页查询岗位
     */
    @PostMapping("/post/page")
    ApiResult<com.baomidou.mybatisplus.extension.plugins.pagination.Page<SysPostDto>> page(@RequestParam("current") int current,
                                                                                           @RequestParam("size") int size,
                                                                                           @RequestBody SysPostDto dto);
}
