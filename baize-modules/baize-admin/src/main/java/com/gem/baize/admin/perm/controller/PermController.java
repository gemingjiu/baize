package com.gem.baize.admin.perm.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gem.baize.admin.perm.entity.Perm;
import com.gem.baize.admin.perm.service.PermService;
import com.gem.baize.api.admin.perm.domain.dto.PermDTO;
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
@RequestMapping("/admin/perm")
public class PermController {
    @Autowired
    private PermService permService;

    @GetMapping("/{id}")
    @Operation(summary = "根据ID获取权限", description = "根据ID查询权限信息")
    public Result<PermDTO> getById(@PathVariable String id) {
        if (StringUtils.isBlank(id)) {
            throw new BadRequestException("请求参数id不能为空");
        }
        Perm perm = permService.getById(id);
        PermDTO dto = new PermDTO();
        BeanUtils.copyProperties(perm, dto);
        return Result.success(dto);
    }

    @PostMapping
    @Operation(summary = "创建权限")
    public Result<Integer> create(@Valid @RequestBody PermDTO dto) {
        Perm perm = new Perm();
        BeanUtils.copyProperties(dto, perm);
        return Result.success(permService.create(perm));
    }

    @PutMapping("/{id}")
    @Operation(summary = "更新权限")
    public Result<Void> update(@PathVariable String id, @Valid @RequestBody PermDTO dto) {
        // 双重验证
        if(!id.equals(dto.getId())) {
            throw new BadRequestException("请求参数id不一致");
        }
        Perm perm = new Perm();
        BeanUtils.copyProperties(dto, perm);
        permService.update(perm);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除权限")
    public Result<Void> delete(@PathVariable String id) {
        permService.deleteById(id);
        return Result.success();
    }

    @PostMapping("/page")
    @Operation(summary = "分页查询权限")
    public Result<Page<PermDTO>> page(@RequestParam(value = "current", defaultValue = "1") int current, @RequestParam(value = "size", defaultValue = "10") int size, @Valid @RequestBody PermDTO dto) {
        PageParam pageParam = new PageParam(current, size);
        Perm perm = new Perm();
        BeanUtils.copyProperties(dto, perm);
        Page<Perm> page = permService.page(pageParam, perm);
        Page<PermDTO> dtoPage = new Page<>();
        BeanUtils.copyProperties(page, dtoPage);
        return Result.success(dtoPage);
    }
}
