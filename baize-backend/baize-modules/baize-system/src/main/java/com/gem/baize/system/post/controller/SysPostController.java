package com.gem.baize.system.post.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gem.baize.system.post.entity.SysPost;
import com.gem.baize.system.post.service.SysPostService;
import com.gem.baize.api.system.post.domain.dto.SysPostDto;
import com.gem.baize.common.core.annotation.Log;
import com.gem.baize.common.core.annotation.RequiresPermission;
import com.gem.baize.common.core.exception.model.ParamException;
import com.gem.baize.common.core.model.vo.ApiResult;
import com.gem.baize.common.core.model.vo.PageResult;
import com.gem.baize.common.database.convert.PageConvert;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.gem.baize.common.core.constant.PermCodes;
@RestController
@RequestMapping("/system/post")
public class SysPostController {
    @Autowired
    private SysPostService sysPostService;

    /**
     * 岗位选项列表（登录即可见，2026-11-05 P6）。
     * <p>
     * 用户管理表单的岗位多选数据源：有 system:user:edit 权限的人未必有
     * system:post:list，按权限设门槛会拦住用户表单（对齐租户 /list、
     * 字典 /all 先例）。只返回启用中的岗位，租户隔离由行级拦截器保证。
     */
    @GetMapping("/list")
    @Operation(summary = "岗位选项列表", description = "登录即可见：用户表单岗位多选数据源，只返回启用中的岗位")
    public ApiResult<java.util.List<SysPostDto>> list() {
        return ApiResult.ok(sysPostService.listAvailable());
    }

    @GetMapping("/{id}")
    @Operation(summary = "根据ID获取部门", description = "根据ID查询部门信息")
    @RequiresPermission(PermCodes.POST_QUERY)
    public ApiResult<SysPostDto> getById(@PathVariable String id) {
        if (StringUtils.isBlank(id)) {
            throw new ParamException("请求参数id不能为空");
        }
        return ApiResult.ok(sysPostService.getById(id));
    }

    @PostMapping
    @Operation(summary = "创建部门")
    @RequiresPermission(PermCodes.POST_ADD)
    @Log(title = "岗位管理", businessType = Log.BusinessType.INSERT)
    public ApiResult<Integer> create(@Valid @RequestBody SysPostDto dto) {
        sysPostService.create(dto);
        return ApiResult.ok();
    }

    @PutMapping("/{id}")
    @Operation(summary = "更新部门")
    @RequiresPermission(PermCodes.POST_EDIT)
    @Log(title = "岗位管理", businessType = Log.BusinessType.UPDATE)
    public ApiResult<Void> update(@PathVariable String id, @Valid @RequestBody SysPostDto dto) {
        // 双重验证
        if(!id.equals(dto.getId())) {
            throw new ParamException("请求参数id不一致");
        }

        sysPostService.updateById(dto);
        return ApiResult.ok();
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除部门")
    @RequiresPermission(PermCodes.POST_REMOVE)
    @Log(title = "岗位管理", businessType = Log.BusinessType.DELETE)
    public ApiResult<Void> delete(@PathVariable String id) {
        sysPostService.removeById(id);
        return ApiResult.ok();
    }

    @PostMapping("/page")
    @Operation(summary = "分页查询部门")
    @RequiresPermission(PermCodes.POST_LIST)
    public ApiResult<PageResult<SysPostDto>> page(@RequestParam(value = "current", defaultValue = "1") int current, @RequestParam(value = "size", defaultValue = "10") int size, @Valid @RequestBody SysPostDto dto) {
        Page<SysPost> page = new Page<>(current, size);
        PageConvert<SysPostDto> pageConvert = new PageConvert<>();
        return ApiResult.ok(pageConvert.toDto(sysPostService.page(page, dto)));
    }
}
