package ${dto.packageName}.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gem.baize.common.core.annotation.RequiresPermission;
import com.gem.baize.common.core.exception.model.ParamException;
import com.gem.baize.common.core.model.vo.ApiResult;
import com.gem.baize.common.core.model.vo.PageResult;
import com.gem.baize.common.database.convert.PageConvert;
import ${dto.packageName}.domain.dto.${dto.className}Dto;
import ${dto.packageName}.entity.${dto.className};
import ${dto.packageName}.service.${dto.className}Service;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

/**
 * ${dto.tableComment!dto.tableName} Controller
 *
 * @author ${dto.author!"baize"}
 */
@RestController
@RequestMapping("/${dto.moduleName}/${dto.businessName}")
public class ${dto.className}Controller {

    @Autowired
    private ${dto.className}Service ${table.classNameLower}Service;

    @GetMapping("/{id}")
    @Operation(summary = "根据ID获取${dto.functionName!dto.tableComment!dto.tableName}")
    @RequiresPermission("${dto.moduleName}:${dto.businessName}:query")
    public ApiResult<${dto.className}Dto> getById(@PathVariable String id) {
        if (!StringUtils.hasText(id)) {
            throw new ParamException("请求参数id不能为空");
        }
        return ApiResult.ok(${table.classNameLower}Service.getById(id));
    }

    @PostMapping
    @Operation(summary = "创建${dto.functionName!dto.tableComment!dto.tableName}")
    @RequiresPermission("${dto.moduleName}:${dto.businessName}:add")
    public ApiResult<Void> create(@Valid @RequestBody ${dto.className}Dto dto) {
        ${table.classNameLower}Service.create(dto);
        return ApiResult.ok();
    }

    @PutMapping("/{id}")
    @Operation(summary = "更新${dto.functionName!dto.tableComment!dto.tableName}")
    @RequiresPermission("${dto.moduleName}:${dto.businessName}:edit")
    public ApiResult<Void> update(@PathVariable String id, @Valid @RequestBody ${dto.className}Dto dto) {
        if (!id.equals(dto.getId())) {
            throw new ParamException("请求参数id不一致");
        }
        ${table.classNameLower}Service.updateById(dto);
        return ApiResult.ok();
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除${dto.functionName!dto.tableComment!dto.tableName}")
    @RequiresPermission("${dto.moduleName}:${dto.businessName}:remove")
    public ApiResult<Void> delete(@PathVariable String id) {
        ${table.classNameLower}Service.removeById(id);
        return ApiResult.ok();
    }

    @PostMapping("/page")
    @Operation(summary = "分页查询${dto.functionName!dto.tableComment!dto.tableName}")
    @RequiresPermission("${dto.moduleName}:${dto.businessName}:list")
    public ApiResult<PageResult<${dto.className}Dto>> page(
            @RequestParam(value = "current", defaultValue = "1") int current,
            @RequestParam(value = "size", defaultValue = "10") int size,
            @Valid @RequestBody ${dto.className}Dto dto) {
        Page<${dto.className}> page = new Page<>(current, size);
        PageConvert<${dto.className}Dto> pageConvert = new PageConvert<>();
        return ApiResult.ok(pageConvert.toDto(${table.classNameLower}Service.page(page, dto)));
    }
}
