package com.gem.baize.system.dict.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gem.baize.api.system.dict.domain.dto.SysDictDataDto;
import com.gem.baize.common.core.annotation.Log;
import com.gem.baize.common.core.annotation.RequiresPermission;
import com.gem.baize.common.core.exception.model.ParamException;
import com.gem.baize.common.core.model.vo.ApiResult;
import com.gem.baize.common.core.model.vo.PageResult;
import com.gem.baize.common.database.convert.PageConvert;
import com.gem.baize.system.dict.entity.SysDictData;
import com.gem.baize.system.dict.service.SysDictDataService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

import com.gem.baize.common.core.constant.PermCodes;
@RestController
@RequestMapping("/system/dict/data")
public class SysDictDataController {

    @Autowired
    private SysDictDataService sysDictDataService;

    @GetMapping("/{id}")
    @Operation(summary = "根据ID获取字典数据")
    @RequiresPermission(PermCodes.DICT_QUERY)
    public ApiResult<SysDictDataDto> getById(@PathVariable String id) {
        if (StringUtils.isBlank(id)) {
            throw new ParamException("请求参数id不能为空");
        }
        return ApiResult.ok(sysDictDataService.getById(id));
    }

    @PostMapping
    @Operation(summary = "创建字典数据")
    @RequiresPermission(PermCodes.DICT_ADD)
    @Log(title = "字典数据", businessType = Log.BusinessType.INSERT)
    public ApiResult<Integer> create(@Valid @RequestBody SysDictDataDto dto) {
        sysDictDataService.create(dto);
        return ApiResult.ok();
    }

    @PutMapping("/{id}")
    @Operation(summary = "更新字典数据")
    @RequiresPermission(PermCodes.DICT_EDIT)
    @Log(title = "字典数据", businessType = Log.BusinessType.UPDATE)
    public ApiResult<Void> update(@PathVariable String id, @Valid @RequestBody SysDictDataDto dto) {
        if (!id.equals(dto.getId())) {
            throw new ParamException("请求参数id不一致");
        }
        sysDictDataService.updateById(dto);
        return ApiResult.ok();
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除字典数据")
    @RequiresPermission(PermCodes.DICT_REMOVE)
    @Log(title = "字典数据", businessType = Log.BusinessType.DELETE)
    public ApiResult<Void> delete(@PathVariable String id) {
        sysDictDataService.removeById(id);
        return ApiResult.ok();
    }

    @PostMapping("/page")
    @Operation(summary = "分页查询字典数据")
    @RequiresPermission(PermCodes.DICT_LIST)
    public ApiResult<PageResult<SysDictDataDto>> page(@RequestParam(value = "current", defaultValue = "1") int current,
                                                @RequestParam(value = "size", defaultValue = "10") int size,
                                                @Valid @RequestBody SysDictDataDto dto) {
        Page<SysDictData> page = new Page<>(current, size);
        PageConvert<SysDictDataDto> pageConvert = new PageConvert<>();
        return ApiResult.ok(pageConvert.toDto(sysDictDataService.page(page, dto)));
    }

    /**
     * 批量获取当前租户可见的全部启用字典（key=dict_type，value=合并解析后的数据列表）。
     * <p>
     * 故意不加 {@code @RequiresPermission}：字典是全站表单/表格的展示数据源，普通登录用户
     * 也要在业务页面渲染下拉与回显；按 DICT_QUERY 设门槛会把所有表单页变 403，
     * 字典永远无法生效。该接口只读、只返回启用的展示文本（label/value），不含敏感运营数据。
     * 合并解析：租户自定义项 → 平台内置项兑底（见 SysDictDataServiceImpl#listAll）。
     */
    @GetMapping("/all")
    @Operation(summary = "批量获取当前租户可见的全部启用字典", description = "登录即可见：前端登录后一次性拉取并全局缓存")
    public ApiResult<Map<String, List<SysDictDataDto>>> all() {
        return ApiResult.ok(sysDictDataService.listAll());
    }

    @GetMapping("/type/{dictType}")
    @Operation(summary = "根据字典类型获取字典数据列表（合并解析：租户自定义→平台内置兑底）")
    @RequiresPermission(PermCodes.DICT_QUERY)
    public ApiResult<List<SysDictDataDto>> listByDictType(@PathVariable String dictType) {
        if (StringUtils.isBlank(dictType)) {
            throw new ParamException("字典类型不能为空");
        }
        return ApiResult.ok(sysDictDataService.listByDictType(dictType));
    }
}
