package com.gem.baize.admin.dept.mapper;

import com.alibaba.cloud.commons.lang.StringUtils;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gem.baize.admin.dept.entity.Dept;
import com.gem.baize.common.core.model.dto.PageParam;

public interface DeptMapper extends BaseMapper<Dept> {
    default Dept selectByBizId(String bizId) {
        LambdaQueryWrapper<Dept> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Dept::getBizId, bizId);
        return selectOne(queryWrapper);
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
