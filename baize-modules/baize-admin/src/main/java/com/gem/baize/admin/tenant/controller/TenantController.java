package com.gem.baize.admin.tenant.controller;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gem.baize.admin.tenant.entity.Tenant;
import com.gem.baize.admin.tenant.service.TenantService;
import com.gem.baize.api.admin.tenant.domain.dto.TenantDTO;
import com.gem.baize.common.core.exception.model.BadRequestException;
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

    @GetMapping("/{id}")
    @Operation(summary = "根据业务ID获取租户", description = "根据业务ID查询租户信息")
    public Result<TenantDTO> getById(@PathVariable("id") String id) {
        if (StringUtils.isBlank(id)) {
            throw new BadRequestException("请求参数id不能为空");
        }
        Tenant tenant = TenantService.getById(id);
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

    @PutMapping("/{id}")
    @Operation(summary = "更新租户")
    public Result<Void> update(@PathVariable String id, @Valid @RequestBody TenantDTO dto) {
        // 双重验证
        if (!id.equals(dto.getId())) {
            throw new BadRequestException("请求参数id不一致");
        }
        Tenant tenant = new Tenant();
        BeanUtils.copyProperties(dto, tenant);
        TenantService.updateById(tenant);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除租户")
    public Result<Void> delete(@PathVariable("id") String id) {
        TenantService.deleteById(id);
        return Result.success();
    }

    @PostMapping("/page")
    @Operation(summary = "分页查询租户")
    public Result<Page<TenantDTO>> page(@RequestParam(value = "current", defaultValue = "1") int current, @RequestParam(value = "size", defaultValue = "10") int size, @Valid @RequestBody TenantDTO dto) {
        PageParam pageParam = new PageParam(current, size);
        Tenant tenant = new Tenant();
        BeanUtils.copyProperties(dto, tenant);
        Page<Tenant> tenantPage = TenantService.page(pageParam, tenant);
        Page<TenantDTO> pageDTO = new Page<>();
        BeanUtils.copyProperties(tenantPage, pageDTO);
        return Result.success(pageDTO);
    }
}
