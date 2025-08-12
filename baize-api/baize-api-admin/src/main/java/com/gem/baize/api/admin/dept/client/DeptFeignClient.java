package com.gem.baize.api.admin.dept.client;

import com.gem.baize.api.admin.dept.domain.dto.DeptDTO;
import com.gem.baize.api.admin.dept.factory.DeptFeignClientFallbackFactory;
import com.gem.baize.common.core.model.vo.PageResult;
import com.gem.baize.common.core.model.vo.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

@FeignClient(name = "baize-admin", url = "http://localhost:8020",contextId = "DeptFeignClient",fallbackFactory = DeptFeignClientFallbackFactory.class)
public interface DeptFeignClient {
    @RequestMapping(value = "/admin/dept/{bizId}", method = RequestMethod.GET)
    Result<DeptDTO> getById(@PathVariable("bizId") String bizId);

    @RequestMapping(value = "/admin/dept", method = RequestMethod.POST)
    Result<Integer> create(@RequestBody DeptDTO deptDTO);

    @RequestMapping(value = "/admin/dept/{bizId}", method = RequestMethod.PUT)
    Result<Void> update(@PathVariable String bizId, @RequestBody DeptDTO dto);

    @RequestMapping(value = "/admin/dept/{bizId}", method = RequestMethod.DELETE)
    Result<Void> delete(@PathVariable String bizId);

    @RequestMapping(value = "/admin/dept/page")
    Result<PageResult<DeptDTO>> page(@RequestParam("current") int current, @RequestParam("size") int size, @RequestBody DeptDTO dto);
}
