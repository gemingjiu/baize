package com.gem.baize.system.perm.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gem.baize.system.perm.entity.SysPerm;
import com.gem.baize.system.perm.service.SysPermService;
import com.gem.baize.api.system.perm.domain.dto.SysPermDTO;
import com.gem.baize.common.core.exception.model.BadRequestException;
import com.gem.baize.common.core.model.dto.PageParam;
import com.gem.baize.common.core.model.vo.Result;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/system/perm")
public class SysPermController {
    @Autowired
    private SysPermService sysPermService;

    @GetMapping("/{id}")
    @Operation(summary = "根据ID获取权限", description = "根据ID查询权限信息")
    public Result<SysPermDTO> getById(@PathVariable String id) {
        if (StringUtils.isBlank(id)) {
            throw new BadRequestException("请求参数id不能为空");
        }
        SysPerm sysPerm = sysPermService.getById(id);
        SysPermDTO dto = new SysPermDTO();
        BeanUtils.copyProperties(sysPerm, dto);
        return Result.success(dto);
    }

    @PostMapping
    @Operation(summary = "创建权限")
    public Result<Integer> create(@Valid @RequestBody SysPermDTO dto) {
        SysPerm sysPerm = new SysPerm();
        BeanUtils.copyProperties(dto, sysPerm);
        return Result.success(sysPermService.create(sysPerm));
    }

    @PutMapping("/{id}")
    @Operation(summary = "更新权限")
    public Result<Void> update(@PathVariable String id, @Valid @RequestBody SysPermDTO dto) {
        // 双重验证
        if(!id.equals(dto.getId())) {
            throw new BadRequestException("请求参数id不一致");
        }
        SysPerm sysPerm = new SysPerm();
        BeanUtils.copyProperties(dto, sysPerm);
        sysPermService.update(sysPerm);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除权限")
    public Result<Void> delete(@PathVariable String id) {
        sysPermService.deleteById(id);
        return Result.success();
    }

    @PostMapping("/search")
    @Operation(summary = "分页查询权限")
    public Result<Page<SysPermDTO>> page(@RequestParam(value = "current", defaultValue = "1") int current, @RequestParam(value = "size", defaultValue = "10") int size, @Valid @RequestBody SysPermDTO dto) {
        PageParam pageParam = new PageParam(current, size);
        SysPerm sysPerm = new SysPerm();
        BeanUtils.copyProperties(dto, sysPerm);
        Page<SysPerm> page = sysPermService.page(pageParam, sysPerm);
        Page<SysPermDTO> dtoPage = new Page<>();
        BeanUtils.copyProperties(page, dtoPage);
        return Result.success(dtoPage);
    }
}
