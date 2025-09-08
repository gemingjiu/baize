package com.gem.baize.admin.user.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gem.baize.admin.user.entity.User;
import com.gem.baize.admin.user.mapper.UserMapper;
import com.gem.baize.admin.user.service.UserService;
import com.gem.baize.common.core.exception.model.DataCreationException;
import com.gem.baize.common.core.exception.model.IntegrityViolationException;
import com.gem.baize.common.core.exception.model.NotFoundException;
import com.gem.baize.common.core.model.dto.PageParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {
    @Autowired
    private UserMapper userMapper;
    @Override
    public User getById(String id) {
        return Optional.ofNullable(userMapper.selectById(Long.valueOf(id))).orElseThrow(() -> new NotFoundException("用户不存在"));

    }

    @Override
    public Integer create(User user) {
        int result = userMapper.insert(user);
        if (result <= 0) {
            throw new DataCreationException("用户创建失败");
        }
        return result;
    }

    @Override
    public void update(User user) {
        int affectedRows = userMapper.updateById(user);

        if (affectedRows <= 0) {
            throw new NotFoundException("用户信息更新失败，记录不存在");
        }

        if (affectedRows > 1) {
            throw new IntegrityViolationException("用户信息更新异常，影响了多条记录");
        }
    }

    @Override
    public void deleteById(String id) {
        int affectedRows = userMapper.deleteById(Long.valueOf(id));
        if (affectedRows <= 0) {
            throw new NotFoundException("用户信息删除失败，可能记录不存在");
        }
        if (affectedRows > 1) {
            throw new IntegrityViolationException("用户信息删除异常，影响了多条记录");
        }
    }

    @Override
    public Page<User> page(PageParam pageParam, User user) {
        return Optional.ofNullable(userMapper.selectPage(pageParam, user))
                .filter(p -> !CollectionUtils.isEmpty(p.getRecords()))
                .orElseThrow(() -> new NotFoundException("未找到用户信息"));
    }
}
