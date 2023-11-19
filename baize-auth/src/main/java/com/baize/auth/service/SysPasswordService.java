package com.baize.auth.service;

import com.baize.common.cache.service.CacheService;
import com.baize.common.core.constant.CacheConstants;
import com.baize.common.core.exception.BaseException;
import com.baize.common.security.utils.SecurityUtils;
import com.baize.system.api.domain.SysUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

import static com.baize.common.core.enums.BaizeExceptionEnum.SERVICE_EXCEPTION;

/**
 * @author gemj
 * @since 2023/08/22 11:23
 */


@Component
public class SysPasswordService {
    @Autowired
    private CacheService cacheService;

    private int maxRetryCount = CacheConstants.PASSWORD_MAX_RETRY_COUNT;

    private Long lockTime = CacheConstants.PASSWORD_LOCK_TIME;




    /**
     * 登录账户密码错误次数缓存键名
     *
     * @param username 用户名
     * @return 缓存键key
     */
    private String getCacheKey(String username) {
        return CacheConstants.PWD_ERR_CNT_KEY + username;
    }

    public void validate(SysUser user, String password) {
        String username = user.getUserName();

        Integer retryCount = cacheService.getCacheObject(getCacheKey(username));

        if (retryCount == null) {
            retryCount = 0;
        }

        if (retryCount >= maxRetryCount) {
            String errMsg = String.format("密码输入错误%s次，帐户锁定%s分钟", maxRetryCount, lockTime);
            throw new BaseException(SERVICE_EXCEPTION, errMsg);
        }

        if (!matches(user, password)) {
            retryCount = retryCount + 1;
            cacheService.setCacheObject(getCacheKey(username), retryCount, lockTime, TimeUnit.MINUTES);
            throw new BaseException(SERVICE_EXCEPTION, "用户不存在/密码错误");
        } else {
            clearLoginRecordCache(username);
        }
    }

    public boolean matches(SysUser user, String rawPassword) {
        return SecurityUtils.matchesPassword(rawPassword, user.getPassword());
    }

    public void clearLoginRecordCache(String loginName) {
        if (cacheService.hasKey(getCacheKey(loginName))) {
            cacheService.deleteObject(getCacheKey(loginName));
        }
    }
}
