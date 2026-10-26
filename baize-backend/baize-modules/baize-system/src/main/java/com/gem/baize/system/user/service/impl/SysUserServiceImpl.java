package com.gem.baize.system.user.service.impl;

import com.alibaba.cloud.commons.lang.StringUtils;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
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
    @CacheEvict(cacheNames = "sys_user", key = "#sysUserDto.id")
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
        // 逻辑删除：只把 deleted 置 '1'（status 同步置 '1'），保留行供审计/外键引用。
        // 不调用 super.removeById：MP 的全局 logic-delete 配置对由 DTO 转换的实体无效，会退化成物理 DELETE。
        LambdaUpdateWrapper<SysUser> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(SysUser::getId, id);
        wrapper.eq(SysUser::getDeleted, "0");
        wrapper.set(SysUser::getDeleted, "1");
        wrapper.set(SysUser::getStatus, "1");
        boolean success = this.update(wrapper);
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
    // 这里故意不加 @Cacheable：SysUserDto.password 带 @JsonProperty(access = WRITE_ONLY)，
    // 经 Jackson 序列化进 Redis 后密码字段会丢失；一旦命中缓存，AuthServiceImpl.login
    // 拿到的 DTO 密码为空，会误报「用户名或密码错误」。登录/刷新属低频操作，直接查库更安全。
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
    // 同上：不加 @Cacheable，避免缓存里的 DTO 丢掉密码导致刷新令牌时误判。
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
    public String getPasswordByUsername(String username) {
        LambdaQueryWrapper<SysUser> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysUser::getUserName, username);
        SysUser sysUser = super.getOne(wrapper);
        // 用户不存在时返回 null，由认证服务统一抛出「用户名或密码错误」，避免暴露用户是否存在
        return sysUser == null ? null : sysUser.getPassword();
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
