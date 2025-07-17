package com.gem.baize.admin.tenant.controller;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gem.baize.admin.tenant.service.TenantService;
import com.gem.baize.api.admin.tenant.entity.Tenant;
import com.gem.baize.common.core.model.PageParam;
import com.gem.baize.common.core.model.Result;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Tag(name = "租户管理")
@RestController
@RequestMapping("/admin/tenant")
public class TenantController {

    @Autowired
    private TenantService TenantService;

    @GetMapping("/{id}")
    @Operation(summary = "根据ID获取租户", description = "根据ID查询租户信息")
    public Result<Tenant> getById(@PathVariable("id") String id) {
        return Result.success(TenantService.getById(id));
    }

    @PostMapping
    @Operation(summary = "创建租户")
    public Result<Integer> create(@Valid @RequestBody Tenant tenant) {
        return Result.success(TenantService.create(tenant));
    }

    @PutMapping("/{id}")
    @Operation(summary = "更新租户")
    public Result<Void> update(@Valid @RequestBody Tenant tenant) {
        TenantService.update(tenant);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除租户")
    public Result<Void> delete(@PathVariable("id") String id) {
        TenantService.delete(id);
        return Result.success();
    }

    @GetMapping("/page")
    @Operation(summary = "分页查询租户")
    public Result<Page<Tenant>> page(@RequestParam("current") int current, @RequestParam("size") int size,@Valid @RequestBody Tenant tenant) {
        PageParam pageParam = new PageParam(current,size);
        return Result.success(TenantService.page(pageParam, tenant));
    }


}
