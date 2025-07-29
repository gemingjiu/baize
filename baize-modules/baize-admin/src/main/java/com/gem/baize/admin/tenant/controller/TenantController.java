package com.gem.baize.admin.tenant.controller;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gem.baize.admin.tenant.entity.Tenant;
import com.gem.baize.admin.tenant.service.TenantService;
import com.gem.baize.api.admin.tenant.dto.TenantDTO;
import com.gem.baize.common.core.exception.BadRequestException;
import com.gem.baize.common.core.model.dto.PageParam;
import com.gem.baize.common.core.model.vo.Result;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Tag(name = "租户管理")
@RestController
@RequestMapping("/admin/tenant")
public class TenantController {

    @Autowired
    private TenantService TenantService;

    @GetMapping("/{bizId}")
    @Operation(summary = "根据ID获取租户", description = "根据ID查询租户信息")
    public Result<TenantDTO> getById(@PathVariable("bizId") String bizId) {
        if (StringUtils.isBlank(bizId)) {
            throw new BadRequestException("请求参数bizId不能为空》");
        }
        Tenant tenant = TenantService.getByBizId(bizId);
        TenantDTO dto = new TenantDTO();
        BeanUtils.copyProperties(tenant, dto);
        return Result.success(dto);
    }

    @PostMapping
    @Operation(summary = "创建租户")
    public Result<Integer> create(@Valid @RequestBody TenantDTO dto) {
        Tenant tenant = new Tenant();
        BeanUtils.copyProperties(dto, tenant);
        return Result.success(TenantService.create(tenant));
    }

    @PutMapping("/{bizId}")
    @Operation(summary = "更新租户")
    public Result<Void> update(@Valid @RequestBody TenantDTO dto) {
        Tenant tenant = new Tenant();
        BeanUtils.copyProperties(dto, tenant);
        TenantService.update(tenant);
        return Result.success();
    }

    @DeleteMapping("/{bizId}")
    @Operation(summary = "删除租户")
    public Result<Void> delete(@PathVariable("bizId") String bizId) {
        TenantService.deleteByBizId(bizId);
        return Result.success();
    }

    @GetMapping("/page")
    @Operation(summary = "分页查询租户")
    public Result<Page<Tenant>> page(@RequestParam("current") int current, @RequestParam("size") int size, @Valid @RequestBody TenantDTO dto) {
        PageParam pageParam = new PageParam(current, size);
        Tenant tenant = new Tenant();
        BeanUtils.copyProperties(dto, tenant);
        return Result.success(TenantService.page(pageParam, tenant));
    }

}
