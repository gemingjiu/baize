package com.baize.system.api.factory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;

import com.baize.common.core.domain.Response;
import com.baize.system.api.RemoteUserService;
import com.baize.system.api.domain.SysUser;
import com.baize.system.api.domain.vo.LoginUser;


/**
 * @author gemj
 * @since 2023/08/22 14:58
 */

@Component
public class RemoteUserFallbackFactory implements FallbackFactory<RemoteUserService> {
    protected final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Override
    public RemoteUserService create(Throwable throwable) {
        logger.error("用户服务调用失败:{}", throwable.getMessage());
        return new RemoteUserService() {
            @Override
            public Response<LoginUser> getUserInfo(String username, String source) {
                return Response.fail("获取用户失败:" + throwable.getMessage());
            }

            @Override
            public Response<Boolean> registerUserInfo(SysUser sysUser, String source) {
                return Response.fail("注册用户失败:" + throwable.getMessage());
            }
        };
    }
}
