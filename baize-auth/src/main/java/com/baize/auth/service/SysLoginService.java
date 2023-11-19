package com.baize.auth.service;


import com.baize.common.cache.service.CacheService;
import com.baize.common.core.constant.CacheConstants;
import com.baize.common.core.constant.SecurityConstants;
import com.baize.common.core.constant.UserConstants;
import com.baize.common.core.domain.Response;
import com.baize.common.core.enums.UserStatus;
import com.baize.common.core.exception.BaseException;
import com.baize.common.core.utils.ConvertUtils;
import com.baize.common.core.utils.ip.IpUtils;
import com.baize.common.core.utils.text.StringUtils;
import com.baize.common.security.utils.SecurityUtils;
import com.baize.system.api.RemoteUserService;
import com.baize.system.api.domain.SysUser;
import com.baize.system.api.domain.vo.LoginUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static com.baize.common.core.enums.BaizeExceptionEnum.SERVICE_EXCEPTION;

/**
 * 登录校验
 *
 * @author gemj
 * @since 2023/08/22 11:21
 */
@Component
public class SysLoginService {
    @Autowired(required = false)
    private RemoteUserService remoteUserService;

    @Autowired
    private SysPasswordService sysPasswordService;

    @Autowired
    private CacheService CacheService;

    public LoginUser login(String username, String password) {
        // 用户名或密码为空 错误
        if (StringUtils.isAnyBlank(username, password)) {
            throw new BaseException(SERVICE_EXCEPTION, "用户/密码必须填写");
        }
        // 密码如果不在指定范围内 错误
        if (password.length() < UserConstants.PASSWORD_MIN_LENGTH
                || password.length() > UserConstants.PASSWORD_MAX_LENGTH) {
            throw new BaseException(SERVICE_EXCEPTION, "用户密码不在指定范围");
        }
        // 用户名不在指定范围内 错误
        if (username.length() < UserConstants.USERNAME_MIN_LENGTH
                || username.length() > UserConstants.USERNAME_MAX_LENGTH) {
            throw new BaseException(SERVICE_EXCEPTION, "用户名不在指定范围");
        }
        // IP黑名单校验
        String blackStr = ConvertUtils.toStr(CacheService.getCacheObject(CacheConstants.SYS_LOGIN_BLACKLIST));
        if (IpUtils.isMatchedIp(blackStr, IpUtils.getIpAddr())) {
            throw new BaseException(SERVICE_EXCEPTION, "很遗憾，访问IP已被列入系统黑名单");
        }
        // 查询用户信息
        Response<LoginUser> userResult = remoteUserService.getUserInfo(username, SecurityConstants.INNER);

        if (StringUtils.isNull(userResult) || StringUtils.isNull(userResult.getData())) {
            throw new BaseException(SERVICE_EXCEPTION, "登录用户：" + username + " 不存在");
        }

        if (Response.FAIL == userResult.getCode()) {
            throw new BaseException(SERVICE_EXCEPTION, userResult.getMsg());
        }

        LoginUser userInfo = userResult.getData();
        SysUser user = userResult.getData().getSysUser();
        if (UserStatus.DELETED.getCode().equals(user.getStatus())) {
            throw new BaseException(SERVICE_EXCEPTION, "对不起，您的账号：" + username + " 已被删除");
        }
        if (UserStatus.DISABLE.getCode().equals(user.getStatus())) {
            throw new BaseException(SERVICE_EXCEPTION, "对不起，您的账号：" + username + " 已停用");
        }
        sysPasswordService.validate(user, password);
        return userInfo;
    }

    /**
     * 登出
     */
    public void logout(String username) {
    }

    /**
     * 注册
     */
    public void register(String username, String password) {
        // 用户名或密码为空 错误
        if (StringUtils.isAnyBlank(username, password)) {
            throw new BaseException(SERVICE_EXCEPTION, "用户/密码必须填写");
        }
        if (username.length() < UserConstants.USERNAME_MIN_LENGTH
                || username.length() > UserConstants.USERNAME_MAX_LENGTH) {
            throw new BaseException(SERVICE_EXCEPTION, "账户长度必须在2到20个字符之间");
        }
        if (password.length() < UserConstants.PASSWORD_MIN_LENGTH
                || password.length() > UserConstants.PASSWORD_MAX_LENGTH) {
            throw new BaseException(SERVICE_EXCEPTION, "密码长度必须在5到20个字符之间");
        }

        // 注册用户信息
        SysUser sysUser = new SysUser();
        sysUser.setUserName(username);
        sysUser.setNickName(username);
        sysUser.setPassword(SecurityUtils.encryptPassword(password));
        Response<?> registerResult = remoteUserService.registerUserInfo(sysUser, SecurityConstants.INNER);
        if (Response.FAIL == registerResult.getCode()) {
            throw new BaseException(SERVICE_EXCEPTION, registerResult.getMsg());
        }
    }
}
