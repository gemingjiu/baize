package com.gem.baize.example.dept.controller;

import com.gem.baize.api.admin.dept.client.DeptFeignClient;
import com.gem.baize.api.admin.dept.domain.dto.DeptDTO;
import com.gem.baize.common.core.model.vo.PageResult;
import com.gem.baize.common.core.model.vo.Result;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/feign/admin/dept")
public class DeptFeignController {

    @Resource
    private DeptFeignClient deptFeignClient;

    @GetMapping("/{bizId}")
    public Result<DeptDTO> getById(@PathVariable String bizId) {
        return deptFeignClient.getById(bizId);
    }

    @PostMapping(value = "/page")
    public Result<PageResult<DeptDTO>> page(@RequestParam(value = "current", defaultValue = "1") int current, @RequestParam(value = "size", defaultValue = "10") int size, @RequestBody DeptDTO dto) {
        return deptFeignClient.page(current, size, dto);
    }
}
