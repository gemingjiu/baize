package com.gem.baize.admin.tenant.mapper;


import com.alibaba.cloud.commons.lang.StringUtils;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gem.baize.admin.tenant.entity.SysTenant;
import com.gem.baize.common.core.model.dto.PageParam;
import org.apache.ibatis.annotations.Mapper;

/**
 * 租户 Mapper 接口
 */
@Mapper
public interface SysTenantMapper extends BaseMapper<SysTenant> {

    default Page<SysTenant> selectPage(PageParam pageParam, SysTenant sysTenant) {
        // 1. 构建分页对象
        Page<SysTenant> page = new Page<>(pageParam.getPageNum(), pageParam.getPageSize());

        // 2. 构建查询条件
        LambdaQueryWrapper<SysTenant> wrapper = new LambdaQueryWrapper<>();
        // 默认排序
        wrapper.orderByAsc(SysTenant::getSort);
        // 动态条件查询
        if (StringUtils.isNotBlank(sysTenant.getTenantName())) {
            wrapper.like(SysTenant::getTenantName, sysTenant.getTenantName());
        }
        if (StringUtils.isNotBlank(sysTenant.getTenantCode())) {
            wrapper.like(SysTenant::getTenantCode, sysTenant.getTenantCode());
        }
        if (StringUtils.isNotBlank(sysTenant.getStatus())) {
            wrapper.eq(SysTenant::getStatus, sysTenant.getStatus());
        }
        if (sysTenant.getCreatedTime() != null) {
            wrapper.ge(SysTenant::getCreatedTime, sysTenant.getCreatedTime());
        }
        // 3. 执行分页查询
        return selectPage(page, wrapper);
    }
}
