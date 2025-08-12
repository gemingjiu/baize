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

    @GetMapping("/{bizId}")
    @Operation(summary = "根据ID获取权限", description = "根据ID查询权限信息")
    public Result<PermDTO> getById(@PathVariable String bizId) {
        if (StringUtils.isBlank(bizId)) {
            throw new BadRequestException("请求参数bizId不能为空》");
        }
        Perm perm = permService.getByBizId(bizId);
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

    @PutMapping("/{bizId}")
    @Operation(summary = "更新权限")
    public Result<Void> update(@PathVariable String bizId, @Valid @RequestBody PermDTO dto) {
        Perm perm = new Perm();
        BeanUtils.copyProperties(dto, perm);
        perm.setBizId(bizId);
        permService.update(perm);
        return Result.success();
    }

    @DeleteMapping("/{bizId}")
    @Operation(summary = "删除权限")
    public Result<Void> delete(@PathVariable String bizId) {
        permService.deleteByBizId(bizId);
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
