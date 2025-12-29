package com.gem.baize.api.system.dept.client;

import com.gem.baize.api.system.dept.domain.dto.SysDeptDto;
import com.gem.baize.api.system.dept.factory.SysDeptFeignClientFallbackFactory;
import com.gem.baize.common.core.model.vo.PageResult;
import com.gem.baize.common.core.model.vo.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

@FeignClient(name = "baize-system",path = "/api/system/dept", url = "http://localhost:8020",contextId = "SysDeptFeignClient",fallbackFactory = SysDeptFeignClientFallbackFactory.class)
public interface SysDeptFeignClient {

    @RequestMapping(value = "/create", method = RequestMethod.POST)
    Result<Integer> create(@RequestBody SysDeptDto sysDeptDTO);

    @RequestMapping(value = "/search")
    Result<PageResult<SysDeptDto>> page(@RequestParam("current") int current, @RequestParam("size") int size, @RequestBody SysDeptDto dto);


    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    Result<SysDeptDto> getById(@PathVariable("id") String id);


    @RequestMapping(value = "/{id}", method = RequestMethod.PUT)
    Result<Void> update(@PathVariable String id, @RequestBody SysDeptDto dto);

    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    Result<Void> delete(@PathVariable String id);

}
