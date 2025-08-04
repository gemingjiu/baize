package com.gem.baize.admin.tenant.mapper;


import com.alibaba.cloud.commons.lang.StringUtils;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gem.baize.admin.tenant.entity.Tenant;
import com.gem.baize.common.core.exception.BadRequestException;
import com.gem.baize.common.core.model.dto.PageParam;
import org.apache.ibatis.annotations.Mapper;

import java.time.LocalDateTime;

/**
 * 租户 Mapper 接口
 */
@Mapper
public interface TenantMapper extends BaseMapper<Tenant> {
    default Tenant selectByBizId(String bizId) {
        LambdaQueryWrapper<Tenant> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Tenant::getBizId, bizId);
        return selectOne(queryWrapper);
    }


    default int updateByBizId(Tenant tenant) {
        // 1. 参数校验
        if (tenant == null || StringUtils.isBlank(tenant.getBizId())) {
            throw new BadRequestException("业务ID不能为空");
        }

        // 2. 构建更新条件
        LambdaUpdateWrapper<Tenant> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(Tenant::getBizId, tenant.getBizId());

        // 3. 动态设置更新字段（只更新非null字段）
        if (tenant.getTenantName() != null) {
            updateWrapper.set(Tenant::getTenantName, tenant.getTenantName());
        }
        if (tenant.getStatus() != null) {
            updateWrapper.set(Tenant::getStatus, tenant.getStatus());
        }
        if (tenant.getContactPhone() != null) {
            updateWrapper.set(Tenant::getContactPhone, tenant.getContactPhone());
        }
        // 继续添加其他需要更新的字段...

        updateWrapper.set(Tenant::getModifiedTime, LocalDateTime.now());


        // 5. 执行更新
        return update(updateWrapper);

    }

    default int deleteByBizId(String bizId) {
        LambdaQueryWrapper<Tenant> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Tenant::getBizId, bizId);
        return delete(queryWrapper);
    }

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
