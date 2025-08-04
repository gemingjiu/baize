package com.gem.baize.admin.dept.mapper;

import com.alibaba.cloud.commons.lang.StringUtils;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gem.baize.admin.dept.entity.Dept;
import com.gem.baize.common.core.exception.BadRequestException;
import com.gem.baize.common.core.model.dto.PageParam;

import java.time.LocalDateTime;

public interface DeptMapper extends BaseMapper<Dept> {
    default Dept selectByBizId(String bizId) {
        LambdaQueryWrapper<Dept> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Dept::getBizId, bizId);
        return selectOne(queryWrapper);
    }


    default int updateByBizId(Dept dept) {
        // 1. 参数校验
        if (dept == null || StringUtils.isBlank(dept.getBizId())) {
            throw new BadRequestException("业务ID不能为空");
        }

        // 2. 构建更新条件
        LambdaUpdateWrapper<Dept> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(Dept::getBizId, dept.getBizId());

        // 继续添加其他需要更新的字段...

        updateWrapper.set(Dept::getModifiedTime, LocalDateTime.now());


        // 5. 执行更新
        return update(updateWrapper);

    }

    default int deleteByBizId(String bizId) {
        LambdaQueryWrapper<Dept> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Dept::getBizId, bizId);
        return delete(queryWrapper);
    }

    default Page<Dept> selectPage(PageParam pageParam, Dept dept) {
        // 1. 构建分页对象
        Page<Dept> page = new Page<>(pageParam.getPageNum(), pageParam.getPageSize());

        // 2. 构建查询条件
        LambdaQueryWrapper<Dept> wrapper = new LambdaQueryWrapper<>();


        if (StringUtils.isNotBlank(dept.getStatus())) {
            wrapper.eq(Dept::getStatus, dept.getStatus());
        }
        if (dept.getCreatedTime() != null) {
            wrapper.ge(Dept::getCreatedTime, dept.getCreatedTime());
        }
        // 3. 执行分页查询
        return selectPage(page, wrapper);
    }
}
