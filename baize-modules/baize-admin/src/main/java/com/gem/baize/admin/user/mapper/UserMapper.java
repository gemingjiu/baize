package com.gem.baize.admin.user.mapper;


import com.alibaba.cloud.commons.lang.StringUtils;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gem.baize.admin.user.entity.User;
import com.gem.baize.common.core.exception.model.BadRequestException;
import com.gem.baize.common.core.model.dto.PageParam;
import org.apache.ibatis.annotations.Mapper;

import java.time.LocalDateTime;

/**
 * 用户 Mapper 接口
 */
@Mapper
public interface UserMapper extends BaseMapper<User> {
    default User selectByBizId(String bizId) {
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getBizId, bizId);
        return selectOne(queryWrapper);
    }


    default int updateByBizId(User user) {
        // 1. 参数校验
        if (user == null || StringUtils.isBlank(user.getBizId())) {
            throw new BadRequestException("业务ID不能为空");
        }

        // 2. 构建更新条件
        LambdaUpdateWrapper<User> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(User::getBizId, user.getBizId());

        // 继续添加其他需要更新的字段...

        updateWrapper.set(User::getModifiedTime, LocalDateTime.now());


        // 5. 执行更新
        return update(updateWrapper);

    }

    default int deleteByBizId(String bizId) {
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getBizId, bizId);
        return delete(queryWrapper);
    }

    default Page<User> selectPage(PageParam pageParam, User user) {
        // 1. 构建分页对象
        Page<User> page = new Page<>(pageParam.getPageNum(), pageParam.getPageSize());

        // 2. 构建查询条件
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();


        if (StringUtils.isNotBlank(user.getStatus())) {
            wrapper.eq(User::getStatus, user.getStatus());
        }
        if (user.getCreatedTime() != null) {
            wrapper.ge(User::getCreatedTime, user.getCreatedTime());
        }
        // 3. 执行分页查询
        return selectPage(page, wrapper);
    }
}
