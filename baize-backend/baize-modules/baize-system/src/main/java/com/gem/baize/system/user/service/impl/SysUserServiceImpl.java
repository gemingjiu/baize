package com.gem.baize.system.user.service.impl;

import com.alibaba.cloud.commons.lang.StringUtils;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gem.baize.api.system.user.domain.dto.SysUserDto;
import com.gem.baize.common.core.enums.ErrorCode;
import com.gem.baize.common.core.exception.model.BusinessException;
import com.gem.baize.common.core.exception.model.DuplicateException;
import com.gem.baize.common.core.exception.model.NotFoundException;
import com.gem.baize.common.security.domain.vo.UserVO;
import com.gem.baize.system.user.entity.SysUser;
import com.gem.baize.system.user.mapper.SysUserMapper;
import com.gem.baize.system.user.service.SysUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class SysUserServiceImpl extends ServiceImpl<SysUserMapper, SysUser> implements SysUserService {

    @Autowired
    private SysUserConvert sysUserConvert;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    @Cacheable(cacheNames = "sys_user", key = "#id")
    public SysUserDto getById(String id) {
        SysUser sysUser = Optional.ofNullable(super.getById(id)).orElseThrow(() -> new NotFoundException("用户不存在"));
        return sysUserConvert.toDto(sysUser);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void create(SysUserDto sysUserDto) {
        SysUser sysUser = sysUserConvert.toEntity(sysUserDto);
        // 密码加密
        if (StringUtils.isNotBlank(sysUser.getPassword())) {
            sysUser.setPassword(passwordEncoder.encode(sysUser.getPassword()));
        }
        try {
            super.save(sysUser);
        } catch (DuplicateKeyException e) {
            throw new DuplicateException("用户名称已存在，请更换后重试");
        }
    }


    @Override
    @Transactional(rollbackFor = Exception.class)
    @CachePut(cacheNames = "sys_user", key = "#sysUserDto.id")
    public void updateById(SysUserDto sysUserDto) {
        SysUser sysUser = sysUserConvert.toEntity(sysUserDto);
        boolean success = super.updateById(sysUser);
        if (!success) {
            throw new NotFoundException("用户不存在或已删除");
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @CacheEvict(cacheNames = "sys_user", key = "#id")
    public void removeById(String id) {
        boolean success = super.removeById(id);
        if (!success) {
            throw new NotFoundException("用户不存在或已删除");
        }
    }


    @Override
    public Page<SysUserDto> page(Page<SysUser> page, SysUserDto sysUserDto) {
        // 构建查询条件
        LambdaQueryWrapper<SysUser> wrapper = new LambdaQueryWrapper<>();

        // 默认排序
        wrapper.orderByAsc(SysUser::getSort);

        // 动态条件查询
        if (StringUtils.isNotBlank(sysUserDto.getUserName())) {
            wrapper.like(SysUser::getUserName, sysUserDto.getUserName());
        }
        if (StringUtils.isNotBlank(sysUserDto.getEmail())) {
            wrapper.like(SysUser::getEmail, sysUserDto.getEmail());
        }
        if (StringUtils.isNotBlank(sysUserDto.getPhone())) {
            wrapper.eq(SysUser::getPhone, sysUserDto.getPhone());
        }

        Page<SysUser> sysUserPage = super.page(page, wrapper);
        return sysUserConvert.toDtoPage(sysUserPage);
    }

    @Override
    @Cacheable(cacheNames = "sys_user", key = "#username")
    public SysUserDto getByUsername(String username) {
        LambdaQueryWrapper<SysUser> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysUser::getUserName, username);
        SysUser sysUser = super.getOne(wrapper);
        if (sysUser == null) {
            throw new NotFoundException("用户不存在");
        }
        return sysUserConvert.toDto(sysUser);
    }

    @Override
    @Cacheable(cacheNames = "sys_user", key = "#username + '_' + #tenantId")
    public SysUserDto getByUsernameAndTenantId(String username, String tenantId) {
        LambdaQueryWrapper<SysUser> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysUser::getUserName, username);
        wrapper.eq(SysUser::getTenantId, tenantId);
        SysUser sysUser = super.getOne(wrapper);
        if (sysUser == null) {
            throw new NotFoundException("用户名或密码错误");
        }
        return sysUserConvert.toDto(sysUser);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @CacheEvict(cacheNames = "sys_user", key = "#userId")
    public void updateLastLoginTime(String userId, String loginIp) {
        SysUser sysUser = super.getById(userId);
        if (sysUser == null) {
            throw new NotFoundException("用户不存在");
        }
        sysUser.setLoginDate(LocalDateTime.now());
        sysUser.setLoginIp(loginIp);
        super.updateById(sysUser);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @CacheEvict(cacheNames = "sys_user", key = "#userId")
    public void resetPassword(String userId, String newPassword) {
        SysUser sysUser = super.getById(userId);
        if (sysUser == null) {
            throw new NotFoundException("用户不存在");
        }
        sysUser.setPassword(passwordEncoder.encode(newPassword));
        super.updateById(sysUser);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @CacheEvict(cacheNames = "sys_user", key = "#userId")
    public void updatePassword(String userId, String oldPassword, String newPassword) {
        SysUser sysUser = super.getById(userId);
        if (sysUser == null) {
            throw new NotFoundException("用户不存在");
        }
        if (!passwordEncoder.matches(oldPassword, sysUser.getPassword())) {
            throw new BusinessException(ErrorCode.INVALID_PARAM, "原密码错误");
        }
        sysUser.setPassword(passwordEncoder.encode(newPassword));
        super.updateById(sysUser);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @CacheEvict(cacheNames = "sys_user", key = "#userId")
    public void changeStatus(String userId, String status) {
        SysUser sysUser = super.getById(userId);
        if (sysUser == null) {
            throw new NotFoundException("用户不存在");
        }
        sysUser.setStatus(status);
        super.updateById(sysUser);
    }

    @Override
    public UserVO getCurrentUser(String userId) {
        SysUser sysUser = Optional.ofNullable(super.getById(userId))
                .orElseThrow(() -> new NotFoundException("用户不存在"));
        UserVO userVO = new UserVO();
        userVO.setId(sysUser.getId());
        userVO.setUsername(sysUser.getUserName());
        userVO.setNickname(sysUser.getNickName());
        userVO.setEmail(sysUser.getEmail());
        userVO.setPhone(sysUser.getPhone());
        userVO.setAvatar(sysUser.getAvatar());
        userVO.setTenantId(sysUser.getTenantId());
        return userVO;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @CacheEvict(cacheNames = "sys_user", key = "#userId")
    public void updateProfile(String userId, SysUserDto sysUserDto) {
        SysUser sysUser = super.getById(userId);
        if (sysUser == null) {
            throw new NotFoundException("用户不存在");
        }
        // 只允许修改个人资料字段
        if (StringUtils.isNotBlank(sysUserDto.getNickName())) {
            sysUser.setNickName(sysUserDto.getNickName());
        }
        if (StringUtils.isNotBlank(sysUserDto.getEmail())) {
            sysUser.setEmail(sysUserDto.getEmail());
        }
        if (StringUtils.isNotBlank(sysUserDto.getPhone())) {
            sysUser.setPhone(sysUserDto.getPhone());
        }
        if (StringUtils.isNotBlank(sysUserDto.getAvatar())) {
            sysUser.setAvatar(sysUserDto.getAvatar());
        }
        super.updateById(sysUser);
    }
}
