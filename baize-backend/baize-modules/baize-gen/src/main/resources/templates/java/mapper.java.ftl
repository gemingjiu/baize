package ${dto.packageName}.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.gem.baize.common.core.model.dto.PageParam;
import ${dto.packageName}.entity.${dto.className};
import org.apache.ibatis.annotations.Mapper;

/**
 * ${dto.tableComment!dto.tableName} Mapper
 *
 * @author ${dto.author!"baize"}
 */
@Mapper
public interface ${dto.className}Mapper extends BaseMapper<${dto.className}> {
}
