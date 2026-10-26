package com.gem.baize.example.dept.controller;

import com.gem.baize.api.system.dept.client.SysDeptFeignClient;
import com.gem.baize.api.system.dept.domain.dto.SysDeptDto;
import com.gem.baize.common.core.model.vo.ApiResult;
import com.gem.baize.common.core.model.vo.PageResult;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/feign/system/dept")
public class DeptFeignController {

    @Resource
    private SysDeptFeignClient sysDeptFeignClient;

    @GetMapping("/{id}")
    public ApiResult<SysDeptDto> getById(@PathVariable String id) {
        return sysDeptFeignClient.getById(id);
    }

    /**
     * 分页查询
     * 注意：返回类型必须与 SysDeptFeignClient#page 保持一致（全工程分页出参统一用 PageResult，
     * 避免把 MyBatis-Plus 的 Page 内部结构泄漏到 API 契约）
     */
    @PostMapping(value = "/search")
    public ApiResult<PageResult<SysDeptDto>> page(@RequestParam(value = "current", defaultValue = "1") int current, @RequestParam(value = "size", defaultValue = "10") int size, @RequestBody SysDeptDto dto) {
        return sysDeptFeignClient.page(current, size, dto);
    }
}
