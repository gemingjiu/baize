package com.gem.baize.admin.role.mapper;

import com.alibaba.cloud.commons.lang.StringUtils;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gem.baize.admin.role.entity.Role;
import com.gem.baize.common.core.exception.model.BadRequestException;
import com.gem.baize.common.core.model.dto.PageParam;
import com.gem.baize.common.datasource.entity.BaseEntity;

import java.time.LocalDateTime;

public interface RoleMapper extends BaseMapper<Role> {
    default Role selectByBizId(String bizId) {
        LambdaQueryWrapper<Role> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper
                .eq(Role::getBizId, bizId)
                .orderByAsc(BaseEntity::getSort);
        return selectOne(queryWrapper);
    }


    default int updateByBizId(Role role) {
        // 1. 参数校验
        if (role == null || StringUtils.isBlank(role.getBizId())) {
            throw new BadRequestException("业务ID不能为空");
        }

        // 2. 构建更新条件
        LambdaUpdateWrapper<Role> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(Role::getBizId, role.getBizId());

        // 继续添加其他需要更新的字段...

        updateWrapper.set(Role::getModifiedTime, LocalDateTime.now());


        // 5. 执行更新
        return update(updateWrapper);

    }

    default int deleteByBizId(String bizId) {
        LambdaQueryWrapper<Role> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Role::getBizId, bizId);
        return delete(queryWrapper);
    }

    default Page<Role> selectPage(PageParam pageParam, Role role) {
        // 1. 构建分页对象
        Page<Role> page = new Page<>(pageParam.getPageNum(), pageParam.getPageSize());

        // 2. 构建查询条件
        LambdaQueryWrapper<Role> wrapper = new LambdaQueryWrapper<>();


        if (StringUtils.isNotBlank(role.getStatus())) {
            wrapper.eq(Role::getStatus, role.getStatus());
        }
        if (role.getCreatedTime() != null) {
            wrapper.ge(Role::getCreatedTime, role.getCreatedTime());
        }
        // 3. 执行分页查询
        return selectPage(page, wrapper);
    }
}
