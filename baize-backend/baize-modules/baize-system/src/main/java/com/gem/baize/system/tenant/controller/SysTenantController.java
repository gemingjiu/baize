package com.gem.baize.system.tenant.controller;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gem.baize.api.system.tenant.domain.dto.SysTenantDto;
import com.gem.baize.common.core.annotation.InnerAuth;
import com.gem.baize.common.core.annotation.RequiresPermission;
import com.gem.baize.common.core.exception.model.ParamException;
import com.gem.baize.common.core.model.vo.ApiResult;
import com.gem.baize.common.core.model.vo.PageResult;
import com.gem.baize.common.database.convert.PageConvert;
import com.gem.baize.system.tenant.entity.SysTenant;
import com.gem.baize.system.tenant.service.SysTenantService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "租户管理")
@RestController
@RequestMapping("/system/tenant")
public class SysTenantController {

    @Autowired
    private SysTenantService sysTenantService;


    @GetMapping("/{id}")
    @Operation(summary = "根据业务ID获取租户", description = "根据业务ID查询租户信息")
    @RequiresPermission("system:tenant:query")
    public ApiResult<SysTenantDto> getById(@PathVariable("id") String id) {
        if (StringUtils.isBlank(id)) {
            throw new ParamException("请求参数id不能为空");
        }

        return ApiResult.ok(sysTenantService.getById(id));
    }

    @PostMapping
    @Operation(summary = "创建租户")
    @RequiresPermission("system:tenant:add")
    public ApiResult<Integer> create(@Valid @RequestBody SysTenantDto dto) {
        sysTenantService.create(dto);
        return ApiResult.ok();
    }

    @PutMapping("/{id}")
    @Operation(summary = "更新租户")
    @RequiresPermission("system:tenant:edit")
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
    @RequiresPermission("system:tenant:remove")
    public ApiResult<Void> delete(@PathVariable("id") String id) {
        sysTenantService.removeById(id);
        return ApiResult.ok();
    }

    @PostMapping("/page")
    @Operation(summary = "分页查询租户")
    @RequiresPermission("system:tenant:list")
    public ApiResult<PageResult<SysTenantDto>> page(@RequestParam(value = "current", defaultValue = "1") int current, @RequestParam(value = "size", defaultValue = "10") int size, @Valid @RequestBody SysTenantDto dto) {
        Page<SysTenant> page = new Page<>(current, size);
        PageConvert<SysTenantDto> pageConvert = new PageConvert<>();

        return ApiResult.ok(pageConvert.toDto(sysTenantService.page(page, dto)));
    }

    @GetMapping("/getByTenant")
    @Operation(summary = "根据租户编码或域名获取租户", description = "根据租户编码或域名查询租户信息")
    @RequiresPermission("system:tenant:query")
    public ApiResult<SysTenantDto> getByTenant(@RequestParam("tenant") String tenant) {
        if (StringUtils.isBlank(tenant)) {
            throw new ParamException("租户标识不能为空");
        }
        return ApiResult.ok(sysTenantService.getByTenantCodeOrDomain(tenant));
    }

    /**
     * 登录页租户下拉框数据源。
     * <p>
     * 故意不加 {@code @RequiresPermission}：登录前没有令牌，网关也不会注入 X-Role，
     * 加了注解会被 PermissionAspect 以「无权访问」拒绝。该路径的匿名放行由网关白名单
     * {@code baize.gateway.whitelist} 中的 {@code /api/v1/system/tenant/list} 控制
     * （见 doc/DEFAULT_GROUP/baize-gateway-dev.yml），因此接口本身只返回租户名称与 ID
     * 这类非敏感字段，不包含联系信息等租户运营数据。
     */
    @GetMapping("/list")
    @Operation(summary = "查询可用租户列表", description = "免鉴权：登录页租户下拉框数据源，只返回正常状态的租户")
    public ApiResult<List<SysTenantDto>> list() {
        return ApiResult.ok(sysTenantService.listAvailable());
    }

    /**
     * 判断租户ID是否存在（仅供 baize-auth 切换租户时校验目标租户）。
     * <p>
     * 加了 {@code @InnerAuth} 但没有网关白名单条目，浏览器侧无法直接命中；
     * 服务间调用由 InnerAuthFeignConfig 带上 X-Inner-Token 通过校验。
     */
    @GetMapping("/exists")
    @Operation(summary = "判断租户是否存在", description = "仅供认证服务切换租户时校验目标租户")
    @InnerAuth
    public ApiResult<Boolean> exists(@RequestParam("id") String id) {
        if (StringUtils.isBlank(id)) {
            throw new ParamException("请求参数id不能为空");
        }
        return ApiResult.ok(sysTenantService.existsById(id));
    }
}
