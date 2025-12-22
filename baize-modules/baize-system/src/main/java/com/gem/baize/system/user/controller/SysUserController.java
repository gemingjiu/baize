package com.gem.baize.system.user.controller;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gem.baize.system.user.po.SysUserPo;
import com.gem.baize.system.user.service.SysUserService;
import com.gem.baize.api.system.user.domain.dto.SysUserDTO;
import com.gem.baize.api.system.user.domain.vo.SysUserVO;
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
@RequestMapping("/system/user")
public class SysUserController {

    @Autowired
    private SysUserService sysUserService;

    @GetMapping("/{id}")
    @Operation(summary = "根据ID获取用户", description = "根据ID查询用户信息")
    public Result<SysUserVO> getById(@PathVariable String id) {
        if (StringUtils.isBlank(id)) {
            throw new BadRequestException("请求参数id不能为空");
        }
        SysUserPo sysUserPo = sysUserService.getById(id);
        SysUserVO vo = new SysUserVO();
        BeanUtils.copyProperties(sysUserPo, vo);
        return Result.success(vo);
    }

    @PostMapping
    @Operation(summary = "创建用户")
    public Result<Integer> create(@Valid @RequestBody SysUserDTO dto) {
        SysUserPo sysUserPo = new SysUserPo();
        BeanUtils.copyProperties(dto, sysUserPo);
        return Result.success(sysUserService.create(sysUserPo));
    }

    @PutMapping("/{id}")
    @Operation(summary = "更新用户")
    public Result<Void> update(@PathVariable String id, @Valid @RequestBody SysUserDTO dto) {
        // 双重验证
        if(!id.equals(dto.getId())) {
            throw new BadRequestException("请求参数id不一致");
        }
        SysUserPo sysUserPo = new SysUserPo();
        BeanUtils.copyProperties(dto, sysUserPo);
        sysUserService.update(sysUserPo);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除用户")
    public Result<Void> delete(@PathVariable String id) {
        sysUserService.deleteById(id);
        return Result.success();
    }

    @PostMapping("/search")
    @Operation(summary = "分页查询用户")
    public Result<Page<SysUserVO>> page(@RequestParam(value = "current", defaultValue = "1") int current, @RequestParam(value = "size", defaultValue = "10") int size, @Valid @RequestBody SysUserDTO dto) {
        PageParam pageParam = new PageParam(current, size);
        SysUserPo sysUserPo = new SysUserPo();
        BeanUtils.copyProperties(dto, sysUserPo);
        Page<SysUserPo> page = sysUserService.page(pageParam, sysUserPo);
        Page<SysUserVO> voPage = new Page<>();
        BeanUtils.copyProperties(page, voPage);
        return Result.success(voPage);
    }
}