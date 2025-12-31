package com.gem.baize.system.tenant.controller;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gem.baize.api.system.tenant.domain.dto.SysTenantDto;
import com.gem.baize.common.core.exception.model.BadRequestException;
import com.gem.baize.common.core.model.vo.Result;
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
    public Result<SysTenantDto> getById(@PathVariable("id") String id) {
        if (StringUtils.isBlank(id)) {
            throw new BadRequestException("请求参数id不能为空");
        }

        return Result.success(sysTenantService.getById(id));
    }

    @PostMapping
    @Operation(summary = "创建租户")
    public Result<Integer> create(@Valid @RequestBody SysTenantDto dto) {

        return Result.success(sysTenantService.create(dto));
    }

    @PutMapping("/{id}")
    @Operation(summary = "更新租户")
    public Result<Void> update(@PathVariable String id, @Valid @RequestBody SysTenantDto dto) {
        // 双重验证
        if (!id.equals(dto.getId())) {
            throw new BadRequestException("请求参数id不一致");
        }

        sysTenantService.updateById(dto);

        return Result.success();
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除租户")
    public Result<Void> delete(@PathVariable("id") String id) {
        sysTenantService.removeById(id);

        return Result.success();
    }

    @PostMapping("/page")
    @Operation(summary = "分页查询租户")
    public Result<Page<SysTenantDto>> page(@RequestParam(value = "pageNum", defaultValue = "1") int pageNum, @RequestParam(value = "pageSize", defaultValue = "10") int pageSize, @Valid @RequestBody SysTenantDto dto) {
        Page<SysTenant> page = new Page<>(pageNum, pageSize);

        return Result.success(sysTenantService.page(page, dto));
    }
}
