package ${dto.packageName}.convert;

import com.gem.baize.common.core.convert.BaseConvert;
import ${dto.packageName}.domain.dto.${dto.className}Dto;
import ${dto.packageName}.entity.${dto.className};
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

/**
 * ${dto.tableComment!dto.tableName} 转换器
 *
 * @author ${dto.author!"baize"}
 */
@Component
public class ${dto.className}Convert extends BaseConvert<${dto.className}, ${dto.className}Dto> {

    @Override
    public ${dto.className}Dto toDto(${dto.className} entity) {
        if (entity == null) {
            return null;
        }
        ${dto.className}Dto dto = new ${dto.className}Dto();
        BeanUtils.copyProperties(entity, dto);
        return dto;
    }

    @Override
    public ${dto.className} toEntity(${dto.className}Dto dto) {
        if (dto == null) {
            return null;
        }
        ${dto.className} entity = new ${dto.className}();
        BeanUtils.copyProperties(dto, entity);
        return entity;
    }
}
