package com.gem.baize.api.admin.dept.client;

import com.gem.baize.api.admin.dept.domain.dto.DeptDTO;
import com.gem.baize.api.admin.dept.factory.DeptFeignClientFallbackFactory;
import com.gem.baize.common.core.model.vo.PageResult;
import com.gem.baize.common.core.model.vo.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

@FeignClient(name = "baize-system",path = "/api/system/dept", url = "http://localhost:8020",contextId = "DeptFeignClient",fallbackFactory = DeptFeignClientFallbackFactory.class)
public interface DeptFeignClient {

    @RequestMapping(value = "/create", method = RequestMethod.POST)
    Result<Integer> create(@RequestBody DeptDTO deptDTO);

    @RequestMapping(value = "/search")
    Result<PageResult<DeptDTO>> page(@RequestParam("current") int current, @RequestParam("size") int size, @RequestBody DeptDTO dto);


    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    Result<DeptDTO> getById(@PathVariable("id") String id);


    @RequestMapping(value = "/{id}", method = RequestMethod.PUT)
    Result<Void> update(@PathVariable String id, @RequestBody DeptDTO dto);

    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    Result<Void> delete(@PathVariable String id);

}
