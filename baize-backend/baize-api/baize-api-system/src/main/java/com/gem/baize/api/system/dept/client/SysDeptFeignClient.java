package com.gem.baize.api.system.dept.client;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gem.baize.api.system.dept.domain.dto.SysDeptDto;
import com.gem.baize.api.system.dept.factory.SysDeptFeignClientFallbackFactory;
import com.gem.baize.common.core.model.vo.ApiResult;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@FeignClient(name = "baize-system", path = "/system", url = "${baize.system.url:http://localhost:8020}", contextId = "SysDeptFeignClient", fallbackFactory = SysDeptFeignClientFallbackFactory.class)
public interface SysDeptFeignClient {

    @PostMapping("/dept")
    ApiResult<Integer> create(@RequestBody SysDeptDto sysDeptDTO);

    @PostMapping("/dept/page")
    ApiResult<Page<SysDeptDto>> page(@RequestParam("current") int current, @RequestParam("size") int size, @RequestBody SysDeptDto dto);

    @GetMapping("/dept/{id}")
    ApiResult<SysDeptDto> getById(@PathVariable("id") String id);

    @PutMapping("/dept/{id}")
    ApiResult<Void> update(@PathVariable("id") String id, @RequestBody SysDeptDto dto);

    @DeleteMapping("/dept/{id}")
    ApiResult<Void> delete(@PathVariable("id") String id);

    @PostMapping("/dept/tree")
    ApiResult<List<SysDeptDto>> tree(@RequestBody(required = false) SysDeptDto dto);

    @GetMapping("/dept/treeselect")
    ApiResult<List<SysDeptDto>> treeselect(@RequestParam(value = "tenantId", required = false) String tenantId);

    @PostMapping("/dept/list")
    ApiResult<List<SysDeptDto>> list(@RequestBody(required = false) SysDeptDto dto);
}
