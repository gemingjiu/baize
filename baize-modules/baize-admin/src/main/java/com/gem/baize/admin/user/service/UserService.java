package com.gem.baize.admin.user.service;

import com.gem.baize.api.admin.user.dto.UserDTO;
import com.gem.baize.api.admin.user.vo.UserVO;
import com.gem.baize.common.core.model.vo.PageResult;

public interface UserService {
    UserVO getById(String id);

    Long create(UserDTO dto);

    void update(UserDTO dto);

    void delete(String id);

    PageResult<UserVO> page(UserDTO query);
}
