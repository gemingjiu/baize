package com.gem.baize.admin.user.service.impl;

import com.gem.baize.admin.user.service.UserService;
import com.gem.baize.api.admin.user.dto.UserDTO;
import com.gem.baize.api.admin.user.vo.UserVO;
import com.gem.baize.common.core.model.vo.PageResult;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {

    @Override
    public UserVO getById(String id) {
        return null;
    }

    @Override
    public Long create(UserDTO dto) {
        return 0L;
    }

    @Override
    public void update(UserDTO dto) {

    }

    @Override
    public void delete(String id) {

    }

    @Override
    public PageResult<UserVO> page(UserDTO query) {
        return null;
    }
}
