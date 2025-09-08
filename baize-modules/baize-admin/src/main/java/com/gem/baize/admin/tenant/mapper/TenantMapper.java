package com.gem.baize.admin.tenant.mapper;


import com.alibaba.cloud.commons.lang.StringUtils;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gem.baize.admin.tenant.entity.Tenant;
import com.gem.baize.common.core.model.dto.PageParam;
import org.apache.ibatis.annotations.Mapper;

/**
 * 租户 Mapper 接口
 */
@Mapper
public interface TenantMapper extends BaseMapper<Tenant> {

    default Page<Tenant> selectPage(PageParam pageParam, Tenant tenant) {
        // 1. 构建分页对象
        Page<Tenant> page = new Page<>(pageParam.getPageNum(), pageParam.getPageSize());

        // 2. 构建查询条件
        LambdaQueryWrapper<Tenant> wrapper = new LambdaQueryWrapper<>();

        // 动态条件查询
        if (StringUtils.isNotBlank(tenant.getTenantName())) {
            wrapper.like(Tenant::getTenantName, tenant.getTenantName());
        }
        if (StringUtils.isNotBlank(tenant.getStatus())) {
            wrapper.eq(Tenant::getStatus, tenant.getStatus());
        }
        if (tenant.getCreatedTime() != null) {
            wrapper.ge(Tenant::getCreatedTime, tenant.getCreatedTime());
        }
        // 3. 执行分页查询
        return selectPage(page, wrapper);
    }
}
