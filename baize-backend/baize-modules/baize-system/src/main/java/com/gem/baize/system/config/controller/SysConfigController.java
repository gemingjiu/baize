package com.gem.baize.system.config.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gem.baize.api.system.config.domain.dto.SysConfigDto;
import com.gem.baize.common.core.annotation.InnerAuth;
import com.gem.baize.common.core.annotation.Log;
import com.gem.baize.common.core.annotation.RequiresPermission;
import com.gem.baize.common.core.exception.model.ParamException;
import com.gem.baize.common.core.model.vo.ApiResult;
import com.gem.baize.common.core.model.vo.PageResult;
import com.gem.baize.common.database.convert.PageConvert;
import com.gem.baize.system.config.entity.SysConfig;
import com.gem.baize.system.config.service.SysConfigService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.gem.baize.common.core.constant.PermCodes;
@RestController
@RequestMapping("/system/config")
public class SysConfigController {

    @Autowired
    private SysConfigService sysConfigService;

    @GetMapping("/{id}")
    @Operation(summary = "根据ID获取参数配置")
    @RequiresPermission(PermCodes.CONFIG_QUERY)
    public ApiResult<SysConfigDto> getById(@PathVariable String id) {
        if (StringUtils.isBlank(id)) {
            throw new ParamException("请求参数id不能为空");
        }
        return ApiResult.ok(sysConfigService.getById(id));
    }

    @PostMapping
    @Operation(summary = "创建参数配置")
    @RequiresPermission(PermCodes.CONFIG_ADD)
    @Log(title = "参数配置", businessType = Log.BusinessType.INSERT)
    public ApiResult<Integer> create(@Valid @RequestBody SysConfigDto dto) {
        sysConfigService.create(dto);
        return ApiResult.ok();
    }

    @PutMapping("/{id}")
    @Operation(summary = "更新参数配置")
    @RequiresPermission(PermCodes.CONFIG_EDIT)
    @Log(title = "参数配置", businessType = Log.BusinessType.UPDATE)
    public ApiResult<Void> update(@PathVariable String id, @Valid @RequestBody SysConfigDto dto) {
        if (!id.equals(dto.getId())) {
            throw new ParamException("请求参数id不一致");
        }
        sysConfigService.updateById(dto);
        return ApiResult.ok();
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除参数配置")
    @RequiresPermission(PermCodes.CONFIG_REMOVE)
    @Log(title = "参数配置", businessType = Log.BusinessType.DELETE)
    public ApiResult<Void> delete(@PathVariable String id) {
        sysConfigService.removeById(id);
        return ApiResult.ok();
    }

    @PostMapping("/page")
    @Operation(summary = "分页查询参数配置")
    @RequiresPermission(PermCodes.CONFIG_LIST)
    public ApiResult<PageResult<SysConfigDto>> page(@RequestParam(value = "current", defaultValue = "1") int current,
                                               @RequestParam(value = "size", defaultValue = "10") int size,
                                               @Valid @RequestBody SysConfigDto dto) {
        Page<SysConfig> page = new Page<>(current, size);
        PageConvert<SysConfigDto> pageConvert = new PageConvert<>();
        return ApiResult.ok(pageConvert.toDto(sysConfigService.page(page, dto)));
    }

    @GetMapping("/key/{configKey}")
    @Operation(summary = "根据Key获取参数配置")
    @RequiresPermission(PermCodes.CONFIG_QUERY)
    public ApiResult<SysConfigDto> getByConfigKey(@PathVariable String configKey) {
        if (StringUtils.isBlank(configKey)) {
            throw new ParamException("请求参数configKey不能为空");
        }
        return ApiResult.ok(sysConfigService.getByConfigKey(configKey));
    }

    /**
     * 按键读取参数值（仅供服务间调用，2026-11-05 P2 参数消费机制）。
     * <p>
     * {@code @InnerAuth} + 无网关白名单条目，浏览器侧无法直接命中；
     * 服务间调用由 InnerAuthFeignConfig 带上 X-Inner-Token 通过校验（对齐租户 /exists 先例）。
     * 返回裸值：当前租户自定义优先，缺省回落平台兜底；查不到返回 null。
     * 典型消费方：baize-auth 读 sys.account.captchaEnabled 登录验证码开关。
     */
    @GetMapping("/inner/value")
    @Operation(summary = "按键读取参数值", description = "仅供服务间调用：登录验证码开关等配置读取")
    @InnerAuth
    public ApiResult<String> getInnerValue(@RequestParam("key") String configKey) {
        if (StringUtils.isBlank(configKey)) {
            throw new ParamException("参数键不能为空");
        }
        return ApiResult.ok(sysConfigService.getConfigValueByKey(configKey));
    }
}
