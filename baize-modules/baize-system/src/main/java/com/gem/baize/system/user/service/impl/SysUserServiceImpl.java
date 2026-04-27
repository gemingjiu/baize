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
import org.springframework.util.CollectionUtils;

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
    @CachePut(cacheNames = "sys_user", key = "#sysUserDto.id")
    public void updateById(SysUserDto sysUserDto) {
        SysUser sysUser = sysUserConvert.toEntity(sysUserDto);

        boolean success = super.updateById(sysUser);

        if (!success) {
            throw new NotFoundException("用户不存在或已删除");
        }

    }

    @Override
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

        Page<SysUser> sysUserPage = Optional.ofNullable(super.page(page, wrapper))
                .filter(p -> !CollectionUtils.isEmpty(p.getRecords()))
                .orElseThrow(() -> new NotFoundException("未找到用户信息"));
        return sysUserConvert.toDtoPage(sysUserPage);
    }

    @Override
    @Cacheable(cacheNames = "sys_user", key = "#username")
    public SysUserDto getByUsername(String username) {
        LambdaQueryWrapper<SysUser> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysUser::getUserName, username);
        wrapper.last("LIMIT 1");
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
        wrapper.last("LIMIT 1");
        SysUser sysUser = super.getOne(wrapper);
        if (sysUser == null) {
            throw new NotFoundException("用户名或密码错误");
        }
        return sysUserConvert.toDto(sysUser);
    }

    @Override
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
    @CacheEvict(cacheNames = "sys_user", key = "#userId")
    public void changeStatus(String userId, String status) {
        SysUser sysUser = super.getById(userId);
        if (sysUser == null) {
            throw new NotFoundException("用户不存在");
        }
        sysUser.setStatus(status);
        super.updateById(sysUser);
    }
}
