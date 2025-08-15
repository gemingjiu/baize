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

    @GetMapping("/{bizId}")
    @Operation(summary = "根据业务ID获取租户", description = "根据业务ID查询租户信息")
    public Result<TenantDTO> getByBizId(@PathVariable("bizId") String bizId) {
        if (StringUtils.isBlank(bizId)) {
            throw new BadRequestException("请求参数bizId不能为空");
        }
        Tenant tenant = TenantService.getByBizId(bizId);
        TenantDTO dto = new TenantDTO();
        BeanUtils.copyProperties(tenant, dto);
        return Result.success(dto);
    }

    @GetMapping("inner/{Id}")
    @Operation(summary = "根据主键ID获取租户", description = "根据主键ID查询租户信息")
    public Result<Tenant> getById(@PathVariable("Id") String id) {
        if (StringUtils.isBlank(id)) {
            throw new BadRequestException("请求参数Id不能为空");
        }
        Tenant tenant = TenantService.getById(id);
        return Result.success(tenant);
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
    public Result<Void> update(@PathVariable String bizId, @Valid @RequestBody TenantDTO dto) {
        // 双重验证
        if(!bizId.equals(dto.getBizId())) {
            throw new BadRequestException("请求参数BizId不一致");
        }
        Tenant tenant = new Tenant();
        BeanUtils.copyProperties(dto, tenant);
        TenantService.updateByBizId(tenant);
        return Result.success();
    }

    @DeleteMapping("/{bizId}")
    @Operation(summary = "删除租户")
    public Result<Void> delete(@PathVariable("bizId") String bizId) {
        TenantService.deleteByBizId(bizId);
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
