package com.gem.baize.admin.tenant.controller;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gem.baize.admin.tenant.entity.SysTenant;
import com.gem.baize.admin.tenant.service.SysTenantConvert;
import com.gem.baize.admin.tenant.service.SysTenantService;
import com.gem.baize.api.admin.tenant.domain.dto.SysTenantDto;
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
@RequestMapping("/system/tenant")
public class SysTenantController {

    @Autowired
    private SysTenantService sysTenantService;
    @Autowired
    private SysTenantConvert sysTenantConvert;

    @GetMapping("/{id}")
    @Operation(summary = "根据业务ID获取租户", description = "根据业务ID查询租户信息")
    public Result<SysTenantDto> getById(@PathVariable("id") String id) {
        if (StringUtils.isBlank(id)) {
            throw new BadRequestException("请求参数id不能为空");
        }
        SysTenant sysTenant = sysTenantService.getById(id);
        return Result.success(sysTenantConvert.toDto(sysTenant));
    }

    @PostMapping
    @Operation(summary = "创建租户")
    public Result<Integer> create(@Valid @RequestBody SysTenantDto dto) {
        SysTenant sysTenant = new SysTenant();
        BeanUtils.copyProperties(dto, sysTenant);
        return Result.success(sysTenantService.create(sysTenant));
    }

    @PutMapping("/{id}")
    @Operation(summary = "更新租户")
    public Result<Void> update(@PathVariable String id, @Valid @RequestBody SysTenantDto dto) {
        // 双重验证
        if (!id.equals(dto.getId())) {
            throw new BadRequestException("请求参数id不一致");
        }
        SysTenant sysTenant = new SysTenant();
        BeanUtils.copyProperties(dto, sysTenant);
        sysTenantService.updateById(sysTenant);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除租户")
    public Result<Void> delete(@PathVariable("id") String id) {
        sysTenantService.deleteById(id);
        return Result.success();
    }

    @PostMapping("/search")
    @Operation(summary = "分页查询租户")
    public Result<Page<SysTenantDto>> page(@RequestParam(value = "current", defaultValue = "1") int current, @RequestParam(value = "size", defaultValue = "10") int size, @Valid @RequestBody SysTenantDto dto) {
        PageParam pageParam = new PageParam(current, size);
        SysTenant sysTenant = new SysTenant();
        BeanUtils.copyProperties(dto, sysTenant);
        Page<SysTenant> tenantPage = sysTenantService.page(pageParam, sysTenant);
        Page<SysTenantDto> pageDTO = (Page<SysTenantDto>) tenantPage.convert(
                item -> {
                    SysTenantDto sysTenantDTO = new SysTenantDto();
                    BeanUtils.copyProperties(item, sysTenantDTO);
                    return sysTenantDTO;
                });

        return Result.success(pageDTO);
    }
}
