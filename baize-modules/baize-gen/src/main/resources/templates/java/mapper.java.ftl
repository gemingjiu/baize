package ${dto.packageName}.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import ${dto.packageName}.entity.${dto.className};
import org.apache.ibatis.annotations.Mapper;

/**
 * ${dto.tableComment!} Mapper
 *
 * @author ${dto.author!"baize"}
 */
@Mapper
public interface ${dto.className}Mapper extends BaseMapper<${dto.className}> {
}
