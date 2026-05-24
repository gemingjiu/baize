package ${dto.packageName}.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gem.baize.common.core.model.vo.ApiResult;
import ${dto.packageName}.entity.${dto.className};
import ${dto.packageName}.service.${dto.className}Service;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * ${dto.tableComment!} Controller
 *
 * @author ${dto.author!"baize"}
 */
@Tag(name = "${dto.functionName!dto.tableComment}")
@RestController
@RequestMapping("/${dto.moduleName}/${dto.businessName}")
public class ${dto.className}Controller {

    @Autowired
    private ${dto.className}Service ${dto.className?uncap_first}Service;

    @GetMapping("/list")
    @Operation(summary = "获取${dto.functionName!dto.tableComment}列表")
    public ApiResult<List<${dto.className}>> list(@RequestParam(value = "pageNum", defaultValue = "1") int pageNum,
                                                   @RequestParam(value = "pageSize", defaultValue = "10") int pageSize) {
        Page<${dto.className}> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<${dto.className}> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByAsc(${dto.className}::getCreatedTime);
        return ApiResult.ok(${dto.className?uncap_first}Service.page(page, wrapper).getRecords());
    }

    @GetMapping("/{id}")
    @Operation(summary = "根据ID获取${dto.functionName!dto.tableComment}")
    public ApiResult<${dto.className}> getById(@PathVariable String id) {
        return ApiResult.ok(${dto.className?uncap_first}Service.getById(id));
    }

    @PostMapping
    @Operation(summary = "新增${dto.functionName!dto.tableComment}")
    public ApiResult<Void> create(@RequestBody ${dto.className} ${dto.className?uncap_first}) {
        ${dto.className?uncap_first}Service.save(${dto.className?uncap_first});
        return ApiResult.ok();
    }

    @PutMapping
    @Operation(summary = "修改${dto.functionName!dto.tableComment}")
    public ApiResult<Void> update(@RequestBody ${dto.className} ${dto.className?uncap_first}) {
        ${dto.className?uncap_first}Service.updateById(${dto.className?uncap_first});
        return ApiResult.ok();
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除${dto.functionName!dto.tableComment}")
    public ApiResult<Void> delete(@PathVariable String id) {
        ${dto.className?uncap_first}Service.removeById(id);
        return ApiResult.ok();
    }
}
