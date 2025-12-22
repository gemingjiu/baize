package com.gem.baize.example.dept.controller;

import com.gem.baize.api.system.dept.client.SysDeptFeignClient;
import com.gem.baize.api.system.dept.domain.dto.SysDeptDTO;
import com.gem.baize.common.core.model.vo.PageResult;
import com.gem.baize.common.core.model.vo.Result;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/feign/system/dept")
public class DeptFeignController {

    @Resource
    private SysDeptFeignClient sysDeptFeignClient;

    @GetMapping("/{id}")
    public Result<SysDeptDTO> getById(@PathVariable String id) {
        return sysDeptFeignClient.getById(id);
    }

    @PostMapping(value = "/search")
    public Result<PageResult<SysDeptDTO>> page(@RequestParam(value = "current", defaultValue = "1") int current, @RequestParam(value = "size", defaultValue = "10") int size, @RequestBody SysDeptDTO dto) {
        return sysDeptFeignClient.page(current, size, dto);
    }
}
