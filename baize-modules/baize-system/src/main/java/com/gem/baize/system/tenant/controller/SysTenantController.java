package com.gem.baize.system.tenant.controller;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gem.baize.api.system.tenant.domain.dto.SysTenantDto;
import com.gem.baize.common.core.exception.model.ParamException;
import com.gem.baize.common.core.model.vo.ApiResult;
import com.gem.baize.system.tenant.entity.SysTenant;
import com.gem.baize.system.tenant.service.SysTenantService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Tag(name = "租户管理")
@RestController
@RequestMapping("/system/tenant")
public class SysTenantController {

    @Autowired
    private SysTenantService sysTenantService;


    @GetMapping("/{id}")
    @Operation(summary = "根据业务ID获取租户", description = "根据业务ID查询租户信息")
    public ApiResult<SysTenantDto> getById(@PathVariable("id") String id) {
        if (StringUtils.isBlank(id)) {
            throw new ParamException("请求参数id不能为空");
        }

        return ApiResult.ok(sysTenantService.getById(id));
    }

    @PostMapping
    @Operation(summary = "创建租户")
    public ApiResult<Integer> create(@Valid @RequestBody SysTenantDto dto) {
        sysTenantService.create(dto);
        return ApiResult.ok();
    }

    @PutMapping("/{id}")
    @Operation(summary = "更新租户")
    public ApiResult<Void> update(@PathVariable String id, @Valid @RequestBody SysTenantDto dto) {
        // 双重验证
        if (!id.equals(dto.getId())) {
            throw new ParamException("请求参数id不一致");
        }

        sysTenantService.updateById(dto);
        return ApiResult.ok();
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除租户")
    public ApiResult<Void> delete(@PathVariable("id") String id) {
        sysTenantService.removeById(id);
        return ApiResult.ok();
    }

    @PostMapping("/page")
    @Operation(summary = "分页查询租户")
    public ApiResult<Page<SysTenantDto>> page(@RequestParam(value = "current", defaultValue = "1") int current, @RequestParam(value = "size", defaultValue = "10") int size, @Valid @RequestBody SysTenantDto dto) {
        Page<SysTenant> page = new Page<>(current, size);

        return ApiResult.ok(sysTenantService.page(page, dto));
    }

    @GetMapping("/getByTenant")
    @Operation(summary = "根据租户编码或域名获取租户", description = "根据租户编码或域名查询租户信息")
    public ApiResult<SysTenantDto> getByTenant(@RequestParam("tenant") String tenant) {
        if (StringUtils.isBlank(tenant)) {
            throw new ParamException("租户标识不能为空");
        }
        return ApiResult.ok(sysTenantService.getByTenantCodeOrDomain(tenant));
    }
}
