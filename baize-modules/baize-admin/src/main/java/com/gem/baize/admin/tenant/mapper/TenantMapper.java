package com.gem.baize.admin.tenant.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.gem.baize.api.admin.tenant.entity.Tenant;
import org.apache.ibatis.annotations.Mapper;

/**
 * 租户 Mapper 接口
 */
@Mapper
public interface TenantMapper extends BaseMapper<Tenant> {
}
