package com.gem.baize.admin.user.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gem.baize.admin.user.entity.User;
import com.gem.baize.common.core.model.dto.PageParam;

public interface UserService {
    User getById(String id);

    Integer create(User user);

    void update(User user);

    void deleteById(String id);

    Page<User> page(PageParam pageParam, User user);
}
