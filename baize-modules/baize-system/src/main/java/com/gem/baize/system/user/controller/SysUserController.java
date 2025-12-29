package com.gem.baize.system.user.controller;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gem.baize.api.system.user.domain.dto.SysUserDto;
import com.gem.baize.common.core.exception.model.BadRequestException;
import com.gem.baize.common.core.model.vo.Result;
import com.gem.baize.system.user.entity.SysUser;
import com.gem.baize.system.user.service.SysUserService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/system/user")
public class SysUserController {

    @Autowired
    private SysUserService sysUserService;

    @GetMapping("/{id}")
    @Operation(summary = "根据ID获取用户", description = "根据ID查询用户信息")
    public Result<SysUserDto> getById(@PathVariable String id) {
        if (StringUtils.isBlank(id)) {
            throw new BadRequestException("请求参数id不能为空");
        }
        return Result.success(sysUserService.getById(id));
    }

    @PostMapping
    @Operation(summary = "创建用户")
    public Result<Integer> create(@Valid @RequestBody SysUserDto dto) {
        return Result.success(sysUserService.create(dto));
    }

    @PutMapping("/{id}")
    @Operation(summary = "更新用户")
    public Result<Void> update(@PathVariable String id, @Valid @RequestBody SysUserDto dto) {
        // 双重验证
        if (!id.equals(dto.getId())) {
            throw new BadRequestException("请求参数id不一致");
        }

        sysUserService.update(dto);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除用户")
    public Result<Void> delete(@PathVariable String id) {
        sysUserService.removeById(id);
        return Result.success();
    }

    @PostMapping("/page")
    @Operation(summary = "分页查询用户")
    public Result<Page<SysUserDto>> page(@RequestParam(value = "pageNum", defaultValue = "1") int pageNum, @RequestParam(value = "pageSize", defaultValue = "10") int pageSize, @Valid @RequestBody SysUserDto dto) {
        Page<SysUser> page = new Page<>(pageNum, pageSize);
        return Result.success(sysUserService.page(page, dto));
    }
}