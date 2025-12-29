package com.gem.baize.system.tenant.mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.gem.baize.system.tenant.entity.SysTenant;
import org.apache.ibatis.annotations.Mapper;

/**
 * 租户 Mapper 接口
 */
@Mapper
public interface SysTenantMapper extends BaseMapper<SysTenant> {
}
