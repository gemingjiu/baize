package com.gem.baize.admin.perm.mapper;

import com.alibaba.cloud.commons.lang.StringUtils;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gem.baize.admin.perm.entity.Perm;
import com.gem.baize.common.core.exception.model.BadRequestException;
import com.gem.baize.common.core.model.dto.PageParam;

import java.time.LocalDateTime;

public interface PermMapper extends BaseMapper<Perm> {
    default Perm selectByBizId(String bizId) {
        LambdaQueryWrapper<Perm> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Perm::getBizId, bizId);
        return selectOne(queryWrapper);
    }


    default int updateByBizId(Perm perm) {
        // 1. 参数校验
        if (perm == null || StringUtils.isBlank(perm.getBizId())) {
            throw new BadRequestException("业务ID不能为空");
        }

        // 2. 构建更新条件
        LambdaUpdateWrapper<Perm> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(Perm::getBizId, perm.getBizId());

        // 继续添加其他需要更新的字段...

        updateWrapper.set(Perm::getModifiedTime, LocalDateTime.now());


        // 5. 执行更新
        return update(updateWrapper);

    }

    default int deleteByBizId(String bizId) {
        LambdaQueryWrapper<Perm> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Perm::getBizId, bizId);
        return delete(queryWrapper);
    }

    default Page<Perm> selectPage(PageParam pageParam, Perm perm) {
        // 1. 构建分页对象
        Page<Perm> page = new Page<>(pageParam.getPageNum(), pageParam.getPageSize());

        // 2. 构建查询条件
        LambdaQueryWrapper<Perm> wrapper = new LambdaQueryWrapper<>();


        if (StringUtils.isNotBlank(perm.getStatus())) {
            wrapper.eq(Perm::getStatus, perm.getStatus());
        }
        if (perm.getCreatedTime() != null) {
            wrapper.ge(Perm::getCreatedTime, perm.getCreatedTime());
        }
        // 3. 执行分页查询
        return selectPage(page, wrapper);
    }
}
