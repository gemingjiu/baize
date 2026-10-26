package ${dto.packageName}.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gem.baize.common.core.exception.model.NotFoundException;
import ${dto.packageName}.convert.${dto.className}Convert;
import ${dto.packageName}.domain.dto.${dto.className}Dto;
import ${dto.packageName}.entity.${dto.className};
import ${dto.packageName}.mapper.${dto.className}Mapper;
import ${dto.packageName}.service.${dto.className}Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.Optional;

/**
 * ${dto.tableComment!dto.tableName} 服务类实现
 *
 * @author ${dto.author!"baize"}
 */
@Service
public class ${dto.className}ServiceImpl extends ServiceImpl<${dto.className}Mapper, ${dto.className}> implements ${dto.className}Service {

    @Autowired
    private ${dto.className}Convert ${table.classNameLower}Convert;

    @Override
    public ${dto.className}Dto getById(String id) {
        ${dto.className} entity = Optional.ofNullable(super.getById(id))
                .orElseThrow(() -> new NotFoundException("${dto.functionName!dto.tableComment!dto.tableName}不存在"));
        return ${table.classNameLower}Convert.toDto(entity);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void create(${dto.className}Dto dto) {
        super.save(${table.classNameLower}Convert.toEntity(dto));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateById(${dto.className}Dto dto) {
        boolean success = super.updateById(${table.classNameLower}Convert.toEntity(dto));
        if (!success) {
            throw new NotFoundException("${dto.functionName!dto.tableComment!dto.tableName}不存在或已删除");
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void removeById(String id) {
        // 逻辑删除：只把 deleted 置 '1'（status 同步置 '1'），保留行供审计/外键引用。
        // 不调用 super.removeById：MP 的全局 logic-delete 配置对由 DTO 转换的实体无效，会退化成物理 DELETE。
        LambdaUpdateWrapper<${dto.className}> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(${dto.className}::getId, id);
        wrapper.eq(${dto.className}::getDeleted, "0");
        wrapper.set(${dto.className}::getDeleted, "1");
        wrapper.set(${dto.className}::getStatus, "1");
        boolean success = this.update(wrapper);
        if (!success) {
            throw new NotFoundException("${dto.functionName!dto.tableComment!dto.tableName}不存在或已删除");
        }
    }

    @Override
    public Page<${dto.className}Dto> page(Page<${dto.className}> page, ${dto.className}Dto dto) {
        LambdaQueryWrapper<${dto.className}> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByAsc(${dto.className}::getSort);
<#list table.columns as column>
<#if !column.baseColumn && column.javaType == "String">
        if (dto != null && StringUtils.hasText(dto.get${column.javaField?cap_first}())) {
            wrapper.like(${dto.className}::get${column.javaField?cap_first}, dto.get${column.javaField?cap_first}());
        }
</#if>
</#list>
        return ${table.classNameLower}Convert.toDtoPage(super.page(page, wrapper));
    }
}
