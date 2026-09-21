package ${dto.packageName}.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import ${dto.packageName}.entity.${dto.className};
import ${dto.packageName}.mapper.${dto.className}Mapper;
import ${dto.packageName}.service.${dto.className}Service;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * ${dto.tableComment!} ServiceImpl
 *
 * @author ${dto.author!"baize"}
 */
@Slf4j
@Service
public class ${dto.className}ServiceImpl extends ServiceImpl<${dto.className}Mapper, ${dto.className}> implements ${dto.className}Service {
}
