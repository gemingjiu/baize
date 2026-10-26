package ${dto.packageName}.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import ${dto.packageName}.domain.dto.${dto.className}Dto;
import ${dto.packageName}.entity.${dto.className};

/**
 * ${dto.tableComment!dto.tableName} 服务类接口
 *
 * @author ${dto.author!"baize"}
 */
public interface ${dto.className}Service extends IService<${dto.className}> {

    ${dto.className}Dto getById(String id);

    void create(${dto.className}Dto ${table.classNameLower}Dto);

    void updateById(${dto.className}Dto ${table.classNameLower}Dto);

    void removeById(String id);

    Page<${dto.className}Dto> page(Page<${dto.className}> page, ${dto.className}Dto ${table.classNameLower}Dto);
}
